package com.example.shiv.ui.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shiv.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// State classes for different features
data class OwnerSitesState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sites: List<SiteDto> = emptyList(),
    val stats: SiteStatsDto? = null
)

data class TasksState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val tasks: List<TaskDto> = emptyList()
)

data class IssuesState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val issues: List<IssueDto> = emptyList(),
    val stats: IssueStatsDto? = null
)

data class MaterialsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val materialRequests: List<MaterialRequestDto> = emptyList()
)

data class PaymentsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val payments: List<PaymentDto> = emptyList()
)

data class DocumentsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val documents: List<DocumentDto> = emptyList(),
    val stats: DocumentStatsDto? = null
)

data class NotificationsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0
)

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val user: UserDto? = null
)

class OwnerSitesViewModel(private val repo: Repository) : ViewModel() {
    
    // State flows for different features
    private val _sitesState = MutableStateFlow(OwnerSitesState())
    val sitesState: StateFlow<OwnerSitesState> = _sitesState

    private val _tasksState = MutableStateFlow(TasksState())
    val tasksState: StateFlow<TasksState> = _tasksState

    private val _issuesState = MutableStateFlow(IssuesState())
    val issuesState: StateFlow<IssuesState> = _issuesState

    private val _materialsState = MutableStateFlow(MaterialsState())
    val materialsState: StateFlow<MaterialsState> = _materialsState

    private val _paymentsState = MutableStateFlow(PaymentsState())
    val paymentsState: StateFlow<PaymentsState> = _paymentsState

    private val _documentsState = MutableStateFlow(DocumentsState())
    val documentsState: StateFlow<DocumentsState> = _documentsState

    private val _notificationsState = MutableStateFlow(NotificationsState())
    val notificationsState: StateFlow<NotificationsState> = _notificationsState

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    // Authentication methods
    fun login(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.login(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState(isAuthenticated = true)
            } else {
                AuthState(error = result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun signup(name: String, email: String?, phone: String?, password: String, role: String, contractorType: String? = null) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.signup(name, email, phone, password, role, contractorType)
            _authState.value = if (result.isSuccess) {
                AuthState(isAuthenticated = true)
            } else {
                AuthState(error = result.exceptionOrNull()?.message ?: "Signup failed")
            }
        }
    }

    // Sites methods
    fun loadSites() {
        _sitesState.value = _sitesState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.listSites()
            _sitesState.value = if (result.isSuccess) {
                OwnerSitesState(sites = result.getOrThrow())
            } else {
                OwnerSitesState(error = result.exceptionOrNull()?.message ?: "Failed to load sites")
            }
        }
    }

    fun loadSiteStats() {
        viewModelScope.launch {
            val result = repo.getSiteStats()
            if (result.isSuccess) {
                _sitesState.value = _sitesState.value.copy(stats = result.getOrThrow())
            }
        }
    }

    fun createSite(request: CreateSiteRequest) {
        viewModelScope.launch {
            val result = repo.createSite(request)
            if (result.isSuccess) {
                loadSites() // Reload sites after creation
            } else {
                _sitesState.value = _sitesState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to create site")
            }
        }
    }

    fun updateSite(id: String, request: UpdateSiteRequest) {
        viewModelScope.launch {
            val result = repo.updateSite(id, request)
            if (result.isSuccess) {
                loadSites() // Reload sites after update
            } else {
                _sitesState.value = _sitesState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to update site")
            }
        }
    }

    fun deleteSite(id: String) {
        viewModelScope.launch {
            val result = repo.deleteSite(id)
            if (result.isSuccess) {
                loadSites() // Reload sites after deletion
            } else {
                _sitesState.value = _sitesState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to delete site")
            }
        }
    }

