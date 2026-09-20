package com.example.data.remote

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class BridgeHealthResponse(
    val status: String,
    val serverVersion: String,
    val desktopAppVersion: String,
    val databasePath: String,
    val machineCode: String = "AC-DESKTOP-892A",
    val activeConnections: Int,
    val serverTimestamp: Long
)

@JsonClass(generateAdapter = true)
data class ConnectedAccountantDto(
    val username: String,
    val fullName: String,
    val role: String,
    val phone: String,
    val email: String = "",
    val androidDeviceId: String,
    val targetMachineCode: String,
    val lastAction: String,
    val isOnline: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class VoucherDto(
    val guid: String,
    val voucherCode: String,
    val voucherType: String,
    val date: String,
    val partnerCode: String,
    val partnerName: String,
    val debitAccount: String,
    val creditAccount: String,
    val amount: Double,
    val description: String,
    val invoiceNumber: String? = "",
    val invoiceSeries: String? = "",
    val isPostedToLedger: Boolean = true,
    val postedBy: String? = "ketoantruong",
    val vatRate: Double = 10.0,
    val vatAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val targetDesktopMachineCode: String? = "AC-DESKTOP-892A",
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class PartnerDto(
    val code: String,
    val name: String,
    val phone: String,
    val address: String,
    val taxCode: String,
    val type: String, // "CUSTOMER", "VENDOR", "BOTH"
    val currentDebt: Double,
    val debtLimit: Double,
    val isOverdue: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SyncPushRequest(
    val deviceId: String,
    val companyCode: String,
    val targetMachineCode: String = "AC-DESKTOP-892A",
    val encryptedPayload: String? = null,
    val signature: String? = null,
    val vouchers: List<VoucherDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SyncPushResponse(
    val success: Boolean,
    val syncedCount: Int,
    val acknowledgedGuids: List<String>,
    val serverTimestamp: Long,
    val message: String
)

@JsonClass(generateAdapter = true)
data class SyncPullResponse(
    val success: Boolean,
    val serverTimestamp: Long,
    val newVouchers: List<VoucherDto>,
    val cashBalance: Double,
    val bankBalance: Double,
    val totalRevenue: Double,
    val totalExpense: Double
)

@JsonClass(generateAdapter = true)
data class GenericApiResponse(
    val success: Boolean,
    val message: String,
    val recordId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class EventSyncDto(
    val eventId: String,
    val action: String, // "INSERT", "UPDATE", "DELETE"
    val entityType: String, // "INVOICE", "CUSTOMER"
    val entityId: String,
    val payloadJson: String,
    val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class EventsResponse(
    val success: Boolean,
    val events: List<EventSyncDto>,
    val serverTimestamp: Long
)

@JsonClass(generateAdapter = true)
data class FinancialSummaryResponse(
    val totalRevenue: Double,
    val totalExpense: Double,
    val netProfit: Double,
    val cashOnHand: Double,
    val bankDeposit: Double,
    val totalReceivable: Double,
    val totalPayable: Double
)

interface AccountingApiService {

    // 1. Health & Handshake
    @GET("api/v1/health")
    suspend fun checkHealth(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null,
        @Header("X-AC-Signature") signature: String? = null
    ): Response<BridgeHealthResponse>

    // 2. Invoices CRUD
    @GET("api/v1/invoices")
    suspend fun getInvoices(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null
    ): Response<List<VoucherDto>>

    @POST("api/v1/invoices")
    suspend fun createInvoice(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null,
        @Body invoice: VoucherDto
    ): Response<GenericApiResponse>

    @PUT("api/v1/invoices/{id}")
    suspend fun updateInvoice(
        @Path("id") id: String,
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null,
        @Body invoice: VoucherDto
    ): Response<GenericApiResponse>

    @DELETE("api/v1/invoices/{id}")
    suspend fun deleteInvoice(
        @Path("id") id: String,
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null
    ): Response<GenericApiResponse>

    // 3. Customers CRUD
    @GET("api/v1/customers")
    suspend fun getCustomers(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null
    ): Response<List<PartnerDto>>

    @POST("api/v1/customers")
    suspend fun createCustomer(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null,
        @Body customer: PartnerDto
    ): Response<GenericApiResponse>

    @PUT("api/v1/customers/{id}")
    suspend fun updateCustomer(
        @Path("id") id: String,
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null,
        @Body customer: PartnerDto
    ): Response<GenericApiResponse>

    @DELETE("api/v1/customers/{id}")
    suspend fun deleteCustomer(
        @Path("id") id: String,
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null
    ): Response<GenericApiResponse>

    // 4. Batch Sync
    @POST("api/v1/sync/push")
    suspend fun pushVouchers(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Body request: SyncPushRequest
    ): Response<SyncPushResponse>

    @GET("api/v1/sync/pull")
    suspend fun pullUpdates(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Query("since") sinceTimestamp: Long
    ): Response<SyncPullResponse>

    // 5. Real-time Events
    @GET("api/v1/sync/events")
    suspend fun pollEvents(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Query("since") sinceTimestamp: Long
    ): Response<EventsResponse>

    @POST("api/v1/sync/events")
    suspend fun broadcastEvent(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String? = null,
        @Header("X-AC-Device-Id") deviceId: String? = null,
        @Body event: EventSyncDto
    ): Response<GenericApiResponse>

    // 6. Connected Accountant User reporting to Desktop
    @POST("api/v1/desktop/accountants/report")
    suspend fun reportAccountantActivity(
        @Header("X-AC-Token") token: String,
        @Header("X-AC-Company") company: String,
        @Header("X-AC-Target-Machine") targetMachine: String,
        @Header("X-AC-Device-Id") deviceId: String,
        @Body user: ConnectedAccountantDto
    ): Response<GenericApiResponse>
}

object ApiClientProvider {
    private var currentBaseUrl: String = ""
    private var cachedService: AccountingApiService? = null

    fun getService(baseUrl: String): AccountingApiService {
        val sanitizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (sanitizedUrl == currentBaseUrl && cachedService != null) {
            return cachedService!!
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(sanitizedUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val service = retrofit.create(AccountingApiService::class.java)
        currentBaseUrl = sanitizedUrl
        cachedService = service
        return service
    }
}
