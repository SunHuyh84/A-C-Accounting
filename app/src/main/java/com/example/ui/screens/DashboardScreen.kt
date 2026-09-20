package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AccountantUser
import com.example.data.model.FinancialSummary
import com.example.data.model.PollingState
import com.example.data.model.SyncConfig
import com.example.data.model.VoucherItem
import com.example.data.model.VoucherType
import com.example.ui.components.AcCompanyLogo
import com.example.ui.components.Formatters
import com.example.ui.components.MetricCard
import com.example.ui.components.SyncStatusBar
import com.example.ui.components.SyncStatusChip
import com.example.ui.components.VoucherTypeBadge
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcBrandNavy
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcPurpleSync
import com.example.ui.theme.AcRedNegative
import com.example.ui.viewmodel.AppTab

@Composable
fun DashboardScreen(
    summary: FinancialSummary,
    syncConfig: SyncConfig,
    isSyncing: Boolean,
    pendingUploadCount: Int,
    recentVouchers: List<VoucherItem>,
    accountantUsers: List<AccountantUser>,
    onSyncClick: () -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    onOpenCreateVoucher: (VoucherType) -> Unit,
    onOpenSelectMachine: () -> Unit,
    onOpenConnectedSessions: () -> Unit,
    customLogoUri: String? = null,
    onCheckDesktopCapabilities: () -> Unit = {},
    pollingState: PollingState = PollingState(),
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Sync & Remote Bridge Status Banner
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SyncStatusBar(
                isSyncing = isSyncing,
                pendingCount = pendingUploadCount,
                lastSyncTime = syncConfig.lastSyncTimestamp,
                onSyncClick = onSyncClick,
                onOpenSyncScreen = { onNavigateTab(AppTab.BRIDGE_SYNC) },
                pollingState = pollingState
            )
        }

        // 2. Executive Hero Banner with Corporate Brand Logo & Target Machine Info
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("executive_hero_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = AcBrandNavy
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Official Corporate Logo (Desktop Match)
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                AcCompanyLogo(
                                    customLogoUri = customLogoUri,
                                    size = 38.dp,
                                    contentDescription = "Logo Công ty A&C"
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "A&C ACCOUNTING DESKTOP",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Báo Cáo Tài Chính Tổng Hợp",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            onClick = onCheckDesktopCapabilities,
                            color = Color(0x3338BDF8),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(Color(0xFF38BDF8))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "${syncConfig.companyCode} • v2.5",
                                    color = Color(0xFFBAE6FD),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Lợi nhuận ròng",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = Formatters.formatCurrency(summary.netProfit),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AcGreenPositive
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Tổng doanh thu (TK 511)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = Formatters.formatCurrency(summary.totalRevenue),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0x22FFFFFF))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenSelectMachine() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Máy: ${syncConfig.targetDesktopMachineCode}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(Đổi)",
                                fontSize = 10.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                        Text(
                            text = "Đồng bộ không trùng máy",
                            style = MaterialTheme.typography.labelSmall,
                            color = AcGreenPositive,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. Desktop Accountant Activity Monitor Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenConnectedSessions() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AcBrandBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Monitor,
                                contentDescription = null,
                                tint = AcBrandBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bản Desktop Giám Sát Hoạt Động",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AcGreenPositive.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${accountantUsers.size} tài khoản",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AcGreenPositive,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Hiển thị người dùng app Android kết nối trên máy ${syncConfig.targetDesktopMachineCode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 4. Grid of Financial Metrics (Tiền mặt, Tiền gửi, Phải thu, Phải trả)
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    title = "Tiền mặt (TK 111)",
                    value = Formatters.formatCurrency(summary.cashOnHand),
                    subtitle = "Tồn quỹ khả dụng",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = AcBrandBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Tiền gửi (TK 112)",
                    value = Formatters.formatCurrency(summary.bankDeposit),
                    subtitle = "Vietcombank & BIDV",
                    icon = Icons.Default.AccountBalance,
                    accentColor = AcGreenPositive,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    title = "Phải thu KH (TK 131)",
                    value = Formatters.formatCurrency(summary.totalReceivable),
                    subtitle = "Công nợ khách hàng",
                    icon = Icons.Default.ArrowDownward,
                    accentColor = AcAmberWarning,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Phải trả NCC (TK 331)",
                    value = Formatters.formatCurrency(summary.totalPayable),
                    subtitle = "Nợ nhà cung cấp",
                    icon = Icons.Default.ArrowUpward,
                    accentColor = AcRedNegative,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Quick Actions
        item {
            Text(
                text = "Tác Vụ Nhanh",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onOpenCreateVoucher(VoucherType.PHIEU_THU) },
                    modifier = Modifier.weight(1f).testTag("quick_action_phieu_thu"),
                    colors = ButtonDefaults.buttonColors(containerColor = AcGreenPositive),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Phiếu Thu", fontSize = 13.sp)
                }

                Button(
                    onClick = { onOpenCreateVoucher(VoucherType.PHIEU_CHI) },
                    modifier = Modifier.weight(1f).testTag("quick_action_phieu_chi"),
                    colors = ButtonDefaults.buttonColors(containerColor = AcRedNegative),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Phiếu Chi", fontSize = 13.sp)
                }

                FilledTonalButton(
                    onClick = { onNavigateTab(AppTab.CUSTOMERS) },
                    modifier = Modifier.weight(1f).testTag("quick_action_partners"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Khách Hàng", fontSize = 13.sp)
                }
            }
        }

        // 6. Recent Accounting Vouchers
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hóa Đơn & Chứng Từ Gần Đây",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateTab(AppTab.INVOICES) }) {
                    Text(text = "Xem tất cả")
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        if (recentVouchers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Chưa có chứng từ nào được ghi nhận", color = Color.Gray)
                    }
                }
            }
        } else {
            items(recentVouchers.take(5)) { voucher ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_voucher_${voucher.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VoucherTypeBadge(type = voucher.voucherType)
                            SyncStatusChip(status = voucher.syncStatus)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = voucher.voucherCode,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = voucher.partnerName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = Formatters.formatCurrency(voucher.amount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (voucher.voucherType in listOf(VoucherType.PHIEU_CHI, VoucherType.BAO_NO)) AcRedNegative else AcGreenPositive
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nợ: ${voucher.debitAccount} | Có: ${voucher.creditAccount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = voucher.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
