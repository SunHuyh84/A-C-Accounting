package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AccountantUser
import com.example.data.model.PartnerItem
import com.example.data.model.PartnerType
import com.example.data.model.SyncLogItem
import com.example.data.model.SyncStatus
import com.example.data.model.VoucherItem
import com.example.data.model.VoucherType

@Entity(tableName = "vouchers")
data class VoucherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val voucherCode: String,
    val voucherType: String, // from VoucherType.name
    val date: String,
    val partnerCode: String,
    val partnerName: String,
    val debitAccount: String,
    val creditAccount: String,
    val amount: Double,
    val description: String,
    val syncStatus: String, // from SyncStatus.name
    val desktopGuid: String?,
    val invoiceNumber: String = "",
    val invoiceSeries: String = "",
    val isPostedToLedger: Boolean = true,
    val postedBy: String = "ketoantruong",
    val vatRate: Double = 10.0,
    val vatAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val targetDesktopMachineCode: String = "AC-DESKTOP-892A",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toModel(): VoucherItem {
        return VoucherItem(
            id = id,
            voucherCode = voucherCode,
            voucherType = try { VoucherType.valueOf(voucherType) } catch (e: Exception) { VoucherType.PHIEU_THU },
            date = date,
            partnerCode = partnerCode,
            partnerName = partnerName,
            debitAccount = debitAccount,
            creditAccount = creditAccount,
            amount = amount,
            description = description,
            syncStatus = try { SyncStatus.valueOf(syncStatus) } catch (e: Exception) { SyncStatus.SYNCED },
            desktopGuid = desktopGuid,
            invoiceNumber = invoiceNumber,
            invoiceSeries = invoiceSeries,
            isPostedToLedger = isPostedToLedger,
            postedBy = postedBy,
            vatRate = vatRate,
            vatAmount = vatAmount,
            subtotalAmount = subtotalAmount,
            targetDesktopMachineCode = targetDesktopMachineCode,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromModel(model: VoucherItem): VoucherEntity {
            return VoucherEntity(
                id = model.id,
                voucherCode = model.voucherCode,
                voucherType = model.voucherType.name,
                date = model.date,
                partnerCode = model.partnerCode,
                partnerName = model.partnerName,
                debitAccount = model.debitAccount,
                creditAccount = model.creditAccount,
                amount = model.amount,
                description = model.description,
                syncStatus = model.syncStatus.name,
                desktopGuid = model.desktopGuid,
                invoiceNumber = model.invoiceNumber,
                invoiceSeries = model.invoiceSeries,
                isPostedToLedger = model.isPostedToLedger,
                postedBy = model.postedBy,
                vatRate = model.vatRate,
                vatAmount = model.vatAmount,
                subtotalAmount = model.subtotalAmount,
                targetDesktopMachineCode = model.targetDesktopMachineCode,
                createdAt = model.createdAt
            )
        }
    }
}

@Entity(tableName = "partners")
data class PartnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val phone: String,
    val address: String,
    val taxCode: String,
    val type: String, // PartnerType.name
    val currentDebt: Double,
    val debtLimit: Double,
    val isOverdue: Boolean
) {
    fun toModel(): PartnerItem = PartnerItem(
        id = id,
        code = code,
        name = name,
        phone = phone,
        address = address,
        taxCode = taxCode,
        type = try { PartnerType.valueOf(type) } catch (e: Exception) { PartnerType.CUSTOMER },
        currentDebt = currentDebt,
        debtLimit = debtLimit,
        isOverdue = isOverdue
    )

    companion object {
        fun fromModel(model: PartnerItem): PartnerEntity = PartnerEntity(
            id = model.id,
            code = model.code,
            name = model.name,
            phone = model.phone,
            address = model.address,
            taxCode = model.taxCode,
            type = model.type.name,
            currentDebt = model.currentDebt,
            debtLimit = model.debtLimit,
            isOverdue = model.isOverdue
        )
    }
}

@Entity(tableName = "inventory_items")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val unit: String,
    val quantityOnHand: Double,
    val costPrice: Double,
    val sellingPrice: Double,
    val minSafeStock: Double
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountCode: String,
    val accountName: String,
    val debitBalance: Double,
    val creditBalance: Double,
    val category: String
)

@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val status: String,
    val recordsPushed: Int,
    val recordsPulled: Int,
    val latencyMs: Long,
    val message: String
) {
    fun toModel(): SyncLogItem = SyncLogItem(
        id = id,
        timestamp = timestamp,
        status = status,
        recordsPushed = recordsPushed,
        recordsPulled = recordsPulled,
        latencyMs = latencyMs,
        message = message
    )
}

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "accountant_users")
data class AccountantUserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val fullName: String,
    val role: String,
    val phone: String,
    val email: String,
    val targetMachineCode: String,
    val androidDeviceCode: String,
    val isApprovedOnDesktop: Boolean = true,
    val isOnline: Boolean = true,
    val lastActiveTime: Long = System.currentTimeMillis(),
    val lastAction: String = "Đăng nhập hệ thống"
) {
    fun toModel(): AccountantUser = AccountantUser(
        id = id,
        username = username,
        fullName = fullName,
        role = role,
        phone = phone,
        email = email,
        targetMachineCode = targetMachineCode,
        androidDeviceCode = androidDeviceCode,
        isApprovedOnDesktop = isApprovedOnDesktop,
        isOnline = isOnline,
        lastActiveTime = lastActiveTime,
        lastAction = lastAction
    )

    companion object {
        fun fromModel(model: AccountantUser): AccountantUserEntity = AccountantUserEntity(
            id = model.id,
            username = model.username,
            fullName = model.fullName,
            role = model.role,
            phone = model.phone,
            email = model.email,
            targetMachineCode = model.targetMachineCode,
            androidDeviceCode = model.androidDeviceCode,
            isApprovedOnDesktop = model.isApprovedOnDesktop,
            isOnline = model.isOnline,
            lastActiveTime = model.lastActiveTime,
            lastAction = model.lastAction
        )
    }
}
