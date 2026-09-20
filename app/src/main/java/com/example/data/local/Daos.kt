package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VoucherDao {
    @Query("SELECT * FROM vouchers ORDER BY createdAt DESC")
    fun getAllVouchers(): Flow<List<VoucherEntity>>

    @Query("SELECT * FROM vouchers WHERE id = :id")
    suspend fun getVoucherById(id: Long): VoucherEntity?

    @Query("SELECT * FROM vouchers WHERE syncStatus = 'PENDING_UPLOAD'")
    suspend fun getPendingUploadVouchers(): List<VoucherEntity>

    @Query("SELECT COUNT(*) FROM vouchers WHERE syncStatus = 'PENDING_UPLOAD'")
    fun getPendingUploadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: VoucherEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vouchers: List<VoucherEntity>)

    @Update
    suspend fun updateVoucher(voucher: VoucherEntity)

    @Query("UPDATE vouchers SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)

    @Query("DELETE FROM vouchers WHERE id = :id")
    suspend fun deleteVoucher(id: Long)
}

@Dao
interface PartnerDao {
    @Query("SELECT * FROM partners ORDER BY name ASC")
    fun getAllPartners(): Flow<List<PartnerEntity>>

    @Query("SELECT * FROM partners WHERE id = :id")
    suspend fun getPartnerById(id: Long): PartnerEntity?

    @Query("SELECT * FROM partners WHERE type = :type ORDER BY name ASC")
    fun getPartnersByType(type: String): Flow<List<PartnerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: PartnerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(partners: List<PartnerEntity>)

    @Update
    suspend fun updatePartner(partner: PartnerEntity)

    @Query("DELETE FROM partners WHERE id = :id")
    suspend fun deletePartner(id: Long)

    @Query("SELECT SUM(currentDebt) FROM partners WHERE type IN ('CUSTOMER', 'BOTH')")
    fun getTotalReceivable(): Flow<Double?>

    @Query("SELECT SUM(currentDebt) FROM partners WHERE type IN ('VENDOR', 'BOTH')")
    fun getTotalPayable(): Flow<Double?>
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllInventory(): Flow<List<InventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryEntity>)

    @Query("SELECT COUNT(*) FROM inventory_items WHERE quantityOnHand <= minSafeStock")
    fun getLowStockCount(): Flow<Int>
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY accountCode ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT debitBalance FROM accounts WHERE accountCode = '111'")
    fun getCashBalance(): Flow<Double?>

    @Query("SELECT debitBalance FROM accounts WHERE accountCode = '112'")
    fun getBankBalance(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountEntity>)
}

@Dao
interface SyncLogDao {
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<SyncLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SyncLogEntity)

    @Query("DELETE FROM sync_logs")
    suspend fun clearLogs()
}

@Dao
interface SettingDao {
    @Query("SELECT value FROM settings WHERE key = :key")
    suspend fun getSetting(key: String): String?

    @Query("SELECT value FROM settings WHERE key = :key")
    fun getSettingFlow(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)
}

@Dao
interface AccountantUserDao {
    @Query("SELECT * FROM accountant_users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<AccountantUserEntity>>

    @Query("SELECT * FROM accountant_users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): AccountantUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AccountantUserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<AccountantUserEntity>)

    @Update
    suspend fun updateUser(user: AccountantUserEntity)

    @Query("UPDATE accountant_users SET lastAction = :action, lastActiveTime = :time, isOnline = 1 WHERE username = :username")
    suspend fun updateActivity(username: String, action: String, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM accountant_users WHERE id = :id")
    suspend fun deleteUser(id: Long)
}
