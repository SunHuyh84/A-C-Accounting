package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VoucherEntity::class,
        PartnerEntity::class,
        InventoryEntity::class,
        AccountEntity::class,
        SyncLogEntity::class,
        SettingEntity::class,
        AccountantUserEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun voucherDao(): VoucherDao
    abstract fun partnerDao(): PartnerDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun accountDao(): AccountDao
    abstract fun syncLogDao(): SyncLogDao
    abstract fun settingDao(): SettingDao
    abstract fun accountantUserDao(): AccountantUserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ac_accounting_mobile.db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.populateInitialData()
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateInitialData() {
        // 1. Chart of Accounts (TT200 / TT133)
        accountDao().insertAll(
            listOf(
                AccountEntity(accountCode = "111", accountName = "Tiền mặt (VND)", debitBalance = 145250000.0, creditBalance = 0.0, category = "Tài sản ngắn hạn"),
                AccountEntity(accountCode = "112", accountName = "Tiền gửi ngân hàng (Vietcombank)", debitBalance = 680450000.0, creditBalance = 0.0, category = "Tài sản ngắn hạn"),
                AccountEntity(accountCode = "131", accountName = "Phải thu của khách hàng", debitBalance = 412500000.0, creditBalance = 0.0, category = "Nợ phải thu"),
                AccountEntity(accountCode = "156", accountName = "Hàng hóa tồn kho", debitBalance = 890000000.0, creditBalance = 0.0, category = "Hàng tồn kho"),
                AccountEntity(accountCode = "211", accountName = "Tài sản cố định hữu hình", debitBalance = 1250000000.0, creditBalance = 0.0, category = "Tài sản dài hạn"),
                AccountEntity(accountCode = "331", accountName = "Phải trả cho người bán", debitBalance = 0.0, creditBalance = 245000000.0, category = "Nợ phải trả"),
                AccountEntity(accountCode = "333", accountName = "Thuế và các khoản phải nộp NN", debitBalance = 0.0, creditBalance = 38500000.0, category = "Nợ phải trả"),
                AccountEntity(accountCode = "511", accountName = "Doanh thu bán hàng & cung cấp DV", debitBalance = 0.0, creditBalance = 1485000000.0, category = "Doanh thu"),
                AccountEntity(accountCode = "642", accountName = "Chi phí quản lý doanh nghiệp", debitBalance = 168000000.0, creditBalance = 0.0, category = "Chi phí"),
                AccountEntity(accountCode = "632", accountName = "Giá vốn hàng bán", debitBalance = 920000000.0, creditBalance = 0.0, category = "Chi phí")
            )
        )

        // 2. Sample Partners (Khách hàng & Nhà cung cấp)
        partnerDao().insertAll(
            listOf(
                PartnerEntity(
                    code = "KH001",
                    name = "Công ty TNHH Giải Pháp Công Nghệ Sao Việt",
                    phone = "0908 123 456",
                    address = "Tầng 5, Tòa nhà Vincom, Q.1, TP.HCM",
                    taxCode = "0314892741",
                    type = "CUSTOMER",
                    currentDebt = 145000000.0,
                    debtLimit = 200000000.0,
                    isOverdue = false
                ),
                PartnerEntity(
                    code = "KH002",
                    name = "Tập đoàn Đầu tư & Xây dựng An Gia",
                    phone = "0912 789 999",
                    address = "128 Nguyễn Thị Minh Khai, Q.3, TP.HCM",
                    taxCode = "0309876543",
                    type = "CUSTOMER",
                    currentDebt = 185000000.0,
                    debtLimit = 150000000.0,
                    isOverdue = true
                ),
                PartnerEntity(
                    code = "KH003",
                    name = "Công ty CP Xuất Nhập Khẩu Đông Dương",
                    phone = "0983 456 789",
                    address = "45 Lê Duẩn, Hoàn Kiếm, Hà Nội",
                    taxCode = "0102938475",
                    type = "CUSTOMER",
                    currentDebt = 82500000.0,
                    debtLimit = 100000000.0,
                    isOverdue = false
                ),
                PartnerEntity(
                    code = "NCC001",
                    name = "Công ty TNHH Phân Phối Thiết Bị Số Việt Nam",
                    phone = "028 3822 9999",
                    address = "250 Hoàng Văn Thụ, Tân Bình, TP.HCM",
                    taxCode = "0311223344",
                    type = "VENDOR",
                    currentDebt = 155000000.0,
                    debtLimit = 300000000.0,
                    isOverdue = false
                ),
                PartnerEntity(
                    code = "NCC002",
                    name = "Tổng Công ty Giấy & Văn phòng phẩm Hoàng Mai",
                    phone = "024 3766 8888",
                    address = "88 Giải Phóng, Hai Bà Trưng, Hà Nội",
                    taxCode = "0109988776",
                    type = "VENDOR",
                    currentDebt = 90000000.0,
                    debtLimit = 100000000.0,
                    isOverdue = false
                )
            )
        )

        // 3. Sample Vouchers (Phiếu thu, phiếu chi, hóa đơn...)
        voucherDao().insertAll(
            listOf(
                VoucherEntity(
                    voucherCode = "PT-2025-0012",
                    voucherType = "PHIEU_THU",
                    date = "2025-05-18",
                    partnerCode = "KH001",
                    partnerName = "Công ty TNHH Giải Pháp Công Nghệ Sao Việt",
                    debitAccount = "111",
                    creditAccount = "131",
                    amount = 55000000.0,
                    description = "Thu tiền tạm ứng hợp đồng phần mềm quản lý A&C",
                    syncStatus = "SYNCED",
                    desktopGuid = "DESK-AC-GUID-9901",
                    invoiceNumber = "0014285",
                    invoiceSeries = "1C25TAV",
                    isPostedToLedger = true,
                    postedBy = "ketoantruong",
                    vatRate = 10.0,
                    vatAmount = 5000000.0,
                    subtotalAmount = 50000000.0,
                    targetDesktopMachineCode = "AC-DESKTOP-892A"
                ),
                VoucherEntity(
                    voucherCode = "BC-2025-0045",
                    voucherType = "BAO_CO",
                    date = "2025-05-18",
                    partnerCode = "KH003",
                    partnerName = "Công ty CP Xuất Nhập Khẩu Đông Dương",
                    debitAccount = "112",
                    creditAccount = "131",
                    amount = 82500000.0,
                    description = "Khách hàng chuyển khoản thanh toán đợt 2 qua VCB",
                    syncStatus = "SYNCED",
                    desktopGuid = "DESK-AC-GUID-9902",
                    invoiceNumber = "0014286",
                    invoiceSeries = "1C25TAV",
                    isPostedToLedger = true,
                    postedBy = "ketoantruong",
                    vatRate = 10.0,
                    vatAmount = 7500000.0,
                    subtotalAmount = 75000000.0,
                    targetDesktopMachineCode = "AC-DESKTOP-892A"
                ),
                VoucherEntity(
                    voucherCode = "PC-2025-0009",
                    voucherType = "PHIEU_CHI",
                    date = "2025-05-17",
                    partnerCode = "NCC002",
                    partnerName = "Tổng Công ty Giấy & Văn phòng phẩm Hoàng Mai",
                    debitAccount = "331",
                    creditAccount = "111",
                    amount = 22000000.0,
                    description = "Chi trả tiền văn phòng phẩm và mực in quý 2",
                    syncStatus = "SYNCED",
                    desktopGuid = "DESK-AC-GUID-9903",
                    invoiceNumber = "0008741",
                    invoiceSeries = "1C25TMM",
                    isPostedToLedger = true,
                    postedBy = "ketoantruong",
                    vatRate = 10.0,
                    vatAmount = 2000000.0,
                    subtotalAmount = 20000000.0,
                    targetDesktopMachineCode = "AC-DESKTOP-892A"
                ),
                VoucherEntity(
                    voucherCode = "HDB-2025-0088",
                    voucherType = "HOA_DON_BAN",
                    date = "2025-05-16",
                    partnerCode = "KH002",
                    partnerName = "Tập đoàn Đầu tư & Xây dựng An Gia",
                    debitAccount = "131",
                    creditAccount = "511",
                    amount = 120000000.0,
                    description = "Xuất hóa đơn dịch vụ tư vấn triển khai kế toán máy",
                    syncStatus = "SYNCED",
                    desktopGuid = "DESK-AC-GUID-9904",
                    invoiceNumber = "0014287",
                    invoiceSeries = "1C25TAV",
                    isPostedToLedger = true,
                    postedBy = "ketoantruong",
                    vatRate = 10.0,
                    vatAmount = 10909090.0,
                    subtotalAmount = 109090910.0,
                    targetDesktopMachineCode = "AC-DESKTOP-892A"
                ),
                VoucherEntity(
                    voucherCode = "PT-2025-0013",
                    voucherType = "PHIEU_THU",
                    date = "2025-05-19",
                    partnerCode = "KH001",
                    partnerName = "Công ty TNHH Giải Pháp Công Nghệ Sao Việt",
                    debitAccount = "111",
                    creditAccount = "131",
                    amount = 30000000.0,
                    description = "Thu tiền mặt thanh toán dịch vụ hỗ trợ kỹ thuật",
                    syncStatus = "PENDING_UPLOAD",
                    desktopGuid = null,
                    invoiceNumber = "0014299",
                    invoiceSeries = "1C25TAV",
                    isPostedToLedger = true,
                    postedBy = "ketoantruong",
                    vatRate = 10.0,
                    vatAmount = 2727272.0,
                    subtotalAmount = 27272728.0,
                    targetDesktopMachineCode = "AC-DESKTOP-892A"
                )
            )
        )

        // 4. Sample Inventory (Vật tư hàng hóa TK 156)
        inventoryDao().insertAll(
            listOf(
                InventoryEntity(code = "SP-01", name = "Bản quyền Phần mềm Kế toán A&C Professional", unit = "Gói", quantityOnHand = 48.0, costPrice = 4500000.0, sellingPrice = 8500000.0, minSafeStock = 10.0),
                InventoryEntity(code = "SP-02", name = "Máy in Hóa đơn điện tử nhiệt cao cấp AC-200", unit = "Chiếc", quantityOnHand = 15.0, costPrice = 1800000.0, sellingPrice = 2850000.0, minSafeStock = 5.0),
                InventoryEntity(code = "SP-03", name = "Server Backup Dữ liệu Mini Cloud Gateway", unit = "Bộ", quantityOnHand = 4.0, costPrice = 12500000.0, sellingPrice = 16900000.0, minSafeStock = 5.0),
                InventoryEntity(code = "SP-04", name = "Chứng thư số Chữ ký số Viettel-CA 3 năm", unit = "Token", quantityOnHand = 85.0, costPrice = 1100000.0, sellingPrice = 1890000.0, minSafeStock = 20.0),
                InventoryEntity(code = "SP-05", name = "Bộ lưu điện Offline Santak 1000VA", unit = "Cái", quantityOnHand = 8.0, costPrice = 2400000.0, sellingPrice = 3300000.0, minSafeStock = 10.0)
            )
        )

        // 5. Initial Accountant Users (Tài khoản kế toán kết nối từ Android đến Desktop)
        accountantUserDao().insertAll(
            listOf(
                AccountantUserEntity(
                    username = "ketoantruong",
                    fullName = "Nguyễn Văn Kế Toán",
                    role = "Kế toán trưởng (Quản trị)",
                    phone = "0988 889 999",
                    email = "ketoantruong@ac-accounting.vn",
                    targetMachineCode = "AC-DESKTOP-892A",
                    androidDeviceCode = "ANDR-MOB-7734",
                    isApprovedOnDesktop = true,
                    isOnline = true,
                    lastActiveTime = System.currentTimeMillis(),
                    lastAction = "Đồng bộ hóa đơn HDB-2025-0088"
                ),
                AccountantUserEntity(
                    username = "kt_banhang",
                    fullName = "Trần Thị Mai Lan",
                    role = "Kế toán bán hàng & công nợ",
                    phone = "0912 345 678",
                    email = "mailan@ac-accounting.vn",
                    targetMachineCode = "AC-DESKTOP-892A",
                    androidDeviceCode = "ANDR-SAMSUNG-S23",
                    isApprovedOnDesktop = true,
                    isOnline = true,
                    lastActiveTime = System.currentTimeMillis() - 1000 * 60 * 5,
                    lastAction = "Lập Phiếu thu PT-2025-0013"
                ),
                AccountantUserEntity(
                    username = "kt_kho",
                    fullName = "Lê Hoàng Phúc",
                    role = "Kế toán kho & vật tư",
                    phone = "0909 654 321",
                    email = "hoangphuc@ac-accounting.vn",
                    targetMachineCode = "AC-DESKTOP-541B",
                    androidDeviceCode = "ANDR-XIAOMI-14",
                    isApprovedOnDesktop = true,
                    isOnline = false,
                    lastActiveTime = System.currentTimeMillis() - 1000 * 60 * 45,
                    lastAction = "Kiểm kê hàng tồn kho SP-01"
                )
            )
        )

        // 6. Initial Sync Logs
        syncLogDao().insertLog(
            SyncLogEntity(
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                status = "SUCCESS",
                recordsPushed = 2,
                recordsPulled = 4,
                latencyMs = 84,
                message = "Kết nối máy trạm [AC-DESKTOP-892A] - Thư mục: C:\\Users\\pc\\Downloads\\Bo cai skills-Claude+Codex\\AC Accounting"
            )
        )
    }
}
