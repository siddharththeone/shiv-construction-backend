package com.example.shiv.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

object ApiClientFactory {
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:4000/api/"

    fun create(baseUrl: String = DEFAULT_BASE_URL, tokenProvider: () -> String?): BackendApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val authInterceptor = Interceptor { chain ->
            val token = tokenProvider()
            val request = if (token != null) {
                chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            } else chain.request()
            chain.proceed(request)
        }
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
        return retrofit.create(BackendApi::class.java)
    }
}

interface BackendApi {
    // Authentication
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/register-fcm")
    suspend fun registerFcm(@Body body: FcmRequest): ApiResponse

    // Sites
    @GET("sites")
    suspend fun listSites(): List<SiteDto>

    @GET("sites/{id}")
    suspend fun getSite(@Path("id") id: String): SiteDto

    @POST("sites")
    suspend fun createSite(@Body body: CreateSiteRequest): SiteDto

    @PUT("sites/{id}")
    suspend fun updateSite(@Path("id") id: String, @Body body: UpdateSiteRequest): SiteDto

    @DELETE("sites/{id}")
    suspend fun deleteSite(@Path("id") id: String): ApiResponse

    @POST("sites/{id}/photos")
    suspend fun addSitePhotos(@Path("id") id: String, @Body body: AddPhotosRequest): ApiResponse

    @GET("sites/stats/overview")
    suspend fun getSiteStats(): SiteStatsDto

    // Tasks
    @GET("tasks")
    suspend fun listTasks(): List<TaskDto>

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") id: String): TaskDto

    @POST("tasks")
    suspend fun createTask(@Body body: CreateTaskRequest): TaskDto

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body body: UpdateTaskRequest): TaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): ApiResponse

    @POST("tasks/{id}/photos")
    suspend fun addTaskPhotos(@Path("id") id: String, @Body body: AddPhotosRequest): ApiResponse

    @POST("tasks/{id}/notes")
    suspend fun addTaskNotes(@Path("id") id: String, @Body body: AddNotesRequest): ApiResponse

    // Issues
    @GET("issues")
    suspend fun listIssues(): List<IssueDto>

    @GET("issues/{id}")
    suspend fun getIssue(@Path("id") id: String): IssueDto

    @POST("issues")
    suspend fun createIssue(@Body body: CreateIssueRequest): IssueDto

    @PUT("issues/{id}")
    suspend fun updateIssue(@Path("id") id: String, @Body body: UpdateIssueRequest): IssueDto

    @DELETE("issues/{id}")
    suspend fun deleteIssue(@Path("id") id: String): ApiResponse

    @POST("issues/{id}/photos")
    suspend fun addIssuePhotos(@Path("id") id: String, @Body body: AddPhotosRequest): ApiResponse

    @GET("issues/stats/overview")
    suspend fun getIssueStats(): IssueStatsDto

    // Materials
    @GET("materials")
    suspend fun listMaterialRequests(): List<MaterialRequestDto>

    @GET("materials/{id}")
    suspend fun getMaterialRequest(@Path("id") id: String): MaterialRequestDto

    @POST("materials/request")
    suspend fun createMaterialRequest(@Body body: CreateMaterialRequestRequest): MaterialRequestDto

    @POST("materials/{id}/status")
    suspend fun updateMaterialStatus(@Path("id") id: String, @Body body: UpdateMaterialStatusRequest): MaterialRequestDto

    @PUT("materials/{id}")
    suspend fun updateMaterialRequest(@Path("id") id: String, @Body body: UpdateMaterialRequestRequest): MaterialRequestDto

    @DELETE("materials/{id}")
    suspend fun cancelMaterialRequest(@Path("id") id: String): ApiResponse

    // Payments
    @GET("payments")
    suspend fun listPayments(): List<PaymentDto>

    @GET("payments/{id}")
    suspend fun getPayment(@Path("id") id: String): PaymentDto

    @POST("payments")
    suspend fun createPayment(@Body body: CreatePaymentRequest): PaymentDto

    @PUT("payments/{id}")
    suspend fun updatePayment(@Path("id") id: String, @Body body: UpdatePaymentRequest): PaymentDto

    @DELETE("payments/{id}")
    suspend fun cancelPayment(@Path("id") id: String): ApiResponse

    // Documents
    @GET("documents")
    suspend fun listDocuments(): List<DocumentDto>

    @GET("documents/{id}")
    suspend fun getDocument(@Path("id") id: String): DocumentDto

    @POST("documents")
    suspend fun uploadDocument(@Body body: UploadDocumentRequest): DocumentDto

    @PUT("documents/{id}")
    suspend fun updateDocument(@Path("id") id: String, @Body body: UpdateDocumentRequest): DocumentDto

    @DELETE("documents/{id}")
    suspend fun deleteDocument(@Path("id") id: String): ApiResponse

    @POST("documents/{id}/download")
    suspend fun recordDocumentDownload(@Path("id") id: String): ApiResponse

    @GET("documents/stats/overview")
    suspend fun getDocumentStats(): DocumentStatsDto

    // Notifications
    @GET("notifications")
    suspend fun listNotifications(): List<NotificationDto>

    @PUT("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String): ApiResponse

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: String): ApiResponse

    // File Uploads
    @POST("uploads")
    suspend fun uploadFile(@Body body: UploadFileRequest): UploadFileResponse
}

