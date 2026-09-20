package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.SyncConfig
import com.example.data.model.VoucherType
import com.example.ui.components.ConnectedSessionsDialog
import com.example.ui.components.DesktopCapabilityDialog
import com.example.ui.components.AcCompanyLogo
import com.example.ui.components.RegisterAccountantDialog
import com.example.ui.components.SelectDesktopMachineDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PartnersScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SyncBridgeScreen
import com.example.ui.screens.VouchersScreen
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcBrandNavy
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcRedNegative
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AccountingViewModel
import com.example.ui.viewmodel.AppTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: AccountingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userSession by viewModel.userSession.collectAsStateWithLifecycle()
    val vouchers by viewModel.vouchers.collectAsStateWithLifecycle()
    val pendingUploadCount by viewModel.pendingUploadCount.collectAsStateWithLifecycle()
    val partners by viewModel.partners.collectAsStateWithLifecycle()
    val accountantUsers by viewModel.accountantUsers.collectAsStateWithLifecycle()
    val financialSummary by viewModel.financialSummary.collectAsStateWithLifecycle()
    val pnlReport by viewModel.pnlReport.collectAsStateWithLifecycle()
    val cashFlowReport by viewModel.cashFlowReport.collectAsStateWithLifecycle()
    val agingDebtReport by viewModel.agingDebtReport.collectAsStateWithLifecycle()
    val syncConfig by viewModel.syncConfig.collectAsStateWithLifecycle()
    val syncLogs by viewModel.recentLogs.collectAsStateWithLifecycle()
    val pollingState by viewModel.pollingState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    // If user is not logged in, show the LoginScreen
    if (!userSession.isLoggedIn) {
        LoginScreen(
            syncConfig = syncConfig,
            onLoginSuccess = { user, pass ->
                viewModel.login(user, pass)
            },
            onOpenServerConfig = {
                viewModel.showServerConfigDialog(true)
            },
            onOpenSelectMachine = {
                viewModel.showSelectMachineDialog(true)
            },
            onOpenRegisterAccount = {
                viewModel.showRegisterAccountantDialog(true)
            },
            customLogoUri = uiState.customLogoUri
        )

        if (uiState.showSelectMachineDialog) {
            SelectDesktopMachineDialog(
                currentMachineCode = syncConfig.targetDesktopMachineCode,
                androidDeviceCode = syncConfig.androidDeviceCode,
                discoveredStations = viewModel.discoveredStations,
                onSelectStation = { station ->
                    viewModel.selectDesktopStation(station)
                },
                onSaveCustomMachineCode = { customCode ->
                    viewModel.setTargetMachineCodeManually(customCode)
                },
                onDismiss = { viewModel.showSelectMachineDialog(false) }
            )
        }

        if (uiState.showRegisterAccountantDialog) {
            RegisterAccountantDialog(
                targetMachineCode = syncConfig.targetDesktopMachineCode,
                onRegister = { u, f, r, p, e, m ->
                    viewModel.createAccountantUser(u, f, r, p, e, m)
                },
                onDismiss = { viewModel.showRegisterAccountantDialog(false) }
            )
        }

        if (uiState.showServerConfigDialog) {
            QuickServerConfigDialog(
                config = syncConfig,
                onDismiss = { viewModel.showServerConfigDialog(false) },
                onSave = { updated ->
                    viewModel.updateConfig(updated)
                    viewModel.showServerConfigDialog(false)
                }
            )
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.showDesktopCapabilityDialog(true) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            // Official Brand Logo in Header
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AcBrandNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                AcCompanyLogo(
                                    customLogoUri = uiState.customLogoUri,
                                    size = 22.dp,
                                    contentDescription = "Logo A&C"
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "A&C ACCOUNTING",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = syncConfig.targetDesktopMachineCode,
                                    color = AcBrandBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = when (uiState.currentTab) {
                                AppTab.DASHBOARD -> "Tổng quan hoạt động"
                                AppTab.INVOICES -> "Quản lý hóa đơn & Chứng từ"
                                AppTab.CUSTOMERS -> "Quản lý khách hàng & Công nợ"
                                AppTab.REPORTS -> "Báo cáo tài chính & Dòng tiền"
                                AppTab.BRIDGE_SYNC -> "Cổng kết nối Desktop Bridge"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.testTag("topbar_user_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Tài khoản",
                            tint = AcBrandBlue
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showConnectedSessionsDialog(true) },
                        modifier = Modifier.testTag("topbar_monitor_sessions")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = AcGreenPositive) {
                                    Text(text = "${accountantUsers.size}")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Monitor,
                                contentDescription = "Theo dõi Desktop",
                                tint = AcBrandBlue
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.triggerSync() },
                        modifier = Modifier.testTag("topbar_sync_action")
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingUploadCount > 0) {
                                    Badge(
                                        containerColor = AcAmberWarning,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$pendingUploadCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Đồng bộ ngay",
                                tint = if (uiState.isSyncing) AcBrandBlue else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                val navItems = listOf(
                    Triple(AppTab.DASHBOARD, "Tổng quan", Icons.Default.Dashboard),
                    Triple(AppTab.INVOICES, "Hóa đơn", Icons.Default.ReceiptLong),
                    Triple(AppTab.CUSTOMERS, "Khách hàng", Icons.Default.People),
                    Triple(AppTab.REPORTS, "Báo cáo", Icons.Default.Assessment),
                    Triple(AppTab.BRIDGE_SYNC, "Đồng bộ", Icons.Default.Sync)
                )

                navItems.forEach { (tab, label, icon) ->
                    val isSelected = uiState.currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            if (tab == AppTab.BRIDGE_SYNC && pendingUploadCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = AcAmberWarning) {
                                            Text(text = "$pendingUploadCount")
                                        }
                                    }
                                ) {
                                    Icon(imageVector = icon, contentDescription = label)
                                }
                            } else {
                                Icon(imageVector = icon, contentDescription = label)
                            }
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AcBrandBlue,
                            selectedTextColor = AcBrandBlue,
                            indicatorColor = Color(0xFFE0F2FE)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = uiState.currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_transition",
            modifier = Modifier.padding(innerPadding)
        ) { targetTab ->
            when (targetTab) {
                AppTab.DASHBOARD -> DashboardScreen(
                    summary = financialSummary,
                    syncConfig = syncConfig,
                    isSyncing = uiState.isSyncing,
                    pendingUploadCount = pendingUploadCount,
                    recentVouchers = vouchers,
                    accountantUsers = accountantUsers,
                    onSyncClick = { viewModel.triggerSync() },
                    onNavigateTab = { viewModel.setTab(it) },
                    onOpenCreateVoucher = { type ->
                        viewModel.setVoucherTypeFilter(type)
                        viewModel.showCreateVoucherDialog(true)
                        viewModel.setTab(AppTab.INVOICES)
                    },
                    onOpenSelectMachine = {
                        viewModel.showSelectMachineDialog(true)
                    },
                    onOpenConnectedSessions = {
                        viewModel.showConnectedSessionsDialog(true)
                    },
                    customLogoUri = uiState.customLogoUri,
                    onCheckDesktopCapabilities = { viewModel.showDesktopCapabilityDialog(true) },
                    pollingState = pollingState
                )

                AppTab.INVOICES -> VouchersScreen(
                    vouchers = vouchers,
                    partners = partners,
                    selectedTypeFilter = uiState.voucherTypeFilter,
                    searchQuery = uiState.searchQuery,
                    onFilterChange = { viewModel.setVoucherTypeFilter(it) },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onDeleteVoucher = { viewModel.deleteVoucher(it) },
                    onUpdateVoucher = { viewModel.updateVoucher(it) },
                    onCreateVoucher = { type, pCode, pName, amount, debit, credit, desc, invNum, invSeries, vatRate ->
                        viewModel.createVoucher(type, pCode, pName, amount, debit, credit, desc, invNum, invSeries, vatRate)
                    },
                    showCreateDialog = uiState.showCreateVoucherDialog,
                    onSetShowCreateDialog = { viewModel.showCreateVoucherDialog(it) },
                    onSyncSingleVoucher = { viewModel.syncSingleVoucher(it) },
                    customLogoUri = uiState.customLogoUri
                )

                AppTab.CUSTOMERS -> PartnersScreen(
                    partners = partners,
                    onCreatePartner = { code, name, phone, address, tax, type, debt, limit ->
                        viewModel.createPartner(code, name, phone, address, tax, type, debt, limit)
                    },
                    onUpdatePartner = { updated ->
                        viewModel.updatePartner(updated)
                    },
                    onDeletePartner = { id ->
                        viewModel.deletePartner(id)
                    }
                )

                AppTab.REPORTS -> ReportsScreen(
                    summary = financialSummary,
                    pnlItems = pnlReport,
                    cashFlowItems = cashFlowReport,
                    agingDebtItems = agingDebtReport,
                    onExportReport = { reportName ->
                        viewModel.testConnection(
                            syncConfig.serverUrl,
                            syncConfig.apiKey,
                            syncConfig.companyCode,
                            syncConfig.desktopFolderPath,
                            syncConfig.targetDesktopMachineCode
                        )
                    }
                )

                AppTab.BRIDGE_SYNC -> SyncBridgeScreen(
                    syncConfig = syncConfig,
                    isSyncing = uiState.isSyncing,
                    isTestingConnection = uiState.isTestingConnection,
                    testResult = uiState.connectionTestResult,
                    pendingVouchers = vouchers.filter { it.syncStatus.name == "PENDING_UPLOAD" },
                    syncLogs = syncLogs,
                    onSaveConfig = { viewModel.updateConfig(it) },
                    onTestConnection = { url, key, company, desktopPath, targetMachine ->
                        viewModel.testConnection(url, key, company, desktopPath, targetMachine)
                    },
                    onForceSync = { viewModel.triggerSync() },
                    onClearLogs = { viewModel.clearLogs() },
                    customLogoUri = uiState.customLogoUri,
                    onSelectCustomLogo = { viewModel.setCustomLogo(it) },
                    onResetCustomLogo = { viewModel.resetToDefaultLogo() },
                    desktopVersionInfo = uiState.desktopVersionInfo,
                    isCheckingDesktopUpdates = uiState.isCheckingDesktopUpdates,
                    onCheckDesktopCapabilities = { viewModel.showDesktopCapabilityDialog(true) },
                    pollingState = pollingState,
                    onTogglePolling = { viewModel.togglePolling(it) },
                    onSetPollingInterval = { viewModel.setPollingInterval(it) },
                    onPollNow = { viewModel.pollNow() },
                    onToggleFcm = { viewModel.toggleFcm(it) },
                    onSimulateDesktopPush = { viewModel.simulateDesktopPush() }
                )
            }
        }
    }

    // Modal Dialogs
    if (uiState.showSelectMachineDialog) {
        SelectDesktopMachineDialog(
            currentMachineCode = syncConfig.targetDesktopMachineCode,
            androidDeviceCode = syncConfig.androidDeviceCode,
            discoveredStations = viewModel.discoveredStations,
            onSelectStation = { station ->
                viewModel.selectDesktopStation(station)
            },
            onSaveCustomMachineCode = { customCode ->
                viewModel.setTargetMachineCodeManually(customCode)
            },
            onDismiss = { viewModel.showSelectMachineDialog(false) }
        )
    }

    if (uiState.showRegisterAccountantDialog) {
        RegisterAccountantDialog(
            targetMachineCode = syncConfig.targetDesktopMachineCode,
            onRegister = { u, f, r, p, e, m ->
                viewModel.createAccountantUser(u, f, r, p, e, m)
            },
            onDismiss = { viewModel.showRegisterAccountantDialog(false) }
        )
    }

    if (uiState.showConnectedSessionsDialog) {
        ConnectedSessionsDialog(
            currentMachineCode = syncConfig.targetDesktopMachineCode,
            accountantUsers = accountantUsers,
            onDeleteAccount = { viewModel.deleteAccountantUser(it) },
            onOpenRegisterDialog = {
                viewModel.showConnectedSessionsDialog(false)
                viewModel.showRegisterAccountantDialog(true)
            },
            onDismiss = { viewModel.showConnectedSessionsDialog(false) }
        )
    }

    if (uiState.showDesktopCapabilityDialog) {
        DesktopCapabilityDialog(
            versionInfo = uiState.desktopVersionInfo,
            syncConfig = syncConfig,
            customLogoUri = uiState.customLogoUri,
            isChecking = uiState.isCheckingDesktopUpdates,
            onCheckUpdates = { viewModel.checkDesktopCapabilities() },
            onDismiss = { viewModel.showDesktopCapabilityDialog(false) }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = AcRedNegative) },
            title = { Text("Tài khoản Kế toán") },
            text = {
                Column {
                    Text("Đang đăng nhập: ${userSession.fullName}")
                    Text("Tài khoản: ${userSession.username}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mã máy Desktop kết nối: ${syncConfig.targetDesktopMachineCode}", fontWeight = FontWeight.SemiBold, color = AcBrandBlue)
                    Text("Đường dẫn phần mềm:")
                    Text(
                        text = syncConfig.desktopFolderPath,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcRedNegative)
                ) {
                    Text("Đăng xuất")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun QuickServerConfigDialog(
    config: SyncConfig,
    onDismiss: () -> Unit,
    onSave: (SyncConfig) -> Unit
) {
    var serverUrl by remember { mutableStateOf(config.serverUrl) }
    var apiKey by remember { mutableStateOf(config.apiKey) }
    var desktopPath by remember { mutableStateOf(config.desktopFolderPath) }
    var targetMachine by remember { mutableStateOf(config.targetDesktopMachineCode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cấu hình kết nối A&C Desktop Bridge", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = targetMachine,
                    onValueChange = { targetMachine = it.uppercase() },
                    label = { Text("Mã máy Desktop (Ngăn trùng máy)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("Địa chỉ API Gateway máy chủ") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desktopPath,
                    onValueChange = { desktopPath = it },
                    label = { Text("Đường dẫn thư mục A&C trên Desktop") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Mã xác thực bảo mật (HMAC Key)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        config.copy(
                            serverUrl = serverUrl,
                            desktopFolderPath = desktopPath,
                            apiKey = apiKey,
                            targetDesktopMachineCode = targetMachine
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue)
            ) {
                Text("Lưu cấu hình")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
