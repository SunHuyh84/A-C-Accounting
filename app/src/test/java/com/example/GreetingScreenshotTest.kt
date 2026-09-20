package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.FinancialSummary
import com.example.data.model.SyncConfig
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun dashboard_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        DashboardScreen(
          summary = FinancialSummary(
            totalRevenue = 1485000000.0,
            totalExpense = 1088000000.0,
            netProfit = 397000000.0,
            cashOnHand = 145250000.0,
            bankDeposit = 680450000.0,
            totalReceivable = 412500000.0,
            totalPayable = 245000000.0
          ),
          syncConfig = SyncConfig(),
          isSyncing = false,
          pendingUploadCount = 0,
          recentVouchers = emptyList(),
          accountantUsers = emptyList(),
          onSyncClick = {},
          onNavigateTab = {},
          onOpenCreateVoucher = {},
          onOpenSelectMachine = {},
          onOpenConnectedSessions = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