// Request/Response Data Classes
data class LoginRequest(val email: String, val password: String)

data class SignupRequest(
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val password: String,
    val role: String,
    val contractorType: String? = null
)

data class AuthResponse(val token: String, val user: UserDto)

data class FcmRequest(val token: String)

data class ApiResponse(val message: String? = null, val success: Boolean = true)

data class UserDto(
    val _id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val role: String,
    val contractorType: String? = null,
    val profileImage: String? = null,
    val address: String? = null,
    val companyName: String? = null
)


data class SiteDto(
    val _id: String,
    val name: String,
    val location: String?,
    val address: String?,
    val startDate: String?,
    val expectedEndDate: String?,
    val actualEndDate: String?,
    val status: String,
    val progressPercent: Int,
    val description: String?,
    val budget: Double?,
    val actualCost: Double?,
    val owner: UserDto,
    val contractors: List<UserDto> = emptyList(),
    val suppliers: List<UserDto> = emptyList(),
    val photos: List<String> = emptyList(),
    val documents: List<String> = emptyList(),
    val latitude: Double?,
    val longitude: Double?,
    val siteArea: Double?,
    val buildingType: String?,
    val floors: Int?
)


data class TaskDto(
    val _id: String,
    val title: String,
    val description: String?,
    val site: SiteDto,
    val assignedTo: UserDto,
    val assignedBy: UserDto,
    val status: String,
    val priority: String,
    val dueDate: String?,
    val completedAt: String?,
    val estimatedHours: Double?,
    val actualHours: Double?,
    val photos: List<String> = emptyList(),
    val notes: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)


data class IssueDto(
    val _id: String,
    val title: String,
    val description: String,
    val site: SiteDto,
    val reportedBy: UserDto,
    val assignedTo: UserDto?,
    val status: String,
    val priority: String,
    val category: String,
    val location: String?,
    val photos: List<String> = emptyList(),
    val resolvedAt: String?,
    val resolution: String?,
    val estimatedCost: Double?,
    val actualCost: Double?,
    val tags: List<String> = emptyList()
)


data class MaterialRequestDto(
    val _id: String,
    val site: SiteDto,
    val requestedBy: UserDto,
    val supplier: UserDto,
    val items: List<MaterialItemDto>,
    val status: String,
    val priority: String,
    val requestedDate: String,
    val expectedDeliveryDate: String?,
    val actualDeliveryDate: String?,
    val totalAmount: Double?,
    val notes: String?,
    val attachments: List<String> = emptyList(),
    val approvedBy: UserDto?,
    val approvedAt: String?,
    val rejectionReason: String?
)


data class MaterialItemDto(
    val name: String,
    val quantity: Int,
    val unit: String?,
    val price: Double?,
    val deliveredQuantity: Int?,
    val notes: String?
)


data class PaymentDto(
    val _id: String,
    val site: SiteDto,
    val amount: Double,
    val date: String,
    val fromUser: UserDto,
    val toUser: UserDto,
    val status: String,
    val type: String,
    val reference: String?,
    val note: String?,
    val approvedBy: UserDto?,
    val approvedAt: String?,
    val paymentMethod: String?,
    val transactionId: String?,
    val invoiceNumber: String?,
    val dueDate: String?,
    val paidAt: String?,
    val attachments: List<String> = emptyList()
)


