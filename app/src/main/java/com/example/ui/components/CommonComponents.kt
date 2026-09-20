package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.SyncStatus
import com.example.data.model.VoucherType
import com.example.ui.theme.AcAmberBg
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcGreenBg
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcPurpleBg
import com.example.ui.theme.AcPurpleSync
import com.example.ui.theme.AcRedBg
import com.example.ui.theme.AcRedNegative
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    fun formatCurrency(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale.GERMAN).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0", symbols)
        return "${formatter.format(amount)} ₫"
    }

    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0) return "Chưa đồng bộ"
        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatShortTime(timestamp: Long): String {
        if (timestamp <= 0) return "--:--"
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun numberToVietnameseWords(amount: Double): String {
        val n = amount.toLong()
        if (n <= 0L) return "Không đồng chẵn"
        val units = arrayOf("", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ")
        val digits = arrayOf("không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín")

        fun readThreeDigits(num: Int, showZeroHundred: Boolean): String {
            val h = num / 100
            val t = (num % 100) / 10
            val u = num % 10
            val sb = StringBuilder()
            if (h > 0 || showZeroHundred) {
                sb.append(digits[h]).append(" trăm ")
            }
            if (t > 1) {
                sb.append(digits[t]).append(" mươi ")
                if (u == 1) sb.append("mốt ")
                else if (u == 5) sb.append("lăm ")
                else if (u > 0) sb.append(digits[u]).append(" ")
            } else if (t == 1) {
                sb.append("mười ")
                if (u == 5) sb.append("lăm ")
                else if (u > 0) sb.append(digits[u]).append(" ")
            } else if (u > 0) {
                if (h > 0 || showZeroHundred) sb.append("lẻ ")
                sb.append(digits[u]).append(" ")
            }
            return sb.toString().trim()
        }

        var temp = n
        var groupIdx = 0
        val parts = mutableListOf<String>()
        var isFirst = true

        while (temp > 0) {
            val three = (temp % 1000).toInt()
            if (three > 0) {
                val words = readThreeDigits(three, !isFirst && temp >= 1000)
                val unit = units.getOrElse(groupIdx) { "" }
                parts.add(0, if (unit.isNotEmpty()) "$words $unit" else words)
            }
            temp /= 1000
            groupIdx++
            isFirst = false
        }

        val result = parts.joinToString(" ").trim().replace("\\s+".toRegex(), " ")
        if (result.isEmpty()) return "Không đồng chẵn"
        val capitalized = result.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return "$capitalized đồng chẵn"
    }
}

@Composable
fun SyncStatusBar(
    isSyncing: Boolean,
    pendingCount: Int,
    lastSyncTime: Long,
    onSyncClick: () -> Unit,
    onOpenSyncScreen: () -> Unit,
    pollingState: com.example.data.model.PollingState = com.example.data.model.PollingState(),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenSyncScreen() }
            .testTag("sync_status_bar"),
        colors = CardDefaults.cardColors(
            containerColor = if (pendingCount > 0) AcAmberBg else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSyncing) AcPurpleBg
                            else if (pendingCount > 0) AcAmberBg
                            else AcGreenBg
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSyncing) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Đang đồng bộ",
                            tint = AcPurpleSync,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(rotation)
                        )
                    } else if (pendingCount > 0) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Chờ đẩy dữ liệu",
                            tint = AcAmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Đã kết nối",
                            tint = AcGreenPositive,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSyncing) "Đang đồng bộ thời gian thực..."
                            else if (pollingState.isHeartbeatBeating) "Đang quét dữ liệu mới từ Desktop..."
                            else if (pendingCount > 0) "Có $pendingCount chứng từ chờ đẩy lên Desktop"
                            else "A&C Desktop: Trực tuyến (Real-time)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingCount > 0) AcAmberWarning else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (pollingState.isPollingActive) "Thăm dò Desktop sau: ${pollingState.nextPollCountdownSeconds}s • FCM Push Sẵn sàng"
                               else "Đồng bộ gần nhất: ${Formatters.formatShortTime(lastSyncTime)} • Chạm để quản lý Bridge",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onSyncClick,
                enabled = !isSyncing,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("force_sync_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Đồng bộ ngay",
                    tint = AcBrandBlue,
                    modifier = if (isSyncing) Modifier.rotate(rotation) else Modifier
                )
            }
        }
    }
}

@Composable
fun SyncStatusChip(status: SyncStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (status) {
        SyncStatus.SYNCED -> Triple(AcGreenBg, AcGreenPositive, Icons.Default.CloudDone)
        SyncStatus.PENDING_UPLOAD -> Triple(AcAmberBg, AcAmberWarning, Icons.Default.CloudUpload)
        SyncStatus.SYNC_ERROR -> Triple(AcRedBg, AcRedNegative, Icons.Default.SyncProblem)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.displayName,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun VoucherTypeBadge(type: VoucherType, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (type) {
        VoucherType.PHIEU_THU, VoucherType.BAO_CO -> Pair(AcGreenBg, AcGreenPositive)
        VoucherType.PHIEU_CHI, VoucherType.BAO_NO -> Pair(AcRedBg, AcRedNegative)
        VoucherType.HOA_DON_BAN -> Pair(Color(0xFFE0F2FE), AcBrandBlue)
        VoucherType.HOA_DON_MUA -> Pair(AcPurpleBg, AcPurpleSync)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = type.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.size(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AcCompanyLogo(
    modifier: Modifier = Modifier,
    customLogoUri: String? = null,
    size: Dp = 48.dp,
    contentDescription: String = "Logo A&C Accounting"
) {
    if (!customLogoUri.isNullOrBlank()) {
        AsyncImage(
            model = customLogoUri,
            contentDescription = contentDescription,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.18f)),
            contentScale = ContentScale.Fit,
            error = painterResource(id = R.drawable.ic_ac_company_logo),
            placeholder = painterResource(id = R.drawable.ic_ac_company_logo)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_ac_company_logo),
            contentDescription = contentDescription,
            modifier = modifier.size(size)
        )
    }
}

