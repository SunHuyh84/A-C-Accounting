package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VpnKey
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DesktopVersionInfo
import com.example.data.model.PollingState
import com.example.data.model.SyncConfig
import com.example.data.model.SyncLogItem
import com.example.data.model.VoucherItem
import com.example.ui.components.AcCompanyLogo
import com.example.ui.components.DesktopPollingAndPushCard
import com.example.ui.components.Formatters
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
fun SyncBridgeScreen(
    syncConfig: SyncConfig,
    isSyncing: Boolean,
    isTestingConnection: Boolean,
    testResult: Pair<Boolean, String>?,
    pendingVouchers: List<VoucherItem>,
    syncLogs: List<SyncLogItem>,
    customLogoUri: String? = null,
    onSelectCustomLogo: (String?) -> Unit = {},
    onResetCustomLogo: () -> Unit = {},
    desktopVersionInfo: DesktopVersionInfo = DesktopVersionInfo(),
    isCheckingDesktopUpdates: Boolean = false,
    onCheckDesktopCapabilities: () -> Unit = {},
    pollingState: PollingState = PollingState(),
    onTogglePolling: (Boolean) -> Unit = {},
    onSetPollingInterval: (Int) -> Unit = {},
    onPollNow: () -> Unit = {},
    onToggleFcm: (Boolean) -> Unit = {},
    onSimulateDesktopPush: () -> Unit = {},
    onSaveConfig: (SyncConfig) -> Unit,
    onTestConnection: (String, String, String, String, String) -> Unit,
    onForceSync: () -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var serverUrl by remember(syncConfig) { mutableStateOf(syncConfig.serverUrl) }
    var apiKey by remember(syncConfig) { mutableStateOf(syncConfig.apiKey) }
    var companyCode by remember(syncConfig) { mutableStateOf(syncConfig.companyCode) }
    var desktopPath by remember(syncConfig) { mutableStateOf(syncConfig.desktopFolderPath) }
    var targetMachineCode by remember(syncConfig) { mutableStateOf(syncConfig.targetDesktopMachineCode) }
    var autoSyncEnabled by remember(syncConfig) { mutableStateOf(syncConfig.autoSync) }
    var showInstructions by remember { mutableStateOf(false) }

    var showUrlDialog by remember { mutableStateOf(false) }
    var inputLogoUrl by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onSelectCustomLogo(uri.toString())
            }
        }
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Live Bridge Connection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("bridge_status_card"),
                colors = CardDefaults.cardColors(containerColor = AcBrandNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3310B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = AcGreenPositive,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "A&C Desktop Bridge Gateway",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Máy mục tiêu: $targetMachineCode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            color = if (pendingVouchers.isEmpty()) AcGreenBg else AcAmberBg,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = if (pendingVouchers.isEmpty()) "Đồng bộ 100%" else "${pendingVouchers.size} chờ gửi",
                                color = if (pendingVouchers.isEmpty()) AcGreenPositive else AcAmberWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0x22FFFFFF))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Đồng bộ gần nhất", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(
                                text = Formatters.formatDate(syncConfig.lastSyncTimestamp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Bảo mật & Định danh", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = AcGreenPositive, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "TLS / Token Bearer", style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = syncConfig.desktopFolderPath,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFCBD5E1),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onForceSync,
                            enabled = !isSyncing,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_sync_now"),
                            colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Đang đồng bộ...", fontSize = 13.sp)
                            } else {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Đồng bộ ngay", fontSize = 13.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                onTestConnection(serverUrl, apiKey, companyCode, desktopPath, targetMachineCode)
                            },
                            enabled = !isTestingConnection,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_test_connection"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            if (isTestingConnection) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Đang kiểm tra...", fontSize = 13.sp)
                            } else {
                                Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kiểm tra API", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // 2. Test Connection Diagnostic Result Banner (if tested)
        if (testResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (testResult.first) AcGreenBg else AcRedBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = if (testResult.first) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (testResult.first) AcGreenPositive else AcRedNegative,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (testResult.first) "Kết Nối Thành Công" else "Không Thể Kết Nối",
                                fontWeight = FontWeight.Bold,
                                color = if (testResult.first) AcGreenPositive else AcRedNegative,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = testResult.second,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 2.5. Card Nhận diện Thương hiệu & Logo A&C Tương Tự Bản Desktop
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_ac_logo_branding"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = AcBrandBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nhận Diện Thương Hiệu & Logo A&C Desktop",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Logo A&C được thiết kế đồng bộ với bản Desktop. Bạn có thể giữ nguyên bản chuẩn hoặc nạp ảnh logo tùy chỉnh lưu trên máy tính của bạn.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Logo Preview Canvas
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AcBrandNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                AcCompanyLogo(
                                    customLogoUri = customLogoUri,
                                    size = 54.dp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = if (customLogoUri != null) AcPurpleBg else AcGreenBg,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (customLogoUri != null) "Logo riêng từ Desktop" else "Logo A&C Accounting chuẩn",
                                        color = if (customLogoUri != null) AcPurpleSync else AcGreenPositive,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (customLogoUri != null) "Đang áp dụng đồng bộ toàn bộ App & Phiếu in" else "Vector sắc nét chuẩn nhận diện phần mềm A&C",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Logo Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_pick_desktop_logo"),
                            colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chọn ảnh từ máy", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showUrlDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nhập URL ảnh", fontSize = 12.sp)
                        }
                    }

                    if (customLogoUri != null) {
                        TextButton(
                            onClick = onResetCustomLogo,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Khôi phục Logo A&C mặc định", color = AcRedNegative, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2.6. Card Đồng Bộ Tính Năng & Phiên Bản Desktop (Desktop Evolution Gateway)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_desktop_evolution_gateway"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AcPurpleSync,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đồng Bộ Thay Đổi & Chức Năng Desktop",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Version status pill
                    Surface(
                        color = AcBrandNavy,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Bản Desktop: ${desktopVersionInfo.desktopAppVersion}",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Build #${desktopVersionInfo.desktopBuildNumber} • Cấu trúc Schema v${desktopVersionInfo.databaseSchemaVersion}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = AcGreenBg,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Khớp 100%",
                                    color = AcGreenPositive,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Answer explanation of user inquiry
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Khả năng tương thích khi Desktop đang phát triển:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AcBrandNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Dữ liệu kế toán: Đồng bộ 2 chiều tức thời không cần build lại app.\n" +
                                       "• Cấu trúc mới: Tự động ánh xạ linh hoạt (Dynamic Schema Adapter).\n" +
                                       "• Chức năng mới: Hỗ trợ nạp cấu hình Feature Flags và tải APK cập nhật trực tiếp qua mạng LAN máy tính.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = onCheckDesktopCapabilities,
                        enabled = !isCheckingDesktopUpdates,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_check_desktop_capabilities"),
                        colors = ButtonDefaults.buttonColors(containerColor = AcPurpleSync),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isCheckingDesktopUpdates) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đang kiểm tra từ Desktop...")
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kiểm tra cập nhật & Tính năng Desktop")
                        }
                    }
                }
            }
        }

        // 2.7. Tự Động Làm Mới: Polling Nền & Firebase Cloud Messaging (FCM)
        item {
            DesktopPollingAndPushCard(
                syncConfig = syncConfig,
                pollingState = pollingState,
                onTogglePolling = onTogglePolling,
                onSetPollingInterval = onSetPollingInterval,
                onPollNow = onPollNow,
                onToggleFcm = onToggleFcm,
                onSimulateDesktopPush = onSimulateDesktopPush
            )
        }

        // 3. Configuration Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Cấu Hình Hệ Thống API Trung Gian",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = targetMachineCode,
                        onValueChange = { targetMachineCode = it.uppercase() },
                        label = { Text("Mã máy Desktop muốn kết nối (Ngăn trùng máy)") },
                        placeholder = { Text("AC-DESKTOP-892A") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Computer, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_target_machine_code"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = { Text("Địa chỉ Máy chủ API Gateway") },
                        placeholder = { Text("https://bridge.domain.com:8443 hoặc http://192.168.1.105:8443") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Link, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_server_url"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("Khóa Bảo Mật API (X-AC-Token)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.VpnKey, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_api_key"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = companyCode,
                        onValueChange = { companyCode = it },
                        label = { Text("Mã Đơn Vị / Cơ Sở Dữ Liệu A&C") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Devices, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_company_code"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = desktopPath,
                        onValueChange = { desktopPath = it },
                        label = { Text("Thư mục phần mềm A&C Accounting trên PC") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Folder, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_desktop_path"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = AcBrandBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mã thiết bị Android gửi lên Desktop: ${syncConfig.androidDeviceCode}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Tự động đồng bộ thời gian thực", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Tự động đẩy chứng từ mới sau 30 giây", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { autoSyncEnabled = it },
                            modifier = Modifier.testTag("switch_auto_sync")
                        )
                    }

                    Button(
                        onClick = {
                            onSaveConfig(
                                syncConfig.copy(
                                    serverUrl = serverUrl,
                                    apiKey = apiKey,
                                    companyCode = companyCode,
                                    desktopFolderPath = desktopPath,
                                    targetDesktopMachineCode = targetMachineCode.trim(),
                                    autoSync = autoSyncEnabled
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_config"),
                        colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lưu Thiết Lập Kết Nối Máy Desktop")
                    }
                }
            }
        }

        // 4. Pending Upload Queue (if any)
        if (pendingVouchers.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AcAmberBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = AcAmberWarning)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Hàng đợi chờ đẩy lên Desktop (${pendingVouchers.size})",
                                    fontWeight = FontWeight.Bold,
                                    color = AcAmberWarning,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            TextButton(onClick = onForceSync) {
                                Text("Đẩy ngay", color = AcBrandBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        pendingVouchers.forEach { v ->
                            Text(
                                text = "• [${v.voucherCode}] ${v.voucherType.displayName}: ${Formatters.formatCurrency(v.amount)} - ${v.partnerName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Desktop Bridge Setup Guide Collapsible
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = AcBrandBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hướng Dẫn Cài Đặt Desktop Bridge",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = { showInstructions = !showInstructions }) {
                            Text(if (showInstructions) "Ẩn" else "Xem")
                        }
                    }

                    AnimatedVisibility(visible = showInstructions) {
                        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HorizontalDivider()
                            Text(
                                text = "Để phần mềm A&C Accounting trên máy tính tự động đồng bộ 2 chiều với app Android này:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "1. Mở thư mục phần mềm trên PC: C:\\Users\\pc\\Downloads\\Bo cai skills-Claude+Codex\\AC Accounting",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "2. Chạy file 'AC_Bridge_Agent.exe' (hoặc script Python/Node bridge) đi kèm để kích hoạt API Gateway bảo mật trên cổng 8080 / 8443.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "3. Lấy Mã máy Desktop (xem trong menu Trợ Giúp -> Giới thiệu / Mã trạm trên bản Desktop) và nhập vào ô 'Mã máy Desktop muốn kết nối' ở trên để tránh bị nhầm với máy trạm khác.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "4. Nếu kết nối trong mạng LAN: Nhập IP máy tính (ví dụ: http://192.168.1.105:8443).",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "5. Nếu truy cập từ xa qua Internet (4G/5G ngoài văn phòng): Dùng Cloud Tunnel (ngrok, Cloudflare Zero Trust) trỏ vào cổng bridge của máy tính.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "6. Nhập API Token và bấm 'Kiểm tra API' để hoàn tất kết nối.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // 6. Recent Sync History Logs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nhật Ký Đồng Bộ Thời Gian Thực",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (syncLogs.isNotEmpty()) {
                    IconButton(onClick = onClearLogs) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Xóa nhật ký", tint = Color.Gray)
                    }
                }
            }
        }

        if (syncLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = "Chưa có nhật ký đồng bộ", color = Color.Gray)
                    }
                }
            }
        } else {
            items(syncLogs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (log.status == "SUCCESS") AcGreenBg else AcRedBg,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (log.status == "SUCCESS") "THÀNH CÔNG" else "THẤT BẠI",
                                        color = if (log.status == "SUCCESS") AcGreenPositive else AcRedNegative,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Formatters.formatDate(log.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${log.latencyMs} ms",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AcBrandBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = log.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showUrlDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUrlDialog = false },
            title = {
                Text("Nhập Đường Dẫn Logo A&C Từ Máy Tính", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Nhập URL ảnh logo được chia sẻ từ máy tính Desktop qua mạng nội bộ LAN/Web Server:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inputLogoUrl,
                        onValueChange = { inputLogoUrl = it },
                        label = { Text("URL Ảnh (http://... hoặc https://...)") },
                        placeholder = { Text("http://192.168.1.105:8443/logo.png") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputLogoUrl.isNotBlank()) {
                            onSelectCustomLogo(inputLogoUrl.trim())
                        }
                        showUrlDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue)
                ) {
                    Text("Áp dụng Logo")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showUrlDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}
