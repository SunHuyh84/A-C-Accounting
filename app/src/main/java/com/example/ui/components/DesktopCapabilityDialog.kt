package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DesktopVersionInfo
import com.example.data.model.SyncConfig
import com.example.ui.theme.AcAmberBg
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcBrandNavy
import com.example.ui.theme.AcGreenBg
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcPurpleBg
import com.example.ui.theme.AcPurpleSync

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesktopCapabilityDialog(
    versionInfo: DesktopVersionInfo,
    syncConfig: SyncConfig,
    customLogoUri: String?,
    isChecking: Boolean,
    onCheckUpdates: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("desktop_capability_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AcCompanyLogo(
                            customLogoUri = customLogoUri,
                            size = 40.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "A&C DESKTOP EVOLUTION",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = AcBrandNavy
                            )
                            Text(
                                text = "Trung Tâm Đồng Bộ Phiên Bản & Tính Năng",
                                style = MaterialTheme.typography.labelSmall,
                                color = AcBrandBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Card 1: Connected Desktop Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AcBrandNavy),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Computer,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Máy Desktop: ${syncConfig.targetDesktopMachineCode}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Surface(
                                color = AcGreenBg,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(AcGreenPositive)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Đang kết nối",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AcGreenPositive
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Phiên bản Desktop", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = versionInfo.desktopAppVersion,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Cấu trúc CSDL (Schema)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "Schema v${versionInfo.databaseSchemaVersion} (Khớp 100%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: Explanation of how desktop updates are synced
                Text(
                    text = "CƠ CHẾ ĐỒNG BỘ KHI NÂNG CẤP DESKTOP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CapabilityItem(
                        icon = Icons.Default.Sync,
                        title = "1. Dữ liệu chứng từ & hóa đơn: Đồng bộ 100% tự động",
                        description = "Mỗi khi bạn thêm, sửa, xóa chứng từ hoặc đối tác trên máy desktop, ứng dụng mobile tự động đồng bộ 2 chiều tức thì mà không cần cài đặt lại.",
                        accentColor = AcBrandBlue
                    )
                    CapabilityItem(
                        icon = Icons.Default.Storage,
                        title = "2. Thích ứng cấu trúc bảng động (Dynamic Schema)",
                        description = "Khi bạn bổ sung cột mới hoặc trường dữ liệu kế toán mới trên bản desktop, mobile tự động ánh xạ mở rộng (Flexible DTO) mà không bị lỗi phiên bản.",
                        accentColor = AcPurpleSync
                    )
                    CapabilityItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "3. Bật/Tắt tính năng theo bản Desktop (Feature Flags)",
                        description = "Desktop gửi danh sách phân hệ đang hoạt động; ứng dụng mobile sẽ tự động kích hoạt hoặc điều chỉnh giao diện tương ứng theo quyền hạn.",
                        accentColor = AcGreenPositive
                    )
                    CapabilityItem(
                        icon = Icons.Default.SystemUpdate,
                        title = "4. Cập nhật Android APK nội bộ từ Desktop (Local OTA)",
                        description = "Khi bạn phát triển các chức năng mới trên desktop đòi hỏi mã nguồn Android mới, máy desktop có thể chia sẻ trực tiếp file APK qua mạng LAN/Wifi để cập nhật không cần qua Store.",
                        accentColor = AcAmberWarning
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Features Grid
                Text(
                    text = "CÁC PHÂN HỆ ĐANG KÍCH HOẠT TỪ MÁY DESKTOP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActiveModuleChip(name = "Chứng từ & Hóa đơn 2 chiều", isEnabled = true)
                    ActiveModuleChip(name = "Sổ cái & Bảng cân đối Desktop", isEnabled = true)
                    ActiveModuleChip(name = "Quản lý đối tác & Công nợ", isEnabled = true)
                    ActiveModuleChip(name = "Kho hàng & Giá vốn tức thời", isEnabled = true)
                    ActiveModuleChip(name = "Hóa đơn điện tử NĐ 123", isEnabled = true)
                    ActiveModuleChip(name = "Báo cáo PnL & Dòng tiền", isEnabled = true)
                    ActiveModuleChip(name = "Kiểm toán & Đối soát trạm", isEnabled = true)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Đóng")
                    }

                    Button(
                        onClick = onCheckUpdates,
                        enabled = !isChecking,
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đang kiểm tra...", fontSize = 13.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kiểm tra Desktop", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CapabilityItem(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ActiveModuleChip(name: String, isEnabled: Boolean) {
    Surface(
        color = if (isEnabled) AcGreenBg else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isEnabled) AcGreenPositive else Color.Gray,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isEnabled) AcGreenPositive else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
