package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PartnerItem
import com.example.data.model.PartnerType
import com.example.ui.components.Formatters
import com.example.ui.theme.AcAmberWarning
import com.example.ui.theme.AcBrandBlue
import com.example.ui.theme.AcRedBg
import com.example.ui.theme.AcRedNegative

@Composable
fun PartnersScreen(
    partners: List<PartnerItem>,
    onCreatePartner: (String, String, String, String, String, PartnerType, Double, Double) -> Unit,
    onUpdatePartner: (PartnerItem) -> Unit,
    onDeletePartner: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, CUSTOMER, VENDOR, OVERDUE

    var showCreateDialog by remember { mutableStateOf(false) }
    var partnerToEdit by remember { mutableStateOf<PartnerItem?>(null) }
    var partnerToDelete by remember { mutableStateOf<PartnerItem?>(null) }

    val filteredPartners = partners.filter { partner ->
        val matchesType = when (selectedFilter) {
            "CUSTOMER" -> partner.type == PartnerType.CUSTOMER || partner.type == PartnerType.BOTH
            "VENDOR" -> partner.type == PartnerType.VENDOR || partner.type == PartnerType.BOTH
            "OVERDUE" -> partner.isOverdue
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                partner.name.contains(searchQuery, ignoreCase = true) ||
                partner.code.contains(searchQuery, ignoreCase = true) ||
                partner.phone.contains(searchQuery) ||
                partner.taxCode.contains(searchQuery)
        matchesType && matchesSearch
    }

    val totalReceivable = partners.filter { it.type == PartnerType.CUSTOMER || it.type == PartnerType.BOTH }.sumOf { it.currentDebt }
    val totalPayable = partners.filter { it.type == PartnerType.VENDOR || it.type == PartnerType.BOTH }.sumOf { it.currentDebt }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = AcBrandBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_create_partner")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm khách hàng")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Receivables & Payables Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Tổng phải thu KH (TK 131)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = Formatters.formatCurrency(totalReceivable),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcAmberWarning
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Tổng phải trả NCC (TK 331)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = Formatters.formatCurrency(totalPayable),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AcRedNegative
                        )
                    }
                }
            }

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("partner_search_input"),
                placeholder = { Text("Tìm theo tên, mã khách hàng, MST, SĐT...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Xóa")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("Tất cả (${partners.size})") },
                    shape = RoundedCornerShape(8.dp)
                )
                FilterChip(
                    selected = selectedFilter == "CUSTOMER",
                    onClick = { selectedFilter = "CUSTOMER" },
                    label = { Text("Khách hàng (${partners.count { it.type == PartnerType.CUSTOMER || it.type == PartnerType.BOTH }})") },
                    shape = RoundedCornerShape(8.dp)
                )
                FilterChip(
                    selected = selectedFilter == "VENDOR",
                    onClick = { selectedFilter = "VENDOR" },
                    label = { Text("Nhà cung cấp (${partners.count { it.type == PartnerType.VENDOR || it.type == PartnerType.BOTH }})") },
                    shape = RoundedCornerShape(8.dp)
                )
                FilterChip(
                    selected = selectedFilter == "OVERDUE",
                    onClick = { selectedFilter = "OVERDUE" },
                    label = { Text("Nợ quá hạn (${partners.count { it.isOverdue }})") },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredPartners.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(52.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Không tìm thấy khách hàng nào", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPartners, key = { it.id }) { partner ->
                        PartnerCard(
                            partner = partner,
                            onEdit = { partnerToEdit = partner },
                            onDelete = { partnerToDelete = partner }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Create Partner Dialog
    if (showCreateDialog) {
        CreatePartnerDialog(
            existingCount = partners.size,
            onDismiss = { showCreateDialog = false },
            onConfirm = { code, name, phone, address, tax, type, debt, limit ->
                onCreatePartner(code, name, phone, address, tax, type, debt, limit)
                showCreateDialog = false
            }
        )
    }

    // Edit Partner Dialog
    partnerToEdit?.let { partner ->
        EditPartnerDialog(
            partner = partner,
            onDismiss = { partnerToEdit = null },
            onConfirm = { updated ->
                onUpdatePartner(updated)
                partnerToEdit = null
            }
        )
    }

    // Delete Partner Dialog
    partnerToDelete?.let { partner ->
        AlertDialog(
            onDismissRequest = { partnerToDelete = null },
            title = { Text("Xác nhận xóa khách hàng") },
            text = { Text("Bạn có chắc muốn xóa đối tác '${partner.name}' (${partner.code}) khỏi cơ sở dữ liệu?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePartner(partner.id)
                        partnerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcRedNegative)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { partnerToDelete = null }) { Text("Hủy") }
            }
        )
    }
}

@Composable
fun PartnerCard(
    partner: PartnerItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("partner_card_${partner.code}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (partner.type == PartnerType.CUSTOMER) Color(0xFFE0F2FE) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = partner.code,
                            color = if (partner.type == PartnerType.CUSTOMER) AcBrandBlue else AcAmberWarning,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = partner.type.displayName,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (partner.isOverdue) {
                        Surface(
                            color = AcRedBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AcRedNegative, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Quá hạn", color = AcRedNegative, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Sửa", tint = AcBrandBlue, modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Xóa", tint = Color.Gray, modifier = Modifier.size(17.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = partner.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = partner.phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "MST: ${partner.taxCode}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = partner.address, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(8.dp))

            val debtRatio = if (partner.debtLimit > 0) (partner.currentDebt / partner.debtLimit).coerceIn(0.0, 1.0).toFloat() else 0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (partner.type == PartnerType.CUSTOMER) "Dư nợ phải thu (TK 131)" else "Dư nợ phải trả (TK 331)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = Formatters.formatCurrency(partner.currentDebt),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (partner.type == PartnerType.CUSTOMER) AcAmberWarning else AcRedNegative
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Hạn mức nợ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = Formatters.formatCurrency(partner.debtLimit),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { debtRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (partner.isOverdue || debtRatio > 0.9f) AcRedNegative else AcBrandBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun CreatePartnerDialog(
    existingCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, PartnerType, Double, Double) -> Unit
) {
    var code by remember { mutableStateOf("KH" + (existingCount + 1).toString().padStart(3, '0')) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var taxCode by remember { mutableStateOf("") }
    var debtLimitText by remember { mutableStateOf("50000000") }
    var initialDebtText by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm Khách Hàng / Đối Tác Mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Mã đối tác") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên khách hàng / Doanh nghiệp") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Địa chỉ") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = taxCode, onValueChange = { taxCode = it }, label = { Text("Mã số thuế") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = debtLimitText, onValueChange = { debtLimitText = it }, label = { Text("Hạn mức nợ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = initialDebtText, onValueChange = { initialDebtText = it }, label = { Text("Dư nợ đầu kỳ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            code,
                            name,
                            phone,
                            address,
                            taxCode,
                            PartnerType.CUSTOMER,
                            initialDebtText.toDoubleOrNull() ?: 0.0,
                            debtLimitText.toDoubleOrNull() ?: 50000000.0
                        )
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue)
            ) {
                Text("Lưu Khách Hàng")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun EditPartnerDialog(
    partner: PartnerItem,
    onDismiss: () -> Unit,
    onConfirm: (PartnerItem) -> Unit
) {
    var name by remember { mutableStateOf(partner.name) }
    var phone by remember { mutableStateOf(partner.phone) }
    var address by remember { mutableStateOf(partner.address) }
    var taxCode by remember { mutableStateOf(partner.taxCode) }
    var debtLimitText by remember { mutableStateOf(partner.debtLimit.toLong().toString()) }
    var currentDebtText by remember { mutableStateOf(partner.currentDebt.toLong().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa Thông Tin: ${partner.code}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên khách hàng") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Địa chỉ") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = taxCode, onValueChange = { taxCode = it }, label = { Text("Mã số thuế") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = currentDebtText, onValueChange = { currentDebtText = it }, label = { Text("Dư nợ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = debtLimitText, onValueChange = { debtLimitText = it }, label = { Text("Hạn mức") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        partner.copy(
                            name = name,
                            phone = phone,
                            address = address,
                            taxCode = taxCode,
                            currentDebt = currentDebtText.toDoubleOrNull() ?: partner.currentDebt,
                            debtLimit = debtLimitText.toDoubleOrNull() ?: partner.debtLimit
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcBrandBlue)
            ) {
                Text("Cập Nhật")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
