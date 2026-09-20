package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgingDebtItem
import com.example.data.model.CashFlowItem
import com.example.data.model.FinancialSummary
import com.example.data.model.PnlItem
import com.example.ui.components.Formatters
import com.example.ui.theme.AcAmberBg
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcBrandNavy
import com.example.ui.theme.AcGreenBg
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcRedBg
import com.example.ui.theme.AcRedNegative

enum class ReportTab(val title: String) {
    PNL("Kết Quả Kinh Doanh"),
    CASH_FLOW("Lưu Chuyển Tiền Tệ"),
    AGING("Tuổi Nợ Khách Hàng")
}

@Composable
fun ReportsScreen(
    summary: FinancialSummary,
    pnlItems: List<PnlItem>,
    cashFlowItems: List<CashFlowItem>,
    agingDebtItems: List<AgingDebtItem>,
    onExportReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ReportTab.PNL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = AcBrandBlue,
            divider = {}
        ) {
            ReportTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action bar: Period & Export
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Kỳ báo cáo: Quý 3/2026",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = { onExportReport(selectedTab.title) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_export_report")
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Xuất Excel/PDF", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content
        when (selectedTab) {
            ReportTab.PNL -> PnlReportContent(summary = summary, items = pnlItems)
            ReportTab.CASH_FLOW -> CashFlowReportContent(items = cashFlowItems)
            ReportTab.AGING -> AgingReportContent(items = agingDebtItems)
        }
    }
}

@Composable
private fun PnlReportContent(summary: FinancialSummary, items: List<PnlItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AcBrandNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LỢI NHUẬN RÒNG SAU THUẾ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = Formatters.formatCurrency(summary.netProfit * 0.8),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AcGreenPositive
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tăng trưởng +18.5% so với cùng kỳ năm trước",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isHeader) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (item.isHeader) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Kỳ trước: ${Formatters.formatCurrency(item.previousPeriod)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = Formatters.formatCurrency(item.currentPeriod),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.title.contains("Lợi nhuận") || item.title.contains("Doanh thu")) AcGreenPositive else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CashFlowReportContent(items: List<CashFlowItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.month, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Surface(
                            color = if (item.netFlow >= 0) AcGreenBg else AcRedBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Dòng tiền ròng: " + Formatters.formatCurrency(item.netFlow),
                                color = if (item.netFlow >= 0) AcGreenPositive else AcRedNegative,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Tổng thu vào (+)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = Formatters.formatCurrency(item.cashIn),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AcGreenPositive
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Tổng chi ra (-)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = Formatters.formatCurrency(item.cashOut),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AcRedNegative
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

@Composable
private fun AgingReportContent(items: List<AgingDebtItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFE0F2FE),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.partnerCode,
                                    color = AcBrandBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = item.partnerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tổng nợ: " + Formatters.formatCurrency(item.totalDebt),
                        fontWeight = FontWeight.Bold,
                        color = AcAmberWarning,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Trong hạn", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = Formatters.formatCurrency(item.withinTerm), style = MaterialTheme.typography.bodySmall, color = AcGreenPositive)
                        }
                        Column {
                            Text(text = "Quá hạn 1-30 ngày", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = Formatters.formatCurrency(item.overdue1To30), style = MaterialTheme.typography.bodySmall, color = AcAmberWarning)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Quá hạn > 30 ngày", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = Formatters.formatCurrency(item.overdueOver30), style = MaterialTheme.typography.bodySmall, color = AcRedNegative)
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
