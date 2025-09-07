# 🔧 API Integration Guide - Connect Mobile App to Real API

## 🚨 **Current Problem:**
Your mobile app is using **mock data** instead of the **real API**. Here's what needs to be fixed:

## 📱 **What's Currently Happening:**
- LoginScreen uses `AuthManager.login()` (mock)
- All data comes from `MockData.sites` (fake)
- No real network calls to your server

## ✅ **What's Already Working:**
- Backend API: ✅ Running on http://localhost:4000
- API Integration Files: ✅ `Api.kt`, `Repository.kt`, `OwnerSitesViewModel.kt`
- Mobile UI: ✅ All screens and components

## 🔧 **Step 1: Update LoginScreen**

Replace the current LoginScreen button click with real API call:

```kotlin
// CURRENT (MOCK):
Button(
    onClick = {
        if (AuthManager.login(email, password)) {
            onLoginSuccess()
        } else {
            errorMessage = LocaleManager.getString("Invalid credentials")
        }
    }
)

// NEW (REAL API):
Button(
    onClick = {
        isLoading = true
        errorMessage = ""
        
        context.lifecycleScope.launch {
            try {
                val tokenStore = object : TokenStore {
                    private var token: String? = null
                    override fun getToken(): String? = token
                    override fun saveToken(token: String) { this.token = token }
                    override fun clearToken() { this.token = null }
                }
                
                val api = ApiClientFactory.create(tokenProvider = { tokenStore.getToken() })
                val repository = Repository(api, tokenStore)
                
                val result = repository.login(email, password)
                if (result.isSuccess) {
                    AuthManager.currentUserRole = UserRole.OWNER
                    onLoginSuccess()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Network error"
            } finally {
                isLoading = false
            }
        }
    }
)
```

## 🔧 **Step 2: Update Dashboards**

Replace mock data with real API calls:

```kotlin
// CURRENT (MOCK):
items(MockData.sites) { site ->
    SiteCard(site = site, onClick = { onSiteClick(site.id) })
}

// NEW (REAL API):
val viewModel = remember { OwnerSitesViewModel(repository) }
val sitesState by viewModel.sitesState.collectAsState()

LaunchedEffect(Unit) {
    viewModel.loadSites()
}

items(sitesState.sites) { site ->
    SiteCard(site = site, onClick = { onSiteClick(site._id) })
}
```

## 🔧 **Step 3: Add Required Imports**

Add these imports to MainActivity.kt:

```kotlin
import com.example.shiv.data.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
```

## 🎯 **Test Credentials:**

Use these credentials to test the real API:
- **Email**: `siddharthshardul27@gmail.com`
- **Password**: `Sid@123`
- **Role**: OWNER

## 🚀 **Quick Test:**

1. **Start the server**: `cd server && npm run test-server`
2. **Test API**: `curl http://localhost:4000/api/test`
3. **Test login**: `curl -X POST http://localhost:4000/api/auth/login -H "Content-Type: application/json" -d '{"email":"siddharthshardul27@gmail.com","password":"Sid@123"}'`

## ✅ **Expected Result:**

After these changes:
- ✅ Real authentication with your server
- ✅ Real data loading from API
- ✅ No more "Invalid credentials" error
- ✅ Actual construction sites data
- ✅ Real-time updates

## 🔧 **Next Steps:**

1. **Update LoginScreen** (Step 1 above)
2. **Update Dashboards** (Step 2 above)
3. **Add imports** (Step 3 above)
4. **Test the app** with real credentials

The API integration files are already complete - we just need to connect them to the UI!





