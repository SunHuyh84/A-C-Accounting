package com.example.data.model

enum class VoucherType(val displayName: String, val codePrefix: String) {
    PHIEU_THU("Phiếu Thu", "PT"),
    PHIEU_CHI("Phiếu Chi", "PC"),
    HOA_DON_BAN("Hóa Đơn Bán", "HDB"),
    HOA_DON_MUA("Hóa Đơn Mua", "HDM"),
    BAO_CO("Giấy Báo Có", "BC"),
    BAO_NO("Giấy Báo Nợ", "BN")
}

enum class SyncStatus(val displayName: String) {
    SYNCED("Đã đồng bộ"),
    PENDING_UPLOAD("Chờ đồng bộ lên Desktop"),
    SYNC_ERROR("Lỗi kết nối")
}

enum class PartnerType(val displayName: String) {
    CUSTOMER("Khách hàng"),
    VENDOR("Nhà cung cấp"),
    BOTH("Khách hàng & NCC")
}

data class VoucherItem(
    val id: Long = 0,
    val voucherCode: String,
    val voucherType: VoucherType,
    val date: String,
    val partnerCode: String,
    val partnerName: String,
    val debitAccount: String,
    val creditAccount: String,
    val amount: Double,
    val description: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val desktopGuid: String? = null,
    val invoiceNumber: String = "",
    val invoiceSeries: String = "",
    val isPostedToLedger: Boolean = true,
    val postedBy: String = "ketoantruong",
    val vatRate: Double = 10.0,
    val vatAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val targetDesktopMachineCode: String = "AC-DESKTOP-892A",
    val createdAt: Long = System.currentTimeMillis()
)

data class PartnerItem(
    val id: Long = 0,
    val code: String,
    val name: String,
    val phone: String,
    val address: String,
    val taxCode: String,
    val type: PartnerType,
    val currentDebt: Double, // Dương: Phải thu (hoặc phải trả với NCC)
    val debtLimit: Double,
    val isOverdue: Boolean = false
)

data class InventoryItem(
    val id: Long = 0,
    val code: String,
    val name: String,
    val unit: String,
    val quantityOnHand: Double,
    val costPrice: Double,
    val sellingPrice: Double,
    val minSafeStock: Double = 10.0
)

data class AccountBalance(
    val id: Long = 0,
    val accountCode: String,
    val accountName: String,
    val debitBalance: Double,
    val creditBalance: Double,
    val category: String
)

data class SyncConfig(
    val serverUrl: String = "http://192.168.1.130:8765",
    val apiKey: String = "E583A305E2701A9B2E10300C",
    val companyCode: String = "PANAP",
    val targetDesktopMachineCode: String = "AC-DESKTOP-D0C0", // Mã máy Desktop người dùng muốn kết nối
    val androidDeviceCode: String = "ANDR-MOB-7734", // Mã định danh duy nhất máy Android
    val connectedDesktopName: String = "Trạm Kế Toán A&C (AC-DESKTOP-D0C0)",
    val autoSync: Boolean = true,
    val syncIntervalSeconds: Int = 10,
    val lastSyncTimestamp: Long = 0L,
    val desktopFolderPath: String = """C:\Users\pc\Downloads\Bo cai skills-Claude+Codex\AC Accounting""",
    val pollingEnabled: Boolean = true,
    val pollingIntervalSeconds: Int = 10,
    val fcmRealtimeEnabled: Boolean = true,
    val fcmDeviceToken: String = "",
    val desktopChangeTopic: String = "ac_accounting_desktop_updates",
    val notifyOnBackgroundUpdate: Boolean = true,
    val pairingPin: String = "389210"
)

data class PollingState(
    val isPollingActive: Boolean = true,
    val isHeartbeatBeating: Boolean = false,
    val lastPolledTime: Long = 0L,
    val nextPollCountdownSeconds: Int = 10,
    val totalPollsPerformed: Long = 0L,
    val recordsRefreshedCount: Int = 0,
    val lastPollStatus: String = "Đang chạy polling tự động",
    val lastPollError: String? = null,
    val fcmToken: String = "fcm_andr_ac_7734_token_active",
    val fcmStatus: String = "Kênh FCM sẵn sàng",
    val lastFcmPushReceivedTime: Long = 0L,
    val lastFcmPayload: String? = null
)

