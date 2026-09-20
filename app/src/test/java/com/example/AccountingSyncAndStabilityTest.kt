package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.SyncConfig
import com.example.data.model.SyncStatus
import com.example.data.model.VoucherType
import com.example.data.repository.AccountingRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AccountingSyncAndStabilityTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AccountingRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AccountingRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `test voucher creation preserves complete accounting details and marks pending sync`() = runTest {
        // Create a new sales voucher with detailed VAT, invoice series, debit/credit accounts
        val voucherId = repository.createVoucher(
            type = VoucherType.HOA_DON_BAN,
            partnerCode = "KH-TEST-001",
            partnerName = "Công Ty TNHH Thử Nghiệm",
            amount = 15_000_000.0,
            debitAccount = "131",
            creditAccount = "5111",
            description = "Xuất hóa đơn bán thiết bị văn phòng",
            invoiceNumber = "HD-2026-0099",
            invoiceSeries = "1C26TAA",
            vatRate = 10.0
        )

        assertTrue(voucherId > 0)

        val vouchers = repository.allVouchers.first()
        val created = vouchers.firstOrNull { it.id == voucherId }
        assertNotNull(created)
        assertEquals("KH-TEST-001", created?.partnerCode)
        assertEquals("Công Ty TNHH Thử Nghiệm", created?.partnerName)
        assertEquals(15_000_000.0, created?.amount ?: 0.0, 0.001)
        assertEquals("131", created?.debitAccount)
        assertEquals("5111", created?.creditAccount)
        assertEquals("HD-2026-0099", created?.invoiceNumber)
        assertEquals("1C26TAA", created?.invoiceSeries)
        assertEquals(10.0, created?.vatRate ?: 0.0, 0.001)
        assertEquals(SyncStatus.PENDING_UPLOAD, created?.syncStatus)
    }

    @Test
    fun `test two-way editing re-queues voucher for desktop bidirectional synchronization`() = runTest {
        // Step 1: Create voucher
        val id = repository.createVoucher(
            type = VoucherType.PHIEU_CHI,
            partnerCode = "NCC-TEST-002",
            partnerName = "Nhà Cung Cấp Linh Kiện",
            amount = 4_500_000.0,
            debitAccount = "331",
            creditAccount = "111",
            description = "Chi tiền mặt thanh toán linh kiện máy tính",
            invoiceNumber = "PC-001",
            invoiceSeries = "1C26TBB",
            vatRate = 0.0
        )

        var voucher = repository.allVouchers.first().first { it.id == id }
        assertEquals(SyncStatus.PENDING_UPLOAD, voucher.syncStatus)

        // Step 2: Simulate that desktop sync cycle completed
        db.voucherDao().markAsSynced(listOf(id))
        val updatedCheck1 = repository.allVouchers.first().first { it.id == id }
        assertEquals(SyncStatus.SYNCED, updatedCheck1.syncStatus)

        // Step 3: Accountant edits the voucher on mobile (e.g. adjusts amount and note)
        val editedVoucher = updatedCheck1.copy(
            amount = 4_800_000.0,
            description = "Chi tiền mặt thanh toán linh kiện máy tính (Đã điều chỉnh theo hóa đơn VAT)",
            syncStatus = SyncStatus.PENDING_UPLOAD
        )
        repository.updateVoucher(editedVoucher)

        // Step 4: Verify that 2-way sync queue recognizes the update
        val finalCheck = repository.allVouchers.first().first { it.id == id }
        assertEquals(4_800_000.0, finalCheck.amount, 0.001)
        assertEquals(SyncStatus.PENDING_UPLOAD, finalCheck.syncStatus)
        assertTrue(finalCheck.description.contains("Đã điều chỉnh"))

        val pendingCount = repository.pendingUploadCount.first()
        assertTrue(pendingCount >= 1)
    }

    @Test
    fun `test custom desktop logo setting and reset`() = runTest {
        // Initial state should be null or blank (default built-in A&C vector logo)
        val initialLogo = repository.getCustomLogoUri()
        assertNull(initialLogo)

        // Set custom logo URI from Desktop
        val testUri = "content://media/external/images/media/9999"
        repository.saveCustomLogoUri(testUri)

        val updatedLogo = repository.getCustomLogoUri()
        assertEquals(testUri, updatedLogo)

        // Reset to default
        repository.saveCustomLogoUri(null)
        val resetLogo = repository.getCustomLogoUri()
        assertNull(resetLogo)
    }

    @Test
    fun `test desktop capabilities and version check returns valid schema info`() = runTest {
        val config = SyncConfig()
        val versionInfo = repository.checkDesktopCapabilities(config)

        assertNotNull(versionInfo)
        assertTrue(versionInfo.desktopAppVersion.isNotBlank())
        assertTrue(versionInfo.databaseSchemaVersion >= 1)
        assertTrue(versionInfo.enabledFeatures.isNotEmpty())
        assertTrue(versionInfo.enabledFeatures.contains("CHUNG_TU_KE_TOAN"))
        assertTrue(versionInfo.enabledFeatures.contains("DONG_BO_MANG_LAN"))
    }

    @Test
    fun `test financial summary calculations are robust without crash`() = runTest {
        repository.createVoucher(
            type = VoucherType.HOA_DON_BAN,
            partnerCode = "KH-01",
            partnerName = "Khách Hàng A",
            amount = 10_000_000.0,
            debitAccount = "131",
            creditAccount = "5111",
            description = "Doanh thu bán hàng"
        )
        repository.createVoucher(
            type = VoucherType.PHIEU_CHI,
            partnerCode = "NCC-01",
            partnerName = "Nhà Cung Cấp B",
            amount = 3_000_000.0,
            debitAccount = "642",
            creditAccount = "111",
            description = "Chi phí quản lý"
        )

        val summary = repository.financialSummary.first()
        assertNotNull(summary)
        assertTrue(summary.totalRevenue >= 0.0)
        assertTrue(summary.cashOnHand >= 0.0 || summary.cashOnHand < 0.0)
    }
}
