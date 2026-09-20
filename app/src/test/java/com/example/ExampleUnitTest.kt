package com.example

import com.example.data.model.SyncStatus
import com.example.data.model.VoucherItem
import com.example.data.model.VoucherType
import com.example.ui.components.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun formatCurrency_formatsCorrectly() {
    val formatted = Formatters.formatCurrency(55000000.0)
    assertTrue(formatted.contains("55.000.000") || formatted.contains("55,000,000"))
  }

  @Test
  fun numberToVietnameseWords_translatesAccurately() {
    val words50M = Formatters.numberToVietnameseWords(50000000.0)
    assertTrue(words50M.contains("triệu"))
    assertTrue(words50M.endsWith("đồng chẵn"))

    val words120M = Formatters.numberToVietnameseWords(120000000.0)
    assertTrue(words120M.contains("trăm"))
    assertTrue(words120M.contains("triệu"))
  }

  @Test
  fun voucherItem_hasFullDesktopAccountingDetails() {
    val voucher = VoucherItem(
      id = 1,
      voucherCode = "HDB-2026-0088",
      voucherType = VoucherType.HOA_DON_BAN,
      date = "2026-05-20",
      partnerCode = "KH001",
      partnerName = "Công ty TNHH Giải Pháp Công Nghệ Sao Việt",
      debitAccount = "131",
      creditAccount = "511",
      amount = 120000000.0,
      description = "Dịch vụ kế toán máy A&C",
      invoiceNumber = "0014285",
      invoiceSeries = "1C26TAV",
      vatRate = 10.0,
      vatAmount = 12000000.0,
      subtotalAmount = 108000000.0,
      targetDesktopMachineCode = "AC-DESKTOP-892A",
      isPostedToLedger = true,
      postedBy = "ketoantruong",
      syncStatus = SyncStatus.SYNCED
    )

    assertEquals("HDB-2026-0088", voucher.voucherCode)
    assertEquals("0014285", voucher.invoiceNumber)
    assertEquals("AC-DESKTOP-892A", voucher.targetDesktopMachineCode)
    assertTrue(voucher.isPostedToLedger)
    assertEquals(SyncStatus.SYNCED, voucher.syncStatus)
  }
}

