package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PartnerItem
import com.example.data.model.SyncStatus
import com.example.data.model.VoucherItem
import com.example.data.model.VoucherType
import com.example.ui.components.AcCompanyLogo
import com.example.ui.components.Formatters
import com.example.ui.components.SyncStatusChip
import com.example.ui.components.VoucherTypeBadge
import com.example.ui.theme.AcAmberBg
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcBrandNavy
import com.example.ui.theme.AcGreenBg
import com.example.ui.theme.AcGreenPositive
import com.example.ui.theme.AcRedBg
import com.example.ui.theme.AcRedNegative

fun getAccountLabel(code: String): String {
    return when (code) {
        "111" -> "111 - Tiền mặt tại quỹ"
        "112" -> "112 - Tiền gửi ngân hàng"
        "131" -> "131 - Phải thu của khách hàng"
        "156" -> "156 - Hàng hóa"
        "331" -> "331 - Phải trả cho người bán"
        "3331" -> "3331 - Thuế GTGT đầu ra"
        "1331" -> "1331 - Thuế GTGT đầu vào"
        "511" -> "511 - Doanh thu bán hàng & CCDV"
        "632" -> "632 - Giá vốn hàng bán"
        "642" -> "642 - Chi phí quản lý doanh nghiệp"
        else -> "$code - Tài khoản hạch toán"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersScreen(
    vouchers: List<VoucherItem>,
    partners: List<PartnerItem>,
    selectedTypeFilter: VoucherType?,
    searchQuery: String,
    onFilterChange: (VoucherType?) -> Unit,
    onSearchChange: (String) -> Unit,
    onDeleteVoucher: (Long) -> Unit,
    onUpdateVoucher: (VoucherItem) -> Unit,
    onCreateVoucher: (VoucherType, String, String, Double, String, String, String, String?, String?, Double) -> Unit,
    showCreateDialog: Boolean,
    onSetShowCreateDialog: (Boolean) -> Unit,
    onSyncSingleVoucher: ((Long) -> Unit)? = null,
    customLogoUri: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedVoucherDetail by remember { mutableStateOf<VoucherItem?>(null) }
    var voucherToEdit by remember { mutableStateOf<VoucherItem?>(null) }
    var voucherToDelete by remember { mutableStateOf<VoucherItem?>(null) }
    var voucherToPrint by remember { mutableStateOf<VoucherItem?>(null) }

    val filteredVouchers = vouchers.filter { item ->
        val matchesType = selectedTypeFilter == null || item.voucherType == selectedTypeFilter
        val matchesSearch = searchQuery.isBlank() ||
                item.voucherCode.contains(searchQuery, ignoreCase = true) ||
                item.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                item.invoiceSeries.contains(searchQuery, ignoreCase = true) ||
                item.partnerName.contains(searchQuery, ignoreCase = true) ||
                item.partnerCode.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.debitAccount.contains(searchQuery) ||
                item.creditAccount.contains(searchQuery)
        matchesType && matchesSearch
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSetShowCreateDialog(true) },
                containerColor = AcBrandBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_create_voucher")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Lập chứng từ mới")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voucher_search_input"),
                placeholder = { Text("Tìm theo số HĐ, mã chứng từ, đối tác, tài khoản...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Xóa tìm kiếm")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTypeFilter == null,
                    onClick = { onFilterChange(null) },
                    label = { Text("Tất cả (${vouchers.size})") },
                    shape = RoundedCornerShape(8.dp)
                )
                VoucherType.values().forEach { type ->
                    val count = vouchers.count { it.voucherType == type }
                    FilterChip(
                        selected = selectedTypeFilter == type,
                        onClick = { onFilterChange(if (selectedTypeFilter == type) null else type) },
                        label = { Text("${type.displayName} ($count)") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List of Vouchers
            if (filteredVouchers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Không tìm thấy chứng từ/hóa đơn phù hợp",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredVouchers, key = { it.id }) { voucher ->
                        VoucherCardItem(
                            voucher = voucher,
                            onClick = { selectedVoucherDetail = voucher },
                            onEdit = { voucherToEdit = voucher },
                            onDelete = { voucherToDelete = voucher },
                            onSync = { onSyncSingleVoucher?.invoke(voucher.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Voucher Detail Modal Dialog
    selectedVoucherDetail?.let { voucher ->
        VoucherDetailDialog(
            voucher = voucher,
            partner = partners.firstOrNull { it.code == voucher.partnerCode },
            onDismiss = { selectedVoucherDetail = null },
            onEdit = {
                voucherToEdit = voucher
                selectedVoucherDetail = null
            },
            onDelete = {
                voucherToDelete = voucher
                selectedVoucherDetail = null
            },
            onPrint = {
                voucherToPrint = voucher
            },
            onSyncSingle = {
                onSyncSingleVoucher?.invoke(voucher.id)
                selectedVoucherDetail = null
            }
        )
    }

    // Print Preview Dialog
    voucherToPrint?.let { voucher ->
        VoucherPrintPreviewDialog(
            voucher = voucher,
            partner = partners.firstOrNull { it.code == voucher.partnerCode },
            customLogoUri = customLogoUri,
            onDismiss = { voucherToPrint = null }
        )
    }

    // Create Voucher Dialog
    if (showCreateDialog) {
        CreateVoucherDialog(
            partners = partners,
            onDismiss = { onSetShowCreateDialog(false) },
            onConfirm = { type, pCode, pName, amount, debit, credit, desc, invNum, invSeries, vatRate ->
                onCreateVoucher(type, pCode, pName, amount, debit, credit, desc, invNum, invSeries, vatRate)
                onSetShowCreateDialog(false)
            }
        )
    }

    // Edit Voucher Dialog
    voucherToEdit?.let { voucher ->
        EditVoucherDialog(
            voucher = voucher,
            onDismiss = { voucherToEdit = null },
            onConfirm = { updated ->
                onUpdateVoucher(updated)
                voucherToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    voucherToDelete?.let { voucher ->
        AlertDialog(
            onDismissRequest = { voucherToDelete = null },
            title = { Text("Xác nhận xóa chứng từ") },
            text = { Text("Bạn có chắc chắn muốn xóa chứng từ ${voucher.voucherCode} (${voucher.partnerName})? Thao tác này sẽ đồng bộ lệnh xóa 2 chiều lên máy trạm Desktop.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteVoucher(voucher.id)
                        voucherToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcRedNegative)
                ) {
                    Text("Xác nhận xóa")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { voucherToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun VoucherCardItem(
    voucher: VoucherItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("voucher_item_${voucher.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Badges, Code & Desktop Ledger Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VoucherTypeBadge(type = voucher.voucherType)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = voucher.voucherCode,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                SyncStatusChip(status = voucher.syncStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Partner & Description
            Text(
                text = voucher.partnerName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = voucher.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Invoice Number & Desktop Station Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (voucher.invoiceNumber.isNotBlank()) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(0.5.dp, AcBrandBlue.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "HĐ: ${voucher.invoiceNumber} (${voucher.invoiceSeries})",
                            color = AcBrandBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = AcGreenBg,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AcGreenPositive,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Sổ cái Desktop: ${voucher.targetDesktopMachineCode}",
                            color = AcGreenPositive,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Debit/Credit, Date, Amount, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ĐK: Nợ ${voucher.debitAccount} / Có ${voucher.creditAccount}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ngày hạch toán: ${voucher.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val isPositive = voucher.voucherType in listOf(
                    VoucherType.PHIEU_THU,
                    VoucherType.BAO_CO,
                    VoucherType.HOA_DON_BAN
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = (if (isPositive) "+" else "-") + Formatters.formatCurrency(voucher.amount),
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) AcGreenPositive else AcRedNegative,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa",
                            tint = AcBrandBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Xóa",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherDetailDialog(
    voucher: VoucherItem,
    partner: PartnerItem?,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPrint: () -> Unit,
    onSyncSingle: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxSize(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AcBrandNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CHI TIẾT CHỨNG TỪ KẾ TOÁN",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Mã chứng từ: ${voucher.voucherCode}",
                                style = MaterialTheme.typography.labelMedium,
                                color = AcBrandBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Desktop Synchronization & Ledger Status Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (voucher.syncStatus == SyncStatus.SYNCED) AcGreenBg else AcAmberBg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (voucher.syncStatus == SyncStatus.SYNCED) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = if (voucher.syncStatus == SyncStatus.SYNCED) AcGreenPositive else AcAmberWarning,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (voucher.syncStatus == SyncStatus.SYNCED)
                                            "ĐỒNG BỘ 2 CHIỀU THÀNH CÔNG"
                                        else
                                            "CHỜ ĐỒNG BỘ LÊN DESKTOP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (voucher.syncStatus == SyncStatus.SYNCED) AcGreenPositive else AcAmberWarning
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Trạm Desktop đích: ${voucher.targetDesktopMachineCode} • Ghi sổ cái: Đã hạch toán",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (voucher.desktopGuid != null) {
                                    Text(
                                        text = "Desktop GUID: ${voucher.desktopGuid}",
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            if (voucher.syncStatus != SyncStatus.SYNCED) {
                                Button(
                                    onClick = onSyncSingle,
                                    colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = ButtonDefaults.TextButtonContentPadding
                                ) {
                                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Đồng bộ", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // 1. General Invoice & Voucher Info
                    DetailSectionCard(title = "1. Thông Tin Hóa Đơn & Chứng Từ") {
                        DetailItemRow(label = "Loại chứng từ", value = "${voucher.voucherType.displayName} (${voucher.voucherType.name})")
                        DetailItemRow(label = "Số chứng từ", value = voucher.voucherCode, isBold = true)
                        DetailItemRow(label = "Số hóa đơn điện tử", value = voucher.invoiceNumber.ifBlank { "Chưa cấp số (Phiếu nội bộ)" })
                        DetailItemRow(label = "Ký hiệu mẫu số/ký hiệu", value = voucher.invoiceSeries.ifBlank { "1C25TAV" })
                        DetailItemRow(label = "Ngày hạch toán", value = voucher.date)
                        DetailItemRow(label = "Người lập / Hạch toán", value = voucher.postedBy.ifBlank { "ketoantruong" })
                    }

                    // 2. Partner / Customer Details
                    DetailSectionCard(title = "2. Thông Tin Khách Hàng / Đối Tác") {
                        DetailItemRow(label = "Mã đối tác", value = voucher.partnerCode, isBold = true)
                        DetailItemRow(label = "Tên đơn vị", value = voucher.partnerName)
                        if (partner != null) {
                            DetailItemRow(label = "Mã số thuế", value = partner.taxCode)
                            DetailItemRow(label = "Địa chỉ", value = partner.address)
                            DetailItemRow(label = "Điện thoại liên hệ", value = partner.phone)
                        }
                    }

                    // 3. Accounting Debit / Credit & Tax Breakdown
                    DetailSectionCard(title = "3. Định Khoản Kế Toán & Thuế GTGT") {
                        // General Ledger Table Header
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tài khoản Nợ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(getAccountLabel(voucher.debitAccount), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tài khoản Có", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AcBrandBlue)
                                    Text(getAccountLabel(voucher.creditAccount), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val vatRate = voucher.vatRate
                        val vatAmount = if (voucher.vatAmount > 0) voucher.vatAmount else voucher.amount * (vatRate / 100.0)
                        val subtotal = if (voucher.subtotalAmount > 0) voucher.subtotalAmount else (voucher.amount - vatAmount).coerceAtLeast(0.0)

                        DetailItemRow(label = "Tiền trước thuế", value = Formatters.formatCurrency(subtotal))
                        DetailItemRow(label = "Thuế suất GTGT", value = "$vatRate %")
                        DetailItemRow(label = "Tiền thuế GTGT", value = Formatters.formatCurrency(vatAmount))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DetailItemRow(
                            label = "TỔNG TIỀN THANH TOÁN",
                            value = Formatters.formatCurrency(voucher.amount),
                            isBold = true,
                            valueColor = AcGreenPositive
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Bằng chữ: ${Formatters.numberToVietnameseWords(voucher.amount)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // 4. Description & Notes
                    DetailSectionCard(title = "4. Diễn Giải & Nội Dung Kinh Tế") {
                        Text(
                            text = voucher.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Footer Actions: Edit, Print Preview, Delete, Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrint,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("In phiếu", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sửa & Đồng bộ", fontSize = 12.sp)
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Xóa", tint = AcRedNegative)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AcBrandNavy
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(2.dp))
            content()
        }
    }
}

@Composable
fun DetailItemRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun VoucherPrintPreviewDialog(
    voucher: VoucherItem,
    partner: PartnerItem?,
    customLogoUri: String? = null,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxSize(0.94f)
                .clip(RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "XEM TRƯỚC PHIẾU KẾ TOÁN CHUẨN A&C",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = AcBrandNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Đóng")
                    }
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Printable A4 Layout Preview
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .background(Color(0xFFFAFAFA), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    // Header of Voucher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AcBrandNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                AcCompanyLogo(
                                    customLogoUri = customLogoUri,
                                    size = 32.dp,
                                    contentDescription = "Logo A&C Desktop"
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Đơn vị: CÔNG TY CỔ PHẦN A&C", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                Text("Bộ phận: Phòng Kế Toán Tài Chính", fontSize = 10.sp, color = Color.DarkGray)
                                Text("Mã máy trạm: ${voucher.targetDesktopMachineCode}", fontSize = 10.sp, color = Color.DarkGray)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Mẫu số: 01-TT", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            Text("(Ban hành theo TT số 200/2014/TT-BTC)", fontSize = 9.sp, fontStyle = FontStyle.Italic, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = voucher.voucherType.displayName.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Ngày hạch toán: ${voucher.date}",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Số: ${voucher.voucherCode} ${if (voucher.invoiceNumber.isNotBlank()) "• HĐ: ${voucher.invoiceNumber}" else ""}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Body
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PrintRow(label = "Họ và tên người nộp / đối tác:", value = voucher.partnerName)
                        PrintRow(label = "Mã số đối tác / MST:", value = "${voucher.partnerCode} ${partner?.taxCode?.let { "- MST: $it" } ?: ""}")
                        PrintRow(label = "Địa chỉ:", value = partner?.address ?: "Hồ Chí Minh, Việt Nam")
                        PrintRow(label = "Lý do nộp / diễn giải:", value = voucher.description)
                        PrintRow(label = "Số tiền:", value = Formatters.formatCurrency(voucher.amount), isBold = true)
                        PrintRow(label = "Viết bằng chữ:", value = Formatters.numberToVietnameseWords(voucher.amount), isItalic = true)
                        PrintRow(label = "Định khoản kế toán:", value = "Nợ TK ${voucher.debitAccount} / Có TK ${voucher.creditAccount}")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Signatures Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SignatureBox(title = "Giám đốc", note = "(Ký, đóng dấu, họ tên)")
                        SignatureBox(title = "Kế toán trưởng", note = "(Ký, họ tên)", name = voucher.postedBy)
                        SignatureBox(title = "Thủ quỹ", note = "(Ký, họ tên)")
                        SignatureBox(title = "Người lập biểu", note = "(Ký, họ tên)", name = "A&C Mobile")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hoàn tất xem trước phiếu")
                }
            }
        }
    }
}

@Composable
fun PrintRow(label: String, value: String, isBold: Boolean = false, isItalic: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SignatureBox(title: String, note: String, name: String = "") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(74.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
        Text(note, fontSize = 8.sp, fontStyle = FontStyle.Italic, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(36.dp))
        if (name.isNotBlank()) {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVoucherDialog(
    partners: List<PartnerItem>,
    onDismiss: () -> Unit,
    onConfirm: (VoucherType, String, String, Double, String, String, String, String?, String?, Double) -> Unit
) {
    var selectedType by remember { mutableStateOf(VoucherType.HOA_DON_BAN) }
    var selectedPartnerCode by remember { mutableStateOf(partners.firstOrNull()?.code ?: "KH001") }
    var partnerName by remember { mutableStateOf(partners.firstOrNull()?.name ?: "Công ty TNHH Giải Pháp Công Nghệ Sao Việt") }
    var amountText by remember { mutableStateOf("") }
    var debitAccount by remember { mutableStateOf("131") }
    var creditAccount by remember { mutableStateOf("511") }
    var description by remember { mutableStateOf("") }
    var invoiceNumber by remember { mutableStateOf("") }
    var invoiceSeries by remember { mutableStateOf("1C26TAV") }
    var selectedVatRate by remember { mutableDoubleStateOf(10.0) }

    var typeMenuExpanded by remember { mutableStateOf(false) }
    var partnerMenuExpanded by remember { mutableStateOf(false) }

    fun updateDefaultAccounts(type: VoucherType) {
        when (type) {
            VoucherType.PHIEU_THU -> {
                debitAccount = "111"
                creditAccount = "131"
                invoiceSeries = "1C26TAV"
            }
            VoucherType.PHIEU_CHI -> {
                debitAccount = "331"
                creditAccount = "111"
                invoiceSeries = "1C26TMM"
            }
            VoucherType.BAO_CO -> {
                debitAccount = "112"
                creditAccount = "131"
                invoiceSeries = "1C26TAV"
            }
            VoucherType.BAO_NO -> {
                debitAccount = "331"
                creditAccount = "112"
                invoiceSeries = "1C26TMM"
            }
            VoucherType.HOA_DON_BAN -> {
                debitAccount = "131"
                creditAccount = "511"
                invoiceSeries = "1C26TAV"
            }
            VoucherType.HOA_DON_MUA -> {
                debitAccount = "156"
                creditAccount = "331"
                invoiceSeries = "1C26TMM"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Lập Hóa Đơn / Chứng Từ Kế Toán", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Voucher Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeMenuExpanded,
                    onExpandedChange = { typeMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Loại chứng từ / hóa đơn") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        VoucherType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedType = type
                                    updateDefaultAccounts(type)
                                    typeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Partner Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = partnerMenuExpanded,
                    onExpandedChange = { partnerMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = partnerName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Đối tác / Khách hàng") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = partnerMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = partnerMenuExpanded,
                        onDismissRequest = { partnerMenuExpanded = false }
                    ) {
                        partners.forEach { partner ->
                            DropdownMenuItem(
                                text = { Text("${partner.code} - ${partner.name}") },
                                onClick = {
                                    selectedPartnerCode = partner.code
                                    partnerName = partner.name
                                    partnerMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Invoice Number & Series
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        label = { Text("Số HĐ (nếu có)") },
                        placeholder = { Text("0014299") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = invoiceSeries,
                        onValueChange = { invoiceSeries = it },
                        label = { Text("Ký hiệu HĐ") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Tổng số tiền thanh toán (VND)") },
                    placeholder = { Text("Ví dụ: 30000000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voucher_amount_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // VAT Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thuế VAT:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    listOf(0.0, 5.0, 8.0, 10.0).forEach { rate ->
                        FilterChip(
                            selected = selectedVatRate == rate,
                            onClick = { selectedVatRate = rate },
                            label = { Text("${rate.toInt()}%", fontSize = 11.sp) }
                        )
                    }
                }

                // Debit & Credit Accounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = debitAccount,
                        onValueChange = { debitAccount = it },
                        label = { Text("TK Nợ") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = creditAccount,
                        onValueChange = { creditAccount = it },
                        label = { Text("TK Có") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Nội dung diễn giải chứng từ") },
                    placeholder = { Text("Diễn giải hạch toán...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voucher_desc_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && description.isNotBlank()) {
                        onConfirm(
                            selectedType,
                            selectedPartnerCode,
                            partnerName,
                            amount,
                            debitAccount,
                            creditAccount,
                            description,
                            invoiceNumber.ifBlank { null },
                            invoiceSeries.ifBlank { null },
                            selectedVatRate
                        )
                    }
                },
                enabled = amountText.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue),
                modifier = Modifier.testTag("dialog_submit_voucher")
            ) {
                Text("Lưu & Đồng Bộ 2 Chiều")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun EditVoucherDialog(
    voucher: VoucherItem,
    onDismiss: () -> Unit,
    onConfirm: (VoucherItem) -> Unit
) {
    var amountText by remember { mutableStateOf(voucher.amount.toLong().toString()) }
    var description by remember { mutableStateOf(voucher.description) }
    var debitAccount by remember { mutableStateOf(voucher.debitAccount) }
    var creditAccount by remember { mutableStateOf(voucher.creditAccount) }
    var invoiceNumber by remember { mutableStateOf(voucher.invoiceNumber) }
    var invoiceSeries by remember { mutableStateOf(voucher.invoiceSeries) }
    var vatRate by remember { mutableDoubleStateOf(voucher.vatRate) }

    val currentAmount = amountText.toDoubleOrNull() ?: voucher.amount
    val calculatedVat = currentAmount * (vatRate / 100.0)
    val calculatedSubtotal = (currentAmount - calculatedVat).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Chỉnh Sửa Chứng Từ: ${voucher.voucherCode}")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Khách hàng: ${voucher.partnerName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Invoice Number & Series
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = invoiceNumber,
                        onValueChange = { invoiceNumber = it },
                        label = { Text("Số HĐ") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = invoiceSeries,
                        onValueChange = { invoiceSeries = it },
                        label = { Text("Ký hiệu") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Số tiền thanh toán (VND)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // VAT Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thuế VAT:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    listOf(0.0, 5.0, 8.0, 10.0).forEach { rate ->
                        FilterChip(
                            selected = vatRate == rate,
                            onClick = { vatRate = rate },
                            label = { Text("${rate.toInt()}%", fontSize = 11.sp) }
                        )
                    }
                }

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Tiền trước thuế: ${Formatters.formatCurrency(calculatedSubtotal)}", fontSize = 11.sp)
                        Text("Tiền thuế GTGT ($vatRate%): ${Formatters.formatCurrency(calculatedVat)}", fontSize = 11.sp)
                        Text("Bằng chữ: ${Formatters.numberToVietnameseWords(currentAmount)}", fontSize = 11.sp, fontStyle = FontStyle.Italic)
                    }
                }

                // Debit & Credit Accounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = debitAccount,
                        onValueChange = { debitAccount = it },
                        label = { Text("TK Nợ") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = creditAccount,
                        onValueChange = { creditAccount = it },
                        label = { Text("TK Có") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Nội dung diễn giải") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: voucher.amount
                    onConfirm(
                        voucher.copy(
                            amount = amount,
                            debitAccount = debitAccount,
                            creditAccount = creditAccount,
                            description = description,
                            invoiceNumber = invoiceNumber,
                            invoiceSeries = invoiceSeries,
                            vatRate = vatRate,
                            vatAmount = calculatedVat,
                            subtotalAmount = calculatedSubtotal,
                            syncStatus = SyncStatus.PENDING_UPLOAD
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue)
            ) {
                Text("Cập Nhật & Đồng Bộ 2 Chiều")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
