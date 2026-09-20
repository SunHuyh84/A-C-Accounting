package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AccountBalance
import com.example.data.model.AccountantUser
import com.example.data.model.AgingDebtItem
import com.example.data.model.CashFlowItem
import com.example.data.model.DesktopStation
import com.example.data.model.DesktopVersionInfo
import com.example.data.model.DesktopPollResult
import com.example.data.model.DesktopPushEvent
import com.example.data.model.FinancialSummary
import com.example.data.model.InventoryItem
import com.example.data.model.PartnerItem
import com.example.data.model.PartnerType
import com.example.data.model.PnlItem
import com.example.data.model.PollingState
import com.example.data.model.SyncConfig
import com.example.data.model.SyncLogItem
import com.example.data.model.UserSession
import com.example.data.model.VoucherItem
import com.example.data.model.VoucherType
import com.example.data.repository.AccountingRepository
import com.example.service.DesktopPushNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    DASHBOARD("Tổng quan"),
    INVOICES("Hóa đơn"),
    CUSTOMERS("Khách hàng"),
    REPORTS("Báo cáo"),
    BRIDGE_SYNC("Đồng bộ")
}

data class UiState(
    val currentTab: AppTab = AppTab.DASHBOARD,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val isTestingConnection: Boolean = false,
    val connectionTestResult: Pair<Boolean, String>? = null,
    val voucherTypeFilter: VoucherType? = null,
    val partnerTypeFilter: PartnerType? = null,
    val searchQuery: String = "",
    val showCreateVoucherDialog: Boolean = false,
    val showServerConfigDialog: Boolean = false,
    val showRegisterAccountantDialog: Boolean = false,
    val showSelectMachineDialog: Boolean = false,
    val showConnectedSessionsDialog: Boolean = false,
    val showDesktopCapabilityDialog: Boolean = false,
    val showPollingSettingsDialog: Boolean = false,
    val isCheckingDesktopUpdates: Boolean = false,
    val desktopVersionInfo: DesktopVersionInfo = DesktopVersionInfo(),
    val customLogoUri: String? = null,
    val snackbarMessage: String? = null
)

class AccountingViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = AccountingRepository(db)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _userSession = MutableStateFlow(UserSession())
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    private val _pollingState = MutableStateFlow(PollingState())
    val pollingState: StateFlow<PollingState> = _pollingState.asStateFlow()

    val vouchers: StateFlow<List<VoucherItem>> = repository.allVouchers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingUploadCount: StateFlow<Int> = repository.pendingUploadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val partners: StateFlow<List<PartnerItem>> = repository.allPartners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accountantUsers: StateFlow<List<AccountantUser>> = repository.allAccountantUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryItem>> = repository.allInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accounts: StateFlow<List<AccountBalance>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<SyncLogItem>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialSummary: StateFlow<FinancialSummary> = repository.financialSummary
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            FinancialSummary(
                totalRevenue = 1485000000.0,
                totalExpense = 1088000000.0,
                netProfit = 397000000.0,
                cashOnHand = 145250000.0,
                bankDeposit = 680450000.0,
                totalReceivable = 412500000.0,
                totalPayable = 245000000.0
            )
        )

    val pnlReport: StateFlow<List<PnlItem>> = repository.pnlReport
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cashFlowReport: StateFlow<List<CashFlowItem>> = repository.cashFlowReport
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agingDebtReport: StateFlow<List<AgingDebtItem>> = repository.agingDebtReport
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val discoveredStations: List<DesktopStation> = repository.getDiscoveredDesktopStations()

    private val _syncConfig = MutableStateFlow(SyncConfig())
    val syncConfig: StateFlow<SyncConfig> = _syncConfig.asStateFlow()

    private val _savedAuthUsername = MutableStateFlow<String?>(null)
    val savedAuthUsername: StateFlow<String?> = _savedAuthUsername.asStateFlow()

    private val _hasSavedAccount = MutableStateFlow(false)
    val hasSavedAccount: StateFlow<Boolean> = _hasSavedAccount.asStateFlow()

    private var pollingJob: Job? = null

    init {
        viewModelScope.launch {
            repository.customLogoFlow.collect { uri ->
                _uiState.update { it.copy(customLogoUri = uri) }
            }
        }
        setupDesktopPushListener()
        viewModelScope.launch {
            val config = repository.loadSyncConfig()
            _syncConfig.value = config
            _userSession.update {
                it.copy(
                    targetMachineCode = config.targetDesktopMachineCode,
                    androidDeviceCode = config.androidDeviceCode
                )
            }
            val saved = repository.getSavedAuthCredentials()
            if (saved != null && saved.first.isNotBlank()) {
                _savedAuthUsername.value = saved.first
                _hasSavedAccount.value = true
            }
            if (config.pollingEnabled) {
                startPollingLoop(config.pollingIntervalSeconds)
            }
        }
    }

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.authenticateUser(username, pass)
            result.onSuccess { user ->
                val config = _syncConfig.value
                _userSession.update {
                    it.copy(
                        username = user.username,
                        isLoggedIn = true,
                        fullName = user.fullName,
                        role = user.role,
                        targetMachineCode = config.targetDesktopMachineCode,
                        androidDeviceCode = config.androidDeviceCode,
                        token = "AC_SECURE_AUTH_TOKEN_${System.currentTimeMillis()}"
                    )
                }
                _savedAuthUsername.value = user.username
                _hasSavedAccount.value = true
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        snackbarMessage = "Đăng nhập thành công! Vai trò: ${user.role} · Máy trạm: ${config.targetDesktopMachineCode}"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        snackbarMessage = err.message ?: "Đăng nhập thất bại"
                    )
                }
            }
        }
    }

    fun loginWithBiometrics() {
        viewModelScope.launch {
            val saved = repository.getSavedAuthCredentials()
            if (saved == null || saved.first.isBlank()) {
                _uiState.update {
                    it.copy(snackbarMessage = "Chưa có tài khoản nào được lưu trên thiết bị. Vui lòng tạo tài khoản kèm mã PIN ghép nối lần đầu.")
                }
                return@launch
            }
            login(saved.first, "biometric_auth")
        }
    }

    fun logout() {
        _userSession.update { it.copy(isLoggedIn = false) }
        _uiState.update { it.copy(snackbarMessage = "Đã đăng xuất.") }
    }

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setVoucherTypeFilter(type: VoucherType?) {
        _uiState.update { it.copy(voucherTypeFilter = type) }
    }

    fun setPartnerTypeFilter(type: PartnerType?) {
        _uiState.update { it.copy(partnerTypeFilter = type) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun showCreateVoucherDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateVoucherDialog = show) }
    }

    fun showServerConfigDialog(show: Boolean) {
        _uiState.update { it.copy(showServerConfigDialog = show) }
    }

    fun showRegisterAccountantDialog(show: Boolean) {
        _uiState.update { it.copy(showRegisterAccountantDialog = show) }
    }

    fun showSelectMachineDialog(show: Boolean) {
        _uiState.update { it.copy(showSelectMachineDialog = show) }
    }

    fun showConnectedSessionsDialog(show: Boolean) {
        _uiState.update { it.copy(showConnectedSessionsDialog = show) }
    }

    // Accountant User Registration
    fun createAccountantUser(
        username: String,
        pass: String,
        fullName: String,
        role: String,
        phone: String,
        email: String,
        targetMachineCode: String,
        pairingPin: String
    ) {
        viewModelScope.launch {
            val res = repository.verifyAndRegisterAccount(
                username = username,
                pass = pass,
                fullName = fullName,
                role = role,
                phone = phone,
                email = email,
                targetMachineCode = targetMachineCode,
                pairingPin = pairingPin
            )
            res.onSuccess { user ->
                val config = _syncConfig.value
                _userSession.update {
                    it.copy(
                        username = user.username,
                        isLoggedIn = true,
                        fullName = user.fullName,
                        role = user.role,
                        targetMachineCode = config.targetDesktopMachineCode,
                        androidDeviceCode = config.androidDeviceCode,
                        token = "AC_SECURE_AUTH_TOKEN_${System.currentTimeMillis()}"
                    )
                }
                _savedAuthUsername.value = user.username
                _hasSavedAccount.value = true
                _uiState.update {
                    it.copy(
                        showRegisterAccountantDialog = false,
                        snackbarMessage = "Tạo tài khoản và ghép nối thành công! Đã lưu khóa vân tay cho những lần sau."
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(snackbarMessage = err.message ?: "Tạo tài khoản thất bại")
                }
            }
        }
    }

    fun deleteAccountantUser(id: Long) {
        viewModelScope.launch {
            repository.deleteAccountantUser(id)
            _uiState.update { it.copy(snackbarMessage = "Đã hủy phiên tài khoản kế toán.") }
        }
    }

    // Desktop Machine Selection
    fun selectDesktopStation(station: DesktopStation) {
        viewModelScope.launch {
            val updated = _syncConfig.value.copy(
                targetDesktopMachineCode = station.machineCode,
                connectedDesktopName = station.stationName,
                desktopFolderPath = station.desktopPath
            )
            _syncConfig.value = updated
            repository.saveSyncConfig(updated)
            _userSession.update { it.copy(targetMachineCode = station.machineCode) }
            _uiState.update {
                it.copy(
                    showSelectMachineDialog = false,
                    snackbarMessage = "Đã chuyển kết nối sang máy Desktop: ${station.machineCode} (${station.stationName})"
                )
            }
            triggerSync()
        }
    }

    fun setTargetMachineCodeManually(machineCode: String) {
        viewModelScope.launch {
            val codeClean = machineCode.trim().uppercase()
            val updated = _syncConfig.value.copy(
                targetDesktopMachineCode = codeClean,
                connectedDesktopName = "Máy Trạm ($codeClean)"
            )
            _syncConfig.value = updated
            repository.saveSyncConfig(updated)
            _userSession.update { it.copy(targetMachineCode = codeClean) }
            _uiState.update {
                it.copy(
                    showSelectMachineDialog = false,
                    snackbarMessage = "Đã lưu mã máy kết nối: $codeClean (Tránh xung đột máy khác)"
                )
            }
            triggerSync()
        }
    }

    // Voucher CRUD
    fun createVoucher(
        type: VoucherType,
        partnerCode: String,
        partnerName: String,
        amount: Double,
        debitAccount: String,
        creditAccount: String,
        description: String,
        invoiceNumber: String? = null,
        invoiceSeries: String? = null,
        vatRate: Double = 10.0
    ) {
        viewModelScope.launch {
            repository.createVoucher(
                type = type,
                partnerCode = partnerCode,
                partnerName = partnerName,
                amount = amount,
                debitAccount = debitAccount,
                creditAccount = creditAccount,
                description = description,
                currentUser = _userSession.value.username,
                invoiceNumber = invoiceNumber,
                invoiceSeries = invoiceSeries,
                vatRate = vatRate
            )
            _uiState.update {
                it.copy(
                    showCreateVoucherDialog = false,
                    snackbarMessage = "Đã tạo chứng từ mới! Desktop [${_syncConfig.value.targetDesktopMachineCode}] ghi nhận hoạt động."
                )
            }
            if (_syncConfig.value.autoSync) {
                triggerSync()
            }
        }
    }

    fun updateVoucher(voucher: VoucherItem) {
        viewModelScope.launch {
            repository.updateVoucher(voucher, currentUser = _userSession.value.username)
            _uiState.update { it.copy(snackbarMessage = "Đã cập nhật chứng từ ${voucher.voucherCode} và đồng bộ 2 chiều lên Desktop.") }
            if (_syncConfig.value.autoSync) {
                triggerSync()
            }
        }
    }

    fun syncSingleVoucher(voucherId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val res = repository.syncSingleVoucher(voucherId)
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    snackbarMessage = res.getOrElse { err -> "Lỗi đồng bộ: ${err.message}" }
                )
            }
        }
    }

    fun deleteVoucher(id: Long) {
        viewModelScope.launch {
            repository.deleteVoucher(id, currentUser = _userSession.value.username)
            _uiState.update { it.copy(snackbarMessage = "Đã xóa chứng từ.") }
            if (_syncConfig.value.autoSync) {
                triggerSync()
            }
        }
    }

    // Partner / Customer CRUD
    fun createPartner(
        code: String,
        name: String,
        phone: String,
        address: String,
        taxCode: String,
        type: PartnerType,
        currentDebt: Double,
        debtLimit: Double
    ) {
        viewModelScope.launch {
            repository.createPartner(
                code = code,
                name = name,
                phone = phone,
                address = address,
                taxCode = taxCode,
                type = type,
                currentDebt = currentDebt,
                debtLimit = debtLimit,
                currentUser = _userSession.value.username
            )
            _uiState.update { it.copy(snackbarMessage = "Đã thêm khách hàng '$name' và đồng bộ lên PC Desktop.") }
            if (_syncConfig.value.autoSync) {
                triggerSync()
            }
        }
    }

    fun updatePartner(partner: PartnerItem) {
        viewModelScope.launch {
            repository.updatePartner(partner, currentUser = _userSession.value.username)
            _uiState.update { it.copy(snackbarMessage = "Đã cập nhật khách hàng ${partner.code}.") }
            if (_syncConfig.value.autoSync) {
                triggerSync()
            }
        }
    }

    fun deletePartner(id: Long) {
        viewModelScope.launch {
            repository.deletePartner(id, currentUser = _userSession.value.username)
            _uiState.update { it.copy(snackbarMessage = "Đã xóa đối tác khỏi danh bạ.") }
            if (_syncConfig.value.autoSync) {
                triggerSync()
            }
        }
    }

    // Sync Actions
    fun triggerSync() {
        if (_uiState.value.isSyncing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val result = repository.performSync(_syncConfig.value)
            delay(350)
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    snackbarMessage = if (result.isSuccess) {
                        val log = result.getOrNull()
                        "Đồng bộ với [${_syncConfig.value.targetDesktopMachineCode}] thành công (${log?.recordsPushed ?: 0} gửi, ${log?.recordsPulled ?: 0} nhận)"
                    } else {
                        "Lỗi đồng bộ: ${result.exceptionOrNull()?.message}"
                    }
                )
            }
        }
    }

    fun testConnection(
        serverUrl: String,
        apiKey: String,
        companyCode: String,
        desktopPath: String,
        targetMachineCode: String = _syncConfig.value.targetDesktopMachineCode
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnection = true, connectionTestResult = null) }
            val tempConfig = _syncConfig.value.copy(
                serverUrl = serverUrl,
                apiKey = apiKey,
                companyCode = companyCode,
                desktopFolderPath = desktopPath,
                targetDesktopMachineCode = targetMachineCode
            )
            val result = repository.testConnection(tempConfig)
            _uiState.update {
                it.copy(
                    isTestingConnection = false,
                    connectionTestResult = result
                )
            }
        }
    }

    fun updateConfig(newConfig: SyncConfig) {
        viewModelScope.launch {
            _syncConfig.value = newConfig
            repository.saveSyncConfig(newConfig)
            _userSession.update {
                it.copy(
                    targetMachineCode = newConfig.targetDesktopMachineCode,
                    androidDeviceCode = newConfig.androidDeviceCode
                )
            }
            if (newConfig.pollingEnabled) {
                startPollingLoop(newConfig.pollingIntervalSeconds)
            } else {
                pollingJob?.cancel()
            }
            _uiState.update { it.copy(snackbarMessage = "Đã lưu cấu hình kết nối Máy Desktop [${newConfig.targetDesktopMachineCode}]!") }
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearSyncLogs()
            _uiState.update { it.copy(snackbarMessage = "Đã dọn sạch nhật ký đồng bộ.") }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun setCustomLogo(uri: String?) {
        viewModelScope.launch {
            repository.saveCustomLogoUri(uri)
            _uiState.update {
                it.copy(
                    customLogoUri = uri,
                    snackbarMessage = if (uri.isNullOrBlank()) "Đã khôi phục Logo A&C chuẩn Desktop mặc định" else "Đã đồng bộ Logo A&C từ máy tính thành công!"
                )
            }
        }
    }

    fun resetToDefaultLogo() {
        setCustomLogo(null)
    }

    fun showDesktopCapabilityDialog(show: Boolean) {
        _uiState.update { it.copy(showDesktopCapabilityDialog = show) }
    }

    fun checkDesktopCapabilities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingDesktopUpdates = true) }
            val config = _syncConfig.value
            val info = repository.checkDesktopCapabilities(config)
            _uiState.update {
                it.copy(
                    isCheckingDesktopUpdates = false,
                    desktopVersionInfo = info,
                    showDesktopCapabilityDialog = true,
                    snackbarMessage = "Đã đồng bộ tính năng & kiểm tra phiên bản từ Máy Desktop [${info.desktopAppVersion}]"
                )
            }
        }
    }

    private fun setupDesktopPushListener() {
        DesktopPushNotificationManager.init(getApplication())

        viewModelScope.launch {
            DesktopPushNotificationManager.pushEvents.collect { pushEvent ->
                handleDesktopPushEvent(pushEvent)
            }
        }
        viewModelScope.launch {
            DesktopPushNotificationManager.fcmToken.collect { token ->
                _pollingState.update { it.copy(fcmToken = token) }
                _syncConfig.update { it.copy(fcmDeviceToken = token) }
            }
        }
        viewModelScope.launch {
            DesktopPushNotificationManager.fcmStatus.collect { status ->
                _pollingState.update { it.copy(fcmStatus = status) }
            }
        }
    }

    private fun handleDesktopPushEvent(pushEvent: DesktopPushEvent) {
        viewModelScope.launch {
            _pollingState.update {
                it.copy(
                    lastFcmPushReceivedTime = pushEvent.timestamp,
                    lastFcmPayload = "${pushEvent.eventType}: ${pushEvent.summary} (${pushEvent.recordsCount} bản ghi)",
                    lastPollStatus = "Nhận lệnh Push FCM từ [${pushEvent.sourceMachineCode}]"
                )
            }
            // Trigger immediate background refresh
            val res = repository.triggerDesktopPushRefresh(_syncConfig.value, pushEvent)
            res.onSuccess { count ->
                _pollingState.update { prev ->
                    prev.copy(
                        recordsRefreshedCount = prev.recordsRefreshedCount + count,
                        lastPolledTime = System.currentTimeMillis()
                    )
                }
                _uiState.update {
                    it.copy(
                        snackbarMessage = "FCM Push [${pushEvent.sourceMachineCode}]: Đã tự động cập nhật sổ sách kế toán!"
                    )
                }
            }
        }
    }

    fun startPollingLoop(intervalSeconds: Int) {
        pollingJob?.cancel()
        val interval = intervalSeconds.coerceAtLeast(3)
        _pollingState.update {
            it.copy(
                isPollingActive = true,
                nextPollCountdownSeconds = interval,
                lastPollStatus = "Đang thăm dò nền ($interval s/lần)"
            )
        }
        pollingJob = viewModelScope.launch {
            var countdown = interval
            while (true) {
                delay(1000L)
                countdown--
                if (countdown <= 0) {
                    countdown = _syncConfig.value.pollingIntervalSeconds.coerceAtLeast(3)
                    _pollingState.update {
                        it.copy(
                            isHeartbeatBeating = true,
                            nextPollCountdownSeconds = countdown,
                            lastPollStatus = "Đang kiểm tra dữ liệu Desktop..."
                        )
                    }
                    if (_syncConfig.value.pollingEnabled && !_uiState.value.isSyncing) {
                        val result = repository.pollDesktopForChanges(_syncConfig.value)
                        result.onSuccess { pollRes ->
                            _pollingState.update { prev ->
                                prev.copy(
                                    isHeartbeatBeating = false,
                                    lastPolledTime = pollRes.timestamp,
                                    totalPollsPerformed = prev.totalPollsPerformed + 1,
                                    recordsRefreshedCount = prev.recordsRefreshedCount + pollRes.newVouchersCount,
                                    lastPollStatus = if (pollRes.hasChanges) "Đã cập nhật ${pollRes.newVouchersCount} chứng từ mới" else "Đồng bộ khớp Desktop (${pollRes.latencyMs}ms)",
                                    lastPollError = null
                                )
                            }
                            if (pollRes.newVouchersCount > 0) {
                                _uiState.update {
                                    it.copy(snackbarMessage = "Polling nền: Nhận ${pollRes.newVouchersCount} chứng từ mới từ Desktop [${_syncConfig.value.targetDesktopMachineCode}]!")
                                }
                            }
                        }.onFailure { err ->
                            _pollingState.update { prev ->
                                prev.copy(
                                    isHeartbeatBeating = false,
                                    lastPollStatus = "Lỗi polling: ${err.localizedMessage ?: "Mất kết nối"}",
                                    lastPollError = err.message
                                )
                            }
                        }
                    } else {
                        _pollingState.update { it.copy(isHeartbeatBeating = false) }
                    }
                } else {
                    _pollingState.update { it.copy(nextPollCountdownSeconds = countdown) }
                }
            }
        }
    }

    fun togglePolling(enabled: Boolean) {
        viewModelScope.launch {
            val updated = _syncConfig.value.copy(pollingEnabled = enabled)
            _syncConfig.value = updated
            repository.saveSyncConfig(updated)
            if (enabled) {
                startPollingLoop(updated.pollingIntervalSeconds)
            } else {
                pollingJob?.cancel()
                _pollingState.update {
                    it.copy(
                        isPollingActive = false,
                        lastPollStatus = "Đã tạm dừng polling tự động"
                    )
                }
            }
        }
    }

    fun setPollingInterval(seconds: Int) {
        viewModelScope.launch {
            val updated = _syncConfig.value.copy(pollingIntervalSeconds = seconds)
            _syncConfig.value = updated
            repository.saveSyncConfig(updated)
            if (updated.pollingEnabled) {
                startPollingLoop(seconds)
            }
        }
    }

    fun pollNow() {
        viewModelScope.launch {
            _pollingState.update {
                it.copy(
                    isHeartbeatBeating = true,
                    lastPollStatus = "Đang kiểm tra dữ liệu Desktop ngay..."
                )
            }
            val result = repository.pollDesktopForChanges(_syncConfig.value)
            result.onSuccess { pollRes ->
                _pollingState.update { prev ->
                    prev.copy(
                        isHeartbeatBeating = false,
                        lastPolledTime = pollRes.timestamp,
                        totalPollsPerformed = prev.totalPollsPerformed + 1,
                        recordsRefreshedCount = prev.recordsRefreshedCount + pollRes.newVouchersCount,
                        lastPollStatus = if (pollRes.hasChanges) "Đã cập nhật ${pollRes.newVouchersCount} chứng từ mới" else "Đồng bộ khớp Desktop (${pollRes.latencyMs}ms)",
                        nextPollCountdownSeconds = _syncConfig.value.pollingIntervalSeconds
                    )
                }
                _uiState.update {
                    it.copy(
                        snackbarMessage = "Quét thủ công: ${pollRes.message}"
                    )
                }
            }.onFailure { err ->
                _pollingState.update { prev ->
                    prev.copy(
                        isHeartbeatBeating = false,
                        lastPollStatus = "Lỗi quét dữ liệu: ${err.message}"
                    )
                }
            }
        }
    }

    fun simulateDesktopPush(sourceMachine: String? = null, count: Int = 1) {
        val machine = sourceMachine ?: _syncConfig.value.targetDesktopMachineCode
        DesktopPushNotificationManager.simulateDesktopPush(getApplication(), machine, count)
    }

    fun toggleFcm(enabled: Boolean) {
        viewModelScope.launch {
            val updated = _syncConfig.value.copy(fcmRealtimeEnabled = enabled)
            _syncConfig.value = updated
            repository.saveSyncConfig(updated)
            _pollingState.update {
                it.copy(
                    fcmStatus = if (enabled) "Kênh FCM v1 Sẵn sàng" else "Kênh FCM đã tắt"
                )
            }
        }
    }

    fun openPollingSettingsDialog() {
        _uiState.update { it.copy(showPollingSettingsDialog = true) }
    }

    fun dismissPollingSettingsDialog() {
        _uiState.update { it.copy(showPollingSettingsDialog = false) }
    }
}
