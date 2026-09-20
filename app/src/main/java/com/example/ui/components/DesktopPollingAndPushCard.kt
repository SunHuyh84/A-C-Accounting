package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PollingState
import com.example.data.model.SyncConfig
import com.example.ui.theme.AcAmberBg
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcBrandNavy
import com.example.ui.theme.AcGreenBg
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcPurpleBg
import com.example.ui.theme.AcPurpleSync
import com.example.ui.theme.AcRedBg
import com.example.ui.theme.AcRedNegative

@Composable
fun DesktopPollingAndPushCard(
    syncConfig: SyncConfig,
    pollingState: PollingState,
    onTogglePolling: (Boolean) -> Unit,
    onSetPollingInterval: (Int) -> Unit,
    onPollNow: () -> Unit,
    onToggleFcm: (Boolean) -> Unit,
    onSimulateDesktopPush: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Heartbeat pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_desktop_polling_and_push"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header with Live Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AcBrandBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Làm Mới Tự Động: Polling & Push FCM",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Đồng bộ tức thời dữ liệu khi Desktop có thay đổi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (pollingState.isPollingActive || syncConfig.fcmRealtimeEnabled) AcGreenBg else AcAmberBg,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .scale(if (pollingState.isPollingActive) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(if (pollingState.isPollingActive || syncConfig.fcmRealtimeEnabled) AcGreenPositive else AcAmberWarning)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (pollingState.isPollingActive) "Đang Thăm Dò" else "Tạm Dừng",
                            color = if (pollingState.isPollingActive) AcGreenPositive else AcAmberWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. SECTION A: Auto Polling Engine
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = AcBrandBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "1. Cơ Chế Thăm Dò Định Kỳ (Polling)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AcBrandNavy
                                )
                            }
                            Text(
                                text = "Định kỳ quét máy tính [${syncConfig.targetDesktopMachineCode}]",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = pollingState.isPollingActive,
                            onCheckedChange = onTogglePolling,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AcBrandBlue
                            ),
                            modifier = Modifier.testTag("switch_toggle_polling")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Polling intervals selector chips
                    Text(
                        text = "Tần suất thăm dò Desktop:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val intervals = listOf(5 to "5s", 10 to "10s (Chuẩn)", 30 to "30s", 60 to "60s")
                        intervals.forEach { (sec, label) ->
                            val isSelected = syncConfig.pollingIntervalSeconds == sec
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSetPollingInterval(sec) },
                                color = if (isSelected) AcBrandBlue else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp),
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)) else null
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Diagnostics Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (pollingState.isHeartbeatBeating) AcAmberWarning else AcGreenPositive)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (pollingState.isHeartbeatBeating) "Đang kiểm tra Desktop..." else "Lần quét kế: sau ${pollingState.nextPollCountdownSeconds}s",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = pollingState.lastPollStatus,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }

                            Button(
                                onClick = onPollNow,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_poll_now")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quét ngay", fontSize = 11.sp, color = Color(0xFF38BDF8))
                            }
                        }
                    }

                    // Metrics row
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng quét: ${pollingState.totalPollsPerformed} lượt",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Bản ghi đã nạp mới: ${pollingState.recordsRefreshedCount}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcBrandBlue
                        )
                        Text(
                            text = "Lần cuối: ${Formatters.formatShortTime(pollingState.lastPolledTime)}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. SECTION B: Firebase Cloud Messaging (FCM) Push Channel
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = AcPurpleSync,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2. Kênh Đẩy Tức Thì (Firebase Cloud Messaging)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AcBrandNavy
                                )
                            }
                            Text(
                                text = "Lắng nghe tín hiệu Push khi Desktop phát sinh chứng từ",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = syncConfig.fcmRealtimeEnabled,
                            onCheckedChange = onToggleFcm,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AcPurpleSync
                            ),
                            modifier = Modifier.testTag("switch_toggle_fcm")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // FCM Token Card with Copy Button
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = AcPurpleBg,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "FCM Device Token",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AcPurpleSync,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Topic: ${syncConfig.desktopChangeTopic}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = pollingState.fcmToken.ifEmpty { "fcm_andr_ac_7734_token_active" },
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("FCM Device Token", pollingState.fcmToken)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Đã sao chép Device Token cho máy Desktop!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_copy_fcm_token")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Sao chép token",
                                    tint = AcPurpleSync,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Push status details or last received push banner
                    if (pollingState.lastFcmPushReceivedTime > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AcGreenBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AcGreenPositive,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Nhận Push từ Desktop lúc ${Formatters.formatShortTime(pollingState.lastFcmPushReceivedTime)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AcGreenPositive
                                    )
                                    if (pollingState.lastFcmPayload != null) {
                                        Text(
                                            text = pollingState.lastFcmPayload ?: "",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulation Button
                    Button(
                        onClick = onSimulateDesktopPush,
                        colors = ButtonDefaults.buttonColors(containerColor = AcPurpleSync),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_simulate_desktop_push")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mô Phỏng Nhận Push Từ Desktop [${syncConfig.targetDesktopMachineCode}]",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
