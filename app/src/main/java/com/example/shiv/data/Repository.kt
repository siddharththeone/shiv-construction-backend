package com.example.shiv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val api: BackendApi, private val tokenStore: TokenStore) {
    
    // Authentication
    suspend fun signup(name: String, email: String?, phone: String?, password: String, role: String, contractorType: String? = null): Result<Unit> = safe {
        val res = api.signup(SignupRequest(name, email, phone, password, role, contractorType))
        tokenStore.saveToken(res.token)
    }

    suspend fun login(email: String, password: String): Result<Unit> = safe {
        val res = api.login(LoginRequest(email = email, password = password))
        tokenStore.saveToken(res.token)
    }

    suspend fun registerFcm(token: String): Result<Unit> = safe {
        api.registerFcm(FcmRequest(token))
    }

    // Sites
    suspend fun listSites(): Result<List<SiteDto>> = safe { api.listSites() }
    suspend fun getSite(id: String): Result<SiteDto> = safe { api.getSite(id) }
    suspend fun createSite(request: CreateSiteRequest): Result<SiteDto> = safe { api.createSite(request) }
    suspend fun updateSite(id: String, request: UpdateSiteRequest): Result<SiteDto> = safe { api.updateSite(id, request) }
    suspend fun deleteSite(id: String): Result<Unit> = safe { api.deleteSite(id) }
    suspend fun addSitePhotos(id: String, photos: List<String>): Result<Unit> = safe { api.addSitePhotos(id, AddPhotosRequest(photos)) }
    suspend fun getSiteStats(): Result<SiteStatsDto> = safe { api.getSiteStats() }

    // Tasks
    suspend fun listTasks(): Result<List<TaskDto>> = safe { api.listTasks() }
    suspend fun getTask(id: String): Result<TaskDto> = safe { api.getTask(id) }
    suspend fun createTask(request: CreateTaskRequest): Result<TaskDto> = safe { api.createTask(request) }
    suspend fun updateTask(id: String, request: UpdateTaskRequest): Result<TaskDto> = safe { api.updateTask(id, request) }
    suspend fun deleteTask(id: String): Result<Unit> = safe { api.deleteTask(id) }
    suspend fun addTaskPhotos(id: String, photos: List<String>): Result<Unit> = safe { api.addTaskPhotos(id, AddPhotosRequest(photos)) }
    suspend fun addTaskNotes(id: String, notes: List<String>): Result<Unit> = safe { api.addTaskNotes(id, AddNotesRequest(notes)) }

    // Issues
    suspend fun listIssues(): Result<List<IssueDto>> = safe { api.listIssues() }
    suspend fun getIssue(id: String): Result<IssueDto> = safe { api.getIssue(id) }
    suspend fun createIssue(request: CreateIssueRequest): Result<IssueDto> = safe { api.createIssue(request) }
    suspend fun updateIssue(id: String, request: UpdateIssueRequest): Result<IssueDto> = safe { api.updateIssue(id, request) }
    suspend fun deleteIssue(id: String): Result<Unit> = safe { api.deleteIssue(id) }
    suspend fun addIssuePhotos(id: String, photos: List<String>): Result<Unit> = safe { api.addIssuePhotos(id, AddPhotosRequest(photos)) }
    suspend fun getIssueStats(): Result<IssueStatsDto> = safe { api.getIssueStats() }

    // Materials
    suspend fun listMaterialRequests(): Result<List<MaterialRequestDto>> = safe { api.listMaterialRequests() }
    suspend fun getMaterialRequest(id: String): Result<MaterialRequestDto> = safe { api.getMaterialRequest(id) }
    suspend fun createMaterialRequest(request: CreateMaterialRequestRequest): Result<MaterialRequestDto> = safe { api.createMaterialRequest(request) }
    suspend fun updateMaterialStatus(id: String, status: String): Result<MaterialRequestDto> = safe { api.updateMaterialStatus(id, UpdateMaterialStatusRequest(status)) }
    suspend fun updateMaterialRequest(id: String, request: UpdateMaterialRequestRequest): Result<MaterialRequestDto> = safe { api.updateMaterialRequest(id, request) }
    suspend fun cancelMaterialRequest(id: String): Result<Unit> = safe { api.cancelMaterialRequest(id) }

    // Payments
    suspend fun listPayments(): Result<List<PaymentDto>> = safe { api.listPayments() }
    suspend fun getPayment(id: String): Result<PaymentDto> = safe { api.getPayment(id) }
    suspend fun createPayment(request: CreatePaymentRequest): Result<PaymentDto> = safe { api.createPayment(request) }
    suspend fun updatePayment(id: String, request: UpdatePaymentRequest): Result<PaymentDto> = safe { api.updatePayment(id, request) }
    suspend fun cancelPayment(id: String): Result<Unit> = safe { api.cancelPayment(id) }

    // Documents
    suspend fun listDocuments(): Result<List<DocumentDto>> = safe { api.listDocuments() }
    suspend fun getDocument(id: String): Result<DocumentDto> = safe { api.getDocument(id) }
    suspend fun uploadDocument(request: UploadDocumentRequest): Result<DocumentDto> = safe { api.uploadDocument(request) }
    suspend fun updateDocument(id: String, request: UpdateDocumentRequest): Result<DocumentDto> = safe { api.updateDocument(id, request) }
    suspend fun deleteDocument(id: String): Result<Unit> = safe { api.deleteDocument(id) }
    suspend fun recordDocumentDownload(id: String): Result<Unit> = safe { api.recordDocumentDownload(id) }
    suspend fun getDocumentStats(): Result<DocumentStatsDto> = safe { api.getDocumentStats() }

    // Notifications
    suspend fun listNotifications(): Result<List<NotificationDto>> = safe { api.listNotifications() }
    suspend fun markNotificationRead(id: String): Result<Unit> = safe { api.markNotificationRead(id) }
    suspend fun deleteNotification(id: String): Result<Unit> = safe { api.deleteNotification(id) }

    // File Uploads
    suspend fun uploadFile(file: String, fileName: String): Result<UploadFileResponse> = safe { api.uploadFile(UploadFileRequest(file, fileName)) }

    private suspend fun <T> safe(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

interface TokenStore {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}

class InMemoryTokenStore : TokenStore {
    private var token: String? = null
    override fun getToken(): String? = token
    override fun saveToken(token: String) { this.token = token }
    override fun clearToken() { this.token = null }
}