    // Tasks methods
    fun loadTasks() {
        _tasksState.value = _tasksState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.listTasks()
            _tasksState.value = if (result.isSuccess) {
                TasksState(tasks = result.getOrThrow())
            } else {
                TasksState(error = result.exceptionOrNull()?.message ?: "Failed to load tasks")
            }
        }
    }

    fun createTask(request: CreateTaskRequest) {
        viewModelScope.launch {
            val result = repo.createTask(request)
            if (result.isSuccess) {
                loadTasks() // Reload tasks after creation
            } else {
                _tasksState.value = _tasksState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to create task")
            }
        }
    }

    fun updateTask(id: String, request: UpdateTaskRequest) {
        viewModelScope.launch {
            val result = repo.updateTask(id, request)
            if (result.isSuccess) {
                loadTasks() // Reload tasks after update
            } else {
                _tasksState.value = _tasksState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to update task")
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            val result = repo.deleteTask(id)
            if (result.isSuccess) {
                loadTasks() // Reload tasks after deletion
            } else {
                _tasksState.value = _tasksState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to delete task")
            }
        }
    }

    // Issues methods
    fun loadIssues() {
        _issuesState.value = _issuesState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.listIssues()
            _issuesState.value = if (result.isSuccess) {
                IssuesState(issues = result.getOrThrow())
            } else {
                IssuesState(error = result.exceptionOrNull()?.message ?: "Failed to load issues")
            }
        }
    }

    fun loadIssueStats() {
        viewModelScope.launch {
            val result = repo.getIssueStats()
            if (result.isSuccess) {
                _issuesState.value = _issuesState.value.copy(stats = result.getOrThrow())
            }
        }
    }

    fun createIssue(request: CreateIssueRequest) {
        viewModelScope.launch {
            val result = repo.createIssue(request)
            if (result.isSuccess) {
                loadIssues() // Reload issues after creation
            } else {
                _issuesState.value = _issuesState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to create issue")
            }
        }
    }

    fun updateIssue(id: String, request: UpdateIssueRequest) {
        viewModelScope.launch {
            val result = repo.updateIssue(id, request)
            if (result.isSuccess) {
                loadIssues() // Reload issues after update
            } else {
                _issuesState.value = _issuesState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to update issue")
            }
        }
    }

    fun deleteIssue(id: String) {
        viewModelScope.launch {
            val result = repo.deleteIssue(id)
            if (result.isSuccess) {
                loadIssues() // Reload issues after deletion
            } else {
                _issuesState.value = _issuesState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to delete issue")
            }
        }
    }

    // Materials methods
    fun loadMaterialRequests() {
        _materialsState.value = _materialsState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.listMaterialRequests()
            _materialsState.value = if (result.isSuccess) {
                MaterialsState(materialRequests = result.getOrThrow())
            } else {
                MaterialsState(error = result.exceptionOrNull()?.message ?: "Failed to load material requests")
            }
        }
    }

    fun createMaterialRequest(request: CreateMaterialRequestRequest) {
        viewModelScope.launch {
            val result = repo.createMaterialRequest(request)
            if (result.isSuccess) {
                loadMaterialRequests() // Reload material requests after creation
            } else {
                _materialsState.value = _materialsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to create material request")
            }
        }
    }

    fun updateMaterialStatus(id: String, status: String) {
        viewModelScope.launch {
            val result = repo.updateMaterialStatus(id, status)
            if (result.isSuccess) {
                loadMaterialRequests() // Reload material requests after update
            } else {
                _materialsState.value = _materialsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to update material status")
            }
        }
    }

    fun cancelMaterialRequest(id: String) {
        viewModelScope.launch {
            val result = repo.cancelMaterialRequest(id)
            if (result.isSuccess) {
                loadMaterialRequests() // Reload material requests after cancellation
            } else {
                _materialsState.value = _materialsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to cancel material request")
            }
        }
    }

    // Payments methods
    fun loadPayments() {
        _paymentsState.value = _paymentsState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.listPayments()
            _paymentsState.value = if (result.isSuccess) {
                PaymentsState(payments = result.getOrThrow())
            } else {
                PaymentsState(error = result.exceptionOrNull()?.message ?: "Failed to load payments")
            }
        }
    }

    fun createPayment(request: CreatePaymentRequest) {
        viewModelScope.launch {
            val result = repo.createPayment(request)
            if (result.isSuccess) {
                loadPayments() // Reload payments after creation
            } else {
                _paymentsState.value = _paymentsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to create payment")
            }
        }
    }

    fun updatePayment(id: String, request: UpdatePaymentRequest) {
        viewModelScope.launch {
            val result = repo.updatePayment(id, request)
            if (result.isSuccess) {
                loadPayments() // Reload payments after update
            } else {
                _paymentsState.value = _paymentsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to update payment")
            }
        }
    }

    fun cancelPayment(id: String) {
        viewModelScope.launch {
            val result = repo.cancelPayment(id)
            if (result.isSuccess) {
                loadPayments() // Reload payments after cancellation
            } else {
                _paymentsState.value = _paymentsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to cancel payment")
            }
        }
    }

    // Documents methods
    fun loadDocuments() {
        _documentsState.value = _documentsState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.listDocuments()
            _documentsState.value = if (result.isSuccess) {
                DocumentsState(documents = result.getOrThrow())
            } else {
                DocumentsState(error = result.exceptionOrNull()?.message ?: "Failed to load documents")
            }
        }
    }

    fun loadDocumentStats() {
        viewModelScope.launch {
            val result = repo.getDocumentStats()
            if (result.isSuccess) {
                _documentsState.value = _documentsState.value.copy(stats = result.getOrThrow())
            }
        }
    }

    fun uploadDocument(request: UploadDocumentRequest) {
        viewModelScope.launch {
            val result = repo.uploadDocument(request)
            if (result.isSuccess) {
                loadDocuments() // Reload documents after upload
            } else {
                _documentsState.value = _documentsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to upload document")
            }
        }
    }

    fun updateDocument(id: String, request: UpdateDocumentRequest) {
        viewModelScope.launch {
            val result = repo.updateDocument(id, request)
            if (result.isSuccess) {
                loadDocuments() // Reload documents after update
            } else {
                _documentsState.value = _documentsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to update document")
            }
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            val result = repo.deleteDocument(id)
            if (result.isSuccess) {
                loadDocuments() // Reload documents after deletion
            } else {
                _documentsState.value = _documentsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to delete document")
            }
        }
    }

    // Notifications methods
    fun loadNotifications() {
        _notificationsState.value = _notificationsState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repo.listNotifications()
            _notificationsState.value = if (result.isSuccess) {
                val notifications = result.getOrThrow()
                val unreadCount = notifications.count { !it.isRead }
                NotificationsState(notifications = notifications, unreadCount = unreadCount)
            } else {
                NotificationsState(error = result.exceptionOrNull()?.message ?: "Failed to load notifications")
            }
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            val result = repo.markNotificationRead(id)
            if (result.isSuccess) {
                loadNotifications() // Reload notifications after marking as read
            } else {
                _notificationsState.value = _notificationsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to mark notification as read")
            }
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            val result = repo.deleteNotification(id)
            if (result.isSuccess) {
                loadNotifications() // Reload notifications after deletion
            } else {
                _notificationsState.value = _notificationsState.value.copy(error = result.exceptionOrNull()?.message ?: "Failed to delete notification")
            }
        }
    }

    // Utility methods
    fun clearError() {
        _sitesState.value = _sitesState.value.copy(error = null)
        _tasksState.value = _tasksState.value.copy(error = null)
        _issuesState.value = _issuesState.value.copy(error = null)
        _materialsState.value = _materialsState.value.copy(error = null)
        _paymentsState.value = _paymentsState.value.copy(error = null)
        _documentsState.value = _documentsState.value.copy(error = null)
        _notificationsState.value = _notificationsState.value.copy(error = null)
        _authState.value = _authState.value.copy(error = null)
    }

    fun refreshAll() {
        loadSites()
        loadTasks()
        loadIssues()
        loadMaterialRequests()
        loadPayments()
        loadDocuments()
        loadNotifications()
    }
}