data class DocumentDto(
    val _id: String,
    val title: String,
    val description: String?,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val mimeType: String,
    val site: SiteDto?,
    val uploadedBy: UserDto,
    val category: String,
    val tags: List<String> = emptyList(),
    val isPublic: Boolean,
    val downloadCount: Int,
    val lastDownloadedAt: String?
)


data class NotificationDto(
    val _id: String,
    val title: String,
    val message: String,
    val recipient: UserDto,
    val sender: UserDto?,
    val type: String,
    val priority: String,
    val isRead: Boolean,
    val readAt: String?,
    val data: Map<String, Any>?,
    val site: SiteDto?,
    val actionUrl: String?,
    val expiresAt: String?
)

// Request DTOs

data class CreateSiteRequest(
    val name: String,
    val location: String?,
    val address: String?,
    val startDate: String?,
    val expectedEndDate: String?,
    val description: String?,
    val budget: Double?,
    val latitude: Double?,
    val longitude: Double?,
    val siteArea: Double?,
    val buildingType: String?,
    val floors: Int?
)


data class UpdateSiteRequest(
    val name: String?,
    val location: String?,
    val address: String?,
    val status: String?,
    val progressPercent: Int?,
    val description: String?,
    val budget: Double?,
    val actualCost: Double?
)


data class CreateTaskRequest(
    val title: String,
    val description: String?,
    val siteId: String,
    val assignedTo: String,
    val priority: String,
    val dueDate: String?,
    val estimatedHours: Double?,
    val tags: List<String> = emptyList()
)


data class UpdateTaskRequest(
    val title: String?,
    val description: String?,
    val status: String?,
    val priority: String?,
    val dueDate: String?,
    val actualHours: Double?,
    val tags: List<String> = emptyList()
)


data class CreateIssueRequest(
    val title: String,
    val description: String,
    val siteId: String,
    val priority: String,
    val category: String,
    val location: String?,
    val estimatedCost: Double?,
    val photos: List<String> = emptyList()
)


data class UpdateIssueRequest(
    val title: String?,
    val description: String?,
    val status: String?,
    val priority: String?,
    val assignedTo: String?,
    val resolution: String?,
    val actualCost: Double?
)


data class CreateMaterialRequestRequest(
    val siteId: String,
    val supplierId: String,
    val items: List<MaterialItemDto>,
    val priority: String,
    val expectedDeliveryDate: String?,
    val notes: String?
)


data class UpdateMaterialStatusRequest(val status: String)


data class UpdateMaterialRequestRequest(
    val items: List<MaterialItemDto>?,
    val priority: String?,
    val expectedDeliveryDate: String?,
    val notes: String?
)


data class CreatePaymentRequest(
    val siteId: String,
    val amount: Double,
    val toUser: String,
    val type: String,
    val reference: String?,
    val note: String?,
    val paymentMethod: String?,
    val dueDate: String?
)


data class UpdatePaymentRequest(
    val status: String?,
    val transactionId: String?,
    val invoiceNumber: String?,
    val paidAt: String?
)


data class UploadDocumentRequest(
    val title: String,
    val description: String?,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val mimeType: String,
    val siteId: String?,
    val category: String,
    val tags: List<String> = emptyList(),
    val isPublic: Boolean = false
)


data class UpdateDocumentRequest(
    val title: String?,
    val description: String?,
    val category: String?,
    val tags: List<String> = emptyList(),
    val isPublic: Boolean?
)


data class UploadFileRequest(val file: String, val fileName: String)


data class UploadFileResponse(val filename: String, val url: String)


data class AddPhotosRequest(val photos: List<String>)


data class AddNotesRequest(val notes: List<String>)

// Stats DTOs

data class SiteStatsDto(
    val totalSites: Int,
    val activeSites: Int,
    val completedSites: Int,
    val totalBudget: Double,
    val actualCost: Double,
    val averageProgress: Double
)


data class IssueStatsDto(
    val totalIssues: Int,
    val openIssues: Int,
    val resolvedIssues: Int,
    val criticalIssues: Int,
    val averageResolutionTime: Double
)


data class DocumentStatsDto(
    val totalDocuments: Int,
    val totalSize: Long,
    val downloads: Int,
    val categories: Map<String, Int>
)