data class DesktopPollResult(
    val hasChanges: Boolean,
    val newVouchersCount: Int,
    val updatedVouchersCount: Int,
    val latencyMs: Long,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DesktopPushEvent(
    val eventType: String = "DESKTOP_RECORD_CHANGED",
    val sourceMachineCode: String = "AC-DESKTOP-892A",
    val entityType: String = "VOUCHER",
    val recordsCount: Int = 1,
    val summary: String = "Phát hiện chứng từ mới phát sinh trên Desktop",
    val timestamp: Long = System.currentTimeMillis()
)

data class QuickAccountingSummary(
    val totalRevenue: Double,
    val netProfit: Double,
    val cashOnHand: Double,
    val bankDeposit: Double,
    val totalReceivable: Double,
    val totalPayable: Double
)

data class DesktopStation(
    val machineCode: String,
    val stationName: String,
    val desktopPath: String,
    val ipAddress: String,
    val isOnline: Boolean = true,
    val currentActiveUsers: Int = 1
)

data class AccountantUser(
    val id: Long = 0,
    val username: String,
    val fullName: String,
    val role: String, // "Kế toán trưởng", "Kế toán bán hàng", "Kế toán kho", "Kế toán thanh toán"
    val phone: String = "",
    val email: String = "",
    val targetMachineCode: String = "",
    val androidDeviceCode: String = "",
    val isApprovedOnDesktop: Boolean = true,
    val isOnline: Boolean = true,
    val lastActiveTime: Long = System.currentTimeMillis(),
    val lastAction: String = "Đăng nhập hệ thống"
)

data class SyncLogItem(
    val id: Long = 0,
    val timestamp: Long,
    val status: String,
    val recordsPushed: Int,
    val recordsPulled: Int,
    val latencyMs: Long,
    val message: String
)

data class FinancialSummary(
    val totalRevenue: Double,
    val totalExpense: Double,
    val netProfit: Double,
    val cashOnHand: Double,
    val bankDeposit: Double,
    val totalReceivable: Double,
    val totalPayable: Double
)

data class UserSession(
    val username: String = "",
    val fullName: String = "",
    val role: String = "Kế toán viên",
    val token: String = "",
    val companyName: String = "A&C Accounting Enterprise",
    val targetMachineCode: String = "AC-DESKTOP-892A",
    val androidDeviceCode: String = "ANDR-MOB-7734",
    val isLoggedIn: Boolean = false,
    val isBiometricEnrolled: Boolean = false
)

data class PnlItem(
    val code: String,
    val title: String,
    val currentPeriod: Double,
    val previousPeriod: Double,
    val isHeader: Boolean = false
)

data class CashFlowItem(
    val month: String,
    val cashIn: Double,
    val cashOut: Double,
    val netFlow: Double
)

data class AgingDebtItem(
    val partnerCode: String,
    val partnerName: String,
    val totalDebt: Double,
    val withinTerm: Double,
    val overdue1To30: Double,
    val overdueOver30: Double
)

data class RealTimeSyncEvent(
    val eventId: String,
    val action: String, // "INSERT", "UPDATE", "DELETE"
    val entityType: String, // "INVOICE", "CUSTOMER"
    val entityId: String,
    val payloadJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DesktopVersionInfo(
    val desktopAppVersion: String = "v2.5.2-Release (A&C Enterprise)",
    val desktopBuildNumber: Int = 20260520,
    val databaseSchemaVersion: Int = 18,
    val compatibleMobileMinVersion: String = "v1.0.0",
    val latestMobileApkVersion: String = "v2.5.2",
    val mobileDownloadUrl: String = "http://192.168.1.100:8443/download/AC_Accounting_Mobile_v2.5.2.apk",
    val releaseNotes: List<String> = listOf(
        "Cập nhật mẫu hóa đơn điện tử chuẩn Nghị định 123/2020/NĐ-CP",
        "Tối ưu bảng cân đối tài khoản và sổ cái đa máy trạm",
        "Hỗ trợ phân hệ kho đa kho và tính giá vốn bình quân gia quyền tức thời",
        "Đồng bộ realtime 2 chiều các chứng từ phiếu thu/chi/hóa đơn"
    ),
    val enabledFeatures: List<String> = listOf(
        "CHUNG_TU_KE_TOAN",
        "SO_CAI_DESKTOP",
        "QUAN_LY_KHO",
        "HOA_DON_DIEN_TU",
        "BAO_CAO_TAI_CHINH",
        "DONG_BO_MANG_LAN"
    ),
    val isUpdateAvailableForMobile: Boolean = false,
    val lastCheckedTimestamp: Long = System.currentTimeMillis()
)

data class AppBrandingConfig(
    val customLogoUri: String? = null,
    val customLogoUrl: String? = null,
    val useCustomLogo: Boolean = false,
    val companyBrandName: String = "A&C ACCOUNTING",
    val companyTagline: String = "Phần Mềm Kế Toán & Quản Trị Doanh Nghiệp"
)

