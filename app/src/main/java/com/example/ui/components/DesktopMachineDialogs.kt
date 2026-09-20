package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.model.AccountantUser
import com.example.data.model.DesktopStation
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcBrandNavy
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcRedNegative
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SelectDesktopMachineDialog(
    currentMachineCode: String,
    androidDeviceCode: String,
    discoveredStations: List<DesktopStation>,
    onSelectStation: (DesktopStation) -> Unit,
    onSaveCustomMachineCode: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var customCode by remember { mutableStateOf(currentMachineCode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = AcBrandBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mã Máy Desktop Kết Nối",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = AcBrandBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Tránh Xung Đột & Trùng Lặp:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AcBrandBlue
                        )
                        Text(
                            text = "Mỗi máy tính cài A&C Desktop có một Mã máy duy nhất. App sẽ đồng bộ dữ liệu chuẩn xác với đúng máy tính bạn chọn.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = customCode,
                    onValueChange = { customCode = it.uppercase() },
                    label = { Text("Nhập mã máy Desktop của bạn") },
                    placeholder = { Text("Ví dụ: AC-DESKTOP-892A") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_custom_machine_code"),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Devices, contentDescription = null)
                    }
                )

                Text(
                    text = "Hoặc chọn máy tính A&C tìm thấy trên mạng LAN:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(discoveredStations) { station ->
                        val isSelected = station.machineCode.equals(customCode, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    customCode = station.machineCode
                                    onSelectStation(station)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AcBrandBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AcBrandBlue) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = station.machineCode,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) AcBrandBlue else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (station.isOnline) AcGreenPositive else Color.Gray)
                                        )
                                    }
                                    Text(
                                        text = station.stationName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = station.desktopPath,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = Color.Gray,
                                        maxLines = 1
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Đã chọn",
                                        tint = AcBrandBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "Mã định danh điện thoại Android của bạn: $androidDeviceCode",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customCode.isNotBlank()) {
                        onSaveCustomMachineCode(customCode)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Xác Nhận Kết Nối")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountantDialog(
    targetMachineCode: String,
    onRegister: (username: String, fullName: String, role: String, phone: String, email: String, targetMachine: String) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Kế toán bán hàng & công nợ") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var machineCode by remember { mutableStateOf(targetMachineCode) }
    var expandedRole by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val rolesList = listOf(
        "Kế toán trưởng (Quản trị)",
        "Kế toán bán hàng & công nợ",
        "Kế toán kho & vật tư",
        "Kế toán thanh toán & ngân quỹ",
        "Giám đốc / Kiểm toán viên"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = AcBrandBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tạo Tài Khoản Kế Toán Mới",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Tài khoản tạo trên App Android sẽ được đăng ký và đồng bộ trực tiếp lên bản A&C Desktop để quản trị viên theo dõi hoạt động.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        errorText = null
                    },
                    label = { Text("Họ và tên nhân sự") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it.lowercase().trim()
                        errorText = null
                    },
                    label = { Text("Tên đăng nhập (username)") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Badge, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange = { expandedRole = !expandedRole }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vai trò / Chức danh kế toán") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Work, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false }
                    ) {
                        rolesList.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    role = r
                                    expandedRole = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại liên hệ") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email công việc") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = machineCode,
                    onValueChange = { machineCode = it.uppercase() },
                    label = { Text("Mã máy Desktop liên kết") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Computer, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                if (errorText != null) {
                    Text(text = errorText ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank() || username.isBlank()) {
                        errorText = "Vui lòng điền Họ tên và Tên đăng nhập"
                    } else {
                        onRegister(username, fullName, role, phone, email, machineCode)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Tạo & Đăng Ký Lên Desktop")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun ConnectedSessionsDialog(
    currentMachineCode: String,
    accountantUsers: List<AccountantUser>,
    onDeleteAccount: (Long) -> Unit,
    onOpenRegisterDialog: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Monitor,
                    contentDescription = null,
                    tint = AcBrandBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Theo Dõi Tài Khoản Kết Nối",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hiển thị đồng bộ trên Desktop: $currentMachineCode",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Desktop Monitor Simulation Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AcBrandNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "A&C DESKTOP MONITOR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(AcGreenPositive)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ĐANG GIÁM SÁT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcGreenPositive
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bản Desktop tự động ghi nhận danh sách tài khoản Android đang kết nối và mọi hoạt động nghiệp vụ theo thời gian thực.",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tài Khoản Kế Toán Đã Kết Nối (${accountantUsers.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onOpenRegisterDialog) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thêm mới", fontSize = 12.sp)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accountantUsers) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (user.isOnline) AcGreenPositive else Color.Gray)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = user.fullName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Surface(
                                        color = if (user.isOnline) AcGreenPositive.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = if (user.isOnline) "Trực tuyến" else "Ngoại tuyến",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (user.isOnline) AcGreenPositive else Color.DarkGray,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${user.role} (@${user.username})",
                                    fontSize = 11.sp,
                                    color = AcBrandBlue,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Máy: ${user.targetMachineCode} | Thiết bị: ${user.androidDeviceCode}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = dateFormat.format(Date(user.lastActiveTime)),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚡ ${user.lastAction}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    IconButton(
                                        onClick = { onDeleteAccount(user.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Xóa tài khoản",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Đóng")
            }
        }
    )
}
