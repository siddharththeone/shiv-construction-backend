@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shiv

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.shiv.ui.theme.SHIVTheme
import com.example.shiv.data.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import java.io.IOException
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import java.io.Serializable
import java.text.NumberFormat
import java.util.*
import kotlin.math.min

// Enums
enum class ContractorType : Serializable {
    GENERAL, ELECTRIC, PLUMBER, FABRICATION
}

enum class UserRole : Serializable {
    OWNER, CONTRACTOR, SUPPLIER, NONE
}

// Language Manager
object LocaleManager {
    var currentLanguage by mutableStateOf("English")
    private val translations = mapOf(
        "English" to mapOf(
            "Construction Site Management" to "Construction Site Management",
            "Select your role to continue" to "Select your role to continue",
            "Owner" to "Owner",
            "Contractor" to "Contractor",
            "Supplier" to "Supplier",
            "Select Contractor Type" to "Select Contractor Type",
            "Owner Dashboard" to "Owner Dashboard",
            "Contractor Dashboard" to "{type} Contractor Dashboard",
            "Supplier Dashboard" to "Supplier Dashboard",
            "Site Details" to "Site Details",
            "Your Sites" to "Your Sites",
            "Assigned Sites" to "Assigned Sites",
            "Material Requests" to "Material Requests",
            "Notifications" to "Notifications",
            "Settings" to "Settings",
            "Select Language" to "Select Language",
            "Total Sites" to "Total Sites",
            "Active" to "Active",
            "Budget" to "Budget",
            "Assigned" to "Assigned",
            "In Progress" to "In Progress",
            "Completed" to "Completed",
            "Orders" to "Orders",
            "Pending" to "Pending",
            "Revenue" to "Revenue",
            "Site Information" to "Site Information",
            "Location" to "Location",
            "Progress" to "Progress",
            "Materials" to "Materials",
            "Payments" to "Payments",
            "Tasks" to "Tasks",
            "Site Photos" to "Site Photos",
            "Request" to "Request",
            "Payment" to "Payment",
            "Add Site" to "Add Site",
            "Reports" to "Reports",
            "Tasks Button" to "Tasks",
            "Add Material" to "Add Material",
            "Add Payment" to "Add Payment",
            "Take Photo" to "Take Photo",
            "Search" to "Search",
            "Filter by Status" to "Filter by Status",
            "No sites found" to "No sites found",
            "Invalid site ID" to "Invalid site ID",
            "No material requests found" to "No material requests found",
            "No notifications" to "No notifications",
            "Welcome to Construction Site Management" to "Welcome to Construction Site Management",
            "Choose your role to manage construction sites efficiently." to "Choose your role to manage construction sites efficiently.",
            "Login" to "Login",
            "Signup" to "Signup",
            "Email" to "Email",
            "Password" to "Password",
            "Invalid credentials" to "Invalid credentials",
            "Signup failed" to "Signup failed",
            "Role" to "Role",
            "Documents" to "Documents",
            "Issues" to "Issues",
            "Add Task" to "Add Task",
            "Add Issue" to "Add Issue",
            "Description" to "Description",
            "Priority" to "Priority",
            "Due Date" to "Due Date",
            "Assigned To" to "Assigned To",
            "Upload Document" to "Upload Document",
            "Upload Photo" to "Upload Photo",
            "No documents found" to "No documents found",
            "No issues found" to "No issues found",
            "No photos found" to "No photos found",
            "Due" to "Due",
            "Status" to "Status"
        ),
        "Hindi" to mapOf(
            "Construction Site Management" to "निर्माण स्थल प्रबंधन",
            "Select your role to continue" to "जारी रखने के लिए अपनी भूमिका चुनें",
            "Owner" to "मालिक",
            "Contractor" to "ठेकेदार",
            "Supplier" to "आपूर्तिकर्ता",
            "Select Contractor Type" to "ठेकेदार प्रकार चुनें",
            "Owner Dashboard" to "मालिक डैशबोर्ड",
            "Contractor Dashboard" to "{type} ठेकेदार डैशबोर्ड",
            "Supplier Dashboard" to "आपूर्तिकर्ता डैशबोर्ड",
            "Site Details" to "स्थल विवरण",
            "Your Sites" to "आपके स्थल",
            "Assigned Sites" to "नियुक्त स्थल",
            "Material Requests" to "सामग्री अनुरोध",
            "Notifications" to "सूचनाएँ",
            "Settings" to "सेटिंग्स",
            "Select Language" to "भाषा चुनें",
            "Total Sites" to "कुल स्थल",
            "Active" to "सक्रिय",
            "Budget" to "बजट",
            "Assigned" to "नियुक्त",
            "In Progress" to "प्रगति पर",
            "Completed" to "पूरा हुआ",
            "Orders" to "आदेश",
            "Pending" to "लंबित",
            "Revenue" to "राजस्व",
            "Site Information" to "स्थल जानकारी",
            "Location" to "स्थान",
            "Progress" to "प्रगति",
            "Materials" to "सामग्री",
            "Payments" to "भुगतान",
            "Tasks" to "कार्य",
            "Site Photos" to "स्थल तस्वीरें",
            "Request" to "अनुरोध",
            "Payment" to "भुगतान",
            "Add Site" to "स्थल जोड़ें",
            "Reports" to "रिपोर्ट्स",
            "Tasks Button" to "कार्य",
            "Add Material" to "सामग्री जोड़ें",
            "Add Payment" to "भुगतान जोड़ें",
            "Take Photo" to "तस्वीर लें",
            "Search" to "खोजें",
            "Filter by Status" to "स्थिति के अनुसार फ़िल्टर करें",
            "No sites found" to "कोई स्थल नहीं मिला",
            "Invalid site ID" to "अमान्य स्थल ID",
            "No material requests found" to "कोई सामग्री अनुरोध नहीं मिला",
            "No notifications" to "कोई सूचनाएँ नहीं",
            "Welcome to Construction Site Management" to "निर्माण स्थल प्रबंधन में आपका स्वागत है",
            "Choose your role to manage construction sites efficiently." to "निर्माण स्थलों को कुशलतापूर्वक प्रबंधित करने के लिए अपनी भूमिका चुनें।",
            "Login" to "लॉगिन",
            "Signup" to "साइनअप",
            "Email" to "ईमेल",
            "Password" to "पासवर्ड",
            "Invalid credentials" to "अमान्य क्रेडेंशियल्स",
            "Signup failed" to "साइनअप विफल",
            "Role" to "भूमिका",
            "Documents" to "दस्तावेज़",
            "Issues" to "मुद्दे",
            "Add Task" to "कार्य जोड़ें",
            "Add Issue" to "मुद्दा जोड़ें",
            "Description" to "विवरण",
            "Priority" to "प्राथमिकता",
            "Due Date" to "नियत तारीख",
            "Assigned To" to "नियुक्त किया गया",
            "Upload Document" to "दस्तावेज़ अपलोड करें",
            "Upload Photo" to "फोटो अपलोड करें",
            "No documents found" to "कोई दस्तावेज़ नहीं मिला",
            "No issues found" to "कोई मुद्दे नहीं मिले",
            "No photos found" to "कोई फोटो नहीं मिली",
            "Due" to "नियत",
            "Status" to "स्थिति"
        ),
        "Marathi" to mapOf(
            "Construction Site Management" to "बांधकाम स्थळ व्यवस्थापन",
            "Select your role to continue" to "पुढे जाण्यासाठी तुमची भूमिका निवडा",
            "Owner" to "मालक",
            "Contractor" to "कंत्राटदार",
            "Supplier" to "पुरवठादार",
            "Select Contractor Type" to "कंत्राटदार प्रकार निवडा",
            "Owner Dashboard" to "मालक डॅशबोर्ड",
            "Contractor Dashboard" to "{type} कंत्राटदार डॅशबोर्ड",
            "Supplier Dashboard" to "पुरवठादार डॅशबोर्ड",
            "Site Details" to "स्थळ तपशील",
            "Your Sites" to "तुमचे स्थळ",
            "Assigned Sites" to "नियुक्त स्थळ",
            "Material Requests" to "सामग्री विनंत्या",
            "Notifications" to "सूचना",
            "Settings" to "सेटिंग्ज",
            "Select Language" to "भाषा निवडा",
            "Total Sites" to "एकूण स्थळ",
            "Active" to "सक्रिय",
            "Budget" to "बजेट",
            "Assigned" to "नियुक्त",
            "In Progress" to "प्रगतीपथावर",
            "Completed" to "पूर्ण झाले",
            "Orders" to "ऑर्डर",
            "Pending" to "प्रलंबित",
            "Revenue" to "महसूल",
            "Site Information" to "स्थळ माहिती",
            "Location" to "स्थान",
            "Progress" to "प्रगती",
            "Materials" to "सामग्री",
            "Payments" to "पेमेंट्स",
            "Tasks" to "कार्य",
            "Site Photos" to "स्थळ छायाचित्रे",
            "Request" to "विनंती",
            "Payment" to "पेमेंट",
            "Add Site" to "स्थळ जोडा",
            "Reports" to "रिपोर्ट्स",
            "Tasks Button" to "कार्य",
            "Add Material" to "सामग्री जोडा",
            "Add Payment" to "पेमेंट जोडा",
            "Take Photo" to "छायाचित्र घ्या",
            "Search" to "शोधा",
            "Filter by Status" to "स्थितीनुसार फिल्टर करा",
            "No sites found" to "कोणतेही स्थळ सापडले नाही",
            "Invalid site ID" to "अवैध स्थळ ID",
            "No material requests found" to "कोणत्याही सामग्री विनंत्या सापडल्या नाहीत",
            "No notifications" to "कोणत्याही सूचना नाहीत",
            "Welcome to Construction Site Management" to "बांधकाम स्थळ व्यवस्थापनात आपले स्वागत आहे",
            "Choose your role to manage construction sites efficiently." to "बांधकाम स्थळांचे कार्यक्षम व्यवस्थापन करण्यासाठी तुमची भूमिका निवडा.",
            "Login" to "लॉगिन",
            "Signup" to "साइनअप",
            "Email" to "ईमेल",
            "Password" to "पासवर्ड",
            "Invalid credentials" to "अवैध क्रेडेन्शियल्स",
            "Signup failed" to "साइनअप अयशस्वी",
            "Role" to "भूमिका",
            "Documents" to "कागदपत्रे",
            "Issues" to "समस्या",
            "Add Task" to "कार्य जोडा",
            "Add Issue" to "समस्या जोडा",
            "Description" to "वर्णन",
            "Priority" to "प्राधान्य",
            "Due Date" to "नियत तारीख",
            "Assigned To" to "नियुक्त केलेले",
            "Upload Document" to "कागदपत्र अपलोड करा",
            "Upload Photo" to "छायाचित्र अपलोड करा",
            "No documents found" to "कोणतेही कागदपत्रे सापडली नाहीत",
            "No issues found" to "कोणत्याही समस्या सापडल्या नाहीत",
            "No photos found" to "कोणतेही छायाचित्र सापडले नाही",
            "Due" to "नियत",
            "Status" to "स्थिती"
        )
    )

    fun getString(key: String, vararg args: String): String {
        val translation = translations[currentLanguage]?.get(key) ?: key
        return if (args.isNotEmpty()) {
            translation.replace("{type}", args[0])
        } else {
            translation
        }
    }

    fun formatCurrency(amount: String): String {
        val cleanAmount = amount.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return formatter.format(cleanAmount)
    }

    fun formatQuantity(quantity: String): String {
        return quantity.replace("tons", "tonnes").replace("kg", "kilograms")
    }
}

// Auth Manager
object AuthManager {
    var currentUserRole by mutableStateOf(UserRole.NONE)
    var currentUserName by mutableStateOf("")
    var currentContractorType by mutableStateOf(ContractorType.GENERAL) // For contractors

    fun login(email: String, password: String): Boolean {
        return when {
            email == "owner@example.com" && password == "pass" -> {
                currentUserRole = UserRole.OWNER
                currentUserName = "Owner"
                true
            }
            email == "contractor@example.com" && password == "pass" -> {
                currentUserRole = UserRole.CONTRACTOR
                currentUserName = "Contractor"
                true
            }
            email == "supplier@example.com" && password == "pass" -> {
                currentUserRole = UserRole.SUPPLIER
                currentUserName = "Supplier"
                true
            }
            else -> false
        }
    }

    fun signup(email: String, password: String, role: UserRole): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            currentUserRole = role
            currentUserName = email.split("@")[0].replaceFirstChar { it.uppercase() }
            return true
        }
        return false
    }

    fun logout() {
        currentUserRole = UserRole.NONE
        currentUserName = ""
        currentContractorType = ContractorType.GENERAL
    }
}

// Helper function to get the appropriate dashboard route based on user role
fun getDashboardRoute(): String {
    return when (AuthManager.currentUserRole) {
        UserRole.OWNER -> "dashboard/owner"
        UserRole.CONTRACTOR -> "dashboard/contractor/${AuthManager.currentContractorType.name}"
        UserRole.SUPPLIER -> "dashboard/supplier"
        UserRole.NONE -> "login"
    }
}

// Data classes
@Immutable
data class SiteItem(
    val id: String,
    val name: String,
    val location: String,
    val status: String,
    val progress: Float,
    val budget: String,
    val startDate: String
)

@Immutable
data class MaterialRequest(
    val id: String,
    val site: String,
    val material: String,
    val quantity: String,
    val status: String,
    val requestDate: String,
    val supplier: String
)

@Immutable
data class MaterialItem(
    val name: String,
    val quantity: String,
    val status: String,
    val cost: String
)

@Immutable
data class Supplier(
    val id: String,
    val name: String,
    val materials: List<String>
)

@Immutable
data class PaymentItem(
    val description: String,
    val amount: String,
    val status: String,
    val date: String
)

@Immutable
data class NotificationItem(
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String
)

@Immutable
data class TaskItem(
    val id: String,
    val siteId: String,
    val description: String,
    val contractorType: ContractorType,
    val status: String,
    val dueDate: String,
    val assignedTo: String = "",
    val priority: String = "Medium"
)

@Immutable
data class DocumentItem(
    val id: String,
    val name: String,
    val uri: Uri,
    val uploadDate: String
)

@Immutable
data class IssueItem(
    val id: String,
    val siteId: String,
    val description: String,
    val status: String,
    val priority: String,
    val reportedBy: String,
    val dueDate: String
)

@Immutable
data class PhotoItem(
    val id: String,
    val uri: Uri,
    val caption: String,
    val uploadDate: String
)

// Mock Data
object MockData {
    val sites = listOf(
        SiteItem("1", "Downtown Plaza", "Main St, Downtown", "In Progress", 0.65f, "₹2500000", "Jan 15, 2024"),
        SiteItem("2", "Residential Complex", "Oak Avenue", "Planning", 0.15f, "₹4200000", "Mar 1, 2024"),
        SiteItem("3", "Office Building", "Business District", "In Progress", 0.85f, "₹1800000", "Nov 10, 2023")
    )

    val contractorSitesByType: Map<ContractorType, List<SiteItem>> = mapOf(
        ContractorType.GENERAL to sites,
        ContractorType.ELECTRIC to sites.subList(0, 1),
        ContractorType.PLUMBER to sites.subList(1, 2),
        ContractorType.FABRICATION to sites.subList(0, 2)
    )

    var tasks = mutableListOf(
        TaskItem("1", "1", "Install wiring", ContractorType.ELECTRIC, "Pending", "Sep 1, 2025", "Electrician1", "High"),
        TaskItem("2", "1", "Foundation work", ContractorType.GENERAL, "In Progress", "Aug 30, 2025", "General1", "Medium"),
        TaskItem("3", "2", "Plumbing setup", ContractorType.PLUMBER, "Pending", "Sep 5, 2025", "Plumber1", "Low"),
        TaskItem("4", "3", "Steel framework", ContractorType.FABRICATION, "Completed", "Aug 20, 2025", "Fabricator1", "High")
    )

    var materials = mutableListOf(
        MaterialItem("Concrete", "50 tonnes", "Delivered", "₹1500000"),
        MaterialItem("Steel Rebar", "2000 kilograms", "Pending", "₹850000"),
        MaterialItem("Bricks", "10000 units", "Delivered", "₹520000")
    )

    var payments = mutableListOf(
        PaymentItem("Material Purchase", "₹1500000", "Paid", "Dec 15, 2024"),
        PaymentItem("Labor Cost", "₹2500000", "Pending", "Dec 20, 2024"),
        PaymentItem("Equipment Rental", "₹850000", "Paid", "Dec 10, 2024")
    )

    val notifications = listOf(
        NotificationItem(
            "Material Delivery",
            "Concrete delivery scheduled for tomorrow at 9 AM",
            "2 hours ago",
            "delivery"
        ),
        NotificationItem(
            "Payment Due",
            "Payment of ₹2500000 due in 3 days",
            "1 day ago",
            "payment"
        ),
        NotificationItem(
            "Site Inspection",
            "Quality inspection completed successfully",
            "3 days ago",
            "inspection"
        )
    )

    var materialRequests = mutableListOf(
        MaterialRequest("1", "Downtown Plaza", "Concrete", "50 tonnes", "Pending", "Dec 18, 2024", "ABC Supplies"),
        MaterialRequest("2", "Office Building", "Steel Rebar", "2000 kilograms", "In Transit", "Dec 20, 2024", "Steel Corp"),
        MaterialRequest("3", "Residential Complex", "Bricks", "15000 units", "Delivered", "Dec 15, 2024", "Build Mart")
    )

    val suppliers = listOf(
        Supplier("1", "ABC Supplies", listOf("Concrete", "Bricks")),
        Supplier("2", "Steel Corp", listOf("Steel Rebar", "Concrete")),
        Supplier("3", "Build Mart", listOf("Concrete", "Steel Rebar", "Bricks"))
    )

    var documents = mutableListOf<DocumentItem>()
    var issues = mutableListOf(
        IssueItem("1", "1", "Crack in foundation", "Open", "High", "Owner", "Sep 5, 2025"),
        IssueItem("2", "1", "Wiring issue", "Open", "Medium", "Contractor", "Sep 10, 2025")
    )
    var photos = mutableListOf<PhotoItem>()

    fun getPaginatedItems(items: List<Any>, page: Int, pageSize: Int): List<Any> {
        val start = page * pageSize
        return if (start < items.size) items.subList(start, minOf(start + pageSize, items.size)) else emptyList()
    }
}

// UI Components
@Composable
fun HorizontalScrollableTabs(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(tabs) { tab ->
            FilterChip(
                onClick = { onTabSelected(tab) },
                label = { Text(tab) },
                selected = selectedTab == tab,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun BottomNavigationTabs(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            "Overview" -> Icons.Default.Dashboard
                            "Sites" -> Icons.Default.LocationOn
                            "Reports" -> Icons.Default.Assessment
                            "Tasks" -> Icons.Default.Assignment
                            "Materials" -> Icons.Default.Inventory
                            "Requests" -> Icons.Default.RequestPage
                            "Orders" -> Icons.Default.ShoppingCart
                            "Payments" -> Icons.Default.Payment
                            "Photos" -> Icons.Default.PhotoCamera
                            "Documents" -> Icons.Default.Description
                            "Issues" -> Icons.Default.ReportProblem
                            else -> Icons.Default.Circle
                        },
                        contentDescription = tab
                    )
                },
                label = { Text(tab) }
            )
        }
    }
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(LocaleManager.getString(text))
    }
}

@Composable
fun RowScope.SummaryCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.weight(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = LocaleManager.getString(title),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RowScope.ActionButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.weight(1f).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(LocaleManager.getString(text), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun SiteCard(site: SiteItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = site.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = site.location,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = LocaleManager.getString(site.status),
                    fontSize = 12.sp,
                    color = when (site.status) {
                        "In Progress" -> MaterialTheme.colorScheme.primary
                        "Completed" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.secondary
                    },
                    modifier = Modifier
                        .background(
                            color = when (site.status) {
                                "In Progress" -> MaterialTheme.colorScheme.primaryContainer
                                "Completed" -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${LocaleManager.getString("Progress")}: ${(site.progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = LocaleManager.formatCurrency(site.budget),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { site.progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MaterialDetailCard(material: MaterialItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = LocaleManager.formatQuantity(material.quantity),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = LocaleManager.formatCurrency(material.cost),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocaleManager.getString(material.status),
                    fontSize = 12.sp,
                    color = when (material.status) {
                        "Delivered" -> MaterialTheme.colorScheme.primary
                        "Pending" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }
        }
    }
}

@Composable
fun PaymentDetailCard(payment: PaymentItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = payment.date,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = LocaleManager.formatCurrency(payment.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocaleManager.getString(payment.status),
                    fontSize = 12.sp,
                    color = when (payment.status) {
                        "Paid" -> MaterialTheme.colorScheme.primary
                        "Pending" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskItem, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${LocaleManager.getString("Due")}: ${task.dueDate}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${LocaleManager.getString("Assigned To")}: ${task.assignedTo}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${LocaleManager.getString("Priority")}: ${task.priority}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = LocaleManager.getString(task.status),
                fontSize = 12.sp,
                color = when (task.status) {
                    "Completed" -> MaterialTheme.colorScheme.primary
                    "Pending" -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.tertiary
                }
            )
        }
    }
}

@Composable
fun DocumentCard(document: DocumentItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = document.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = document.uploadDate,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun IssueCard(issue: IssueItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = issue.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${LocaleManager.getString("Status")}: ${issue.status}",
                fontSize = 14.sp,
                color = if (issue.status == "Open") Color.Red else Color.Green
            )
            Text(
                text = "${LocaleManager.getString("Priority")}: ${issue.priority}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${LocaleManager.getString("Due")}: ${issue.dueDate}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PhotoGridItem(photo: PhotoItem) {
    Card(
        modifier = Modifier.padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = photo.uri.toString()),
                contentDescription = photo.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Text(
                text = photo.caption,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MaterialRequestCard(
    request: MaterialRequest,
    onUpdate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${request.site} - ${request.material}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = LocaleManager.formatQuantity(request.quantity),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Supplier: ${request.supplier}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (request.status) {
                            "Delivered" -> Icons.Default.CheckCircle
                            "Pending" -> Icons.Default.Schedule
                            else -> Icons.Default.Warning
                        },
                        contentDescription = request.status,
                        tint = when (request.status) {
                            "Delivered" -> MaterialTheme.colorScheme.primary
                            "Pending" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocaleManager.getString(request.status),
                        fontSize = 14.sp,
                        color = when (request.status) {
                            "Delivered" -> MaterialTheme.colorScheme.primary
                            "Pending" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    )
                }

                Button(
                    onClick = onUpdate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Text(
                        text = "Update",
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.timestamp,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Handle permission denial (e.g., show a snackbar)
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Handle file/photo upload (mocked for now)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Mock adding to documents or photos
                MockData.documents.add(DocumentItem(UUID.randomUUID().toString(), "Uploaded Document", uri, "Aug 28, 2025"))
                MockData.photos.add(PhotoItem(UUID.randomUUID().toString(), uri, "Uploaded Photo", "Aug 28, 2025"))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHIVTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        context = this
                    )
                }
            }
        }
    }
}

@Composable
fun SHIVTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFFFF5722),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFFFF5722),
            background = Color(0xFFFFFBFE),
            surface = Color(0xFFFFFBFE),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.White,
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1C1B1F)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier, context: ComponentActivity) {
    NavHost(
        navController = navController,
        startDestination = if (AuthManager.currentUserRole == UserRole.NONE) "login" else getDashboardRoute(),
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { 
                    // Navigate directly to the appropriate dashboard based on user role
                    val route = getDashboardRoute()
                    navController.navigate(route) { popUpTo("login") { inclusive = true } }
                },
                onSignupClick = { navController.navigate("signup") },
                context = context
            )
        }
        composable("signup") {
            SignupScreen(
                onSignupSuccess = { 
                    // Navigate directly to the appropriate dashboard based on user role
                    val route = getDashboardRoute()
                    navController.navigate(route) { popUpTo("signup") { inclusive = true } }
                },
                onLoginClick = { navController.navigate("login") },
                context = context
            )
        }

        composable("contractor_type_select") {
            if (AuthManager.currentUserRole == UserRole.CONTRACTOR) {
                ContractorTypeSelectScreen(
                    onTypeSelected = { type -> 
                        AuthManager.currentContractorType = type
                        navController.navigate("dashboard/contractor/${type.name}") 
                    }
                )
            } else {
                navController.navigate("login")
            }
        }
        composable("dashboard/owner") {
            if (AuthManager.currentUserRole == UserRole.OWNER) {
                OwnerDashboard(
                    onBack = { 
                        AuthManager.logout()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    onSiteClick = { siteId -> navController.navigate("site_detail/$siteId") },
                    onSettings = { navController.navigate("settings") },
                    context = context
                )
            } else {
                navController.navigate("login")
            }
        }
        composable("dashboard/contractor/{type}") { backStackEntry ->
            if (AuthManager.currentUserRole == UserRole.CONTRACTOR) {
                val typeStr = backStackEntry.arguments?.getString("type") ?: "GENERAL"
                val type = try { ContractorType.valueOf(typeStr) } catch (e: Exception) { ContractorType.GENERAL }
                ContractorDashboard(
                    onBack = { 
                        AuthManager.logout()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    onSiteClick = { siteId -> navController.navigate("site_detail/$siteId") },
                    onSettings = { navController.navigate("settings") },
                    contractorType = type,
                    context = context
                )
            } else {
                navController.navigate("login")
            }
        }
        composable("dashboard/supplier") {
            if (AuthManager.currentUserRole == UserRole.SUPPLIER) {
                SupplierDashboard(
                    onBack = { 
                        AuthManager.logout()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    onSettings = { navController.navigate("settings") },
                    onCatalog = { navController.navigate("supplier_catalog") },
                    context = context
                )
            } else {
                navController.navigate("login")
            }
        }
        composable("site_detail/{siteId}") { backStackEntry ->
            val siteId = backStackEntry.arguments?.getString("siteId") ?: "1"
            val site = MockData.sites.find { it.id == siteId } ?: MockData.sites.first()
            SiteDetailScreen(
                site = site,
                onBack = { navController.popBackStack() },
                context = context
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = { 
                    AuthManager.logout()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable("supplier_catalog") {
            SupplierCatalogScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    context: ComponentActivity
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocaleManager.getString("Welcome to Construction Site Management"),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = LocaleManager.getString("Choose your role to manage construction sites efficiently."),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(LocaleManager.getString("Email")) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(LocaleManager.getString("Password")) },
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Create API client and repository
                val api = ApiClientFactory.create(tokenProvider = { GlobalTokenStore.getToken() })
                val repository = Repository(api, GlobalTokenStore)
                
                // Use coroutine to make API call
                context.lifecycleScope.launch {
                    try {
                        val result = repository.login(email, password)
                        if (result.isSuccess) {
                            val response = result.getOrNull()
                            if (response?.success == true) {
                                // Update AuthManager with real user data
                                AuthManager.currentUserRole = UserRole.OWNER
                                onLoginSuccess()
                            } else {
                                errorMessage = response?.message ?: "Login failed"
                            }
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Network error: ${e.message}"
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(LocaleManager.getString("Login"))
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onSignupClick) {
            Text(LocaleManager.getString("Don't have an account? Sign up"))
        }
    }
}

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    context: ComponentActivity
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.OWNER) }
    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    // Company fields for owner signup
    var companyName by remember { mutableStateOf("") }
    var companyDescription by remember { mutableStateOf("") }
    var companyAddress by remember { mutableStateOf("") }
    var companyPhone by remember { mutableStateOf("") }
    var companyEmail by remember { mutableStateOf("") }
    var companyWebsite by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocaleManager.getString("Create Account"),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(LocaleManager.getString("Name")) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(LocaleManager.getString("Email")) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(LocaleManager.getString("Password")) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = LocaleManager.getString(selectedRole.name),
                onValueChange = {},
                readOnly = true,
                label = { Text(LocaleManager.getString("Role")) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                UserRole.values().filter { it != UserRole.NONE }.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(LocaleManager.getString(role.name)) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // Company fields for owner signup
        if (selectedRole == UserRole.OWNER) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Company Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name *") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyDescription,
                onValueChange = { companyDescription = it },
                label = { Text("Company Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyAddress,
                onValueChange = { companyAddress = it },
                label = { Text("Company Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyPhone,
                onValueChange = { companyPhone = it },
                label = { Text("Company Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyEmail,
                onValueChange = { companyEmail = it },
                label = { Text("Company Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyWebsite,
                onValueChange = { companyWebsite = it },
                label = { Text("Company Website") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Validate required fields
                if (selectedRole == UserRole.OWNER && companyName.isBlank()) {
                    errorMessage = "Company name is required for owners"
                    return@Button
                }
                
                // Create API client and repository
                val api = ApiClientFactory.create(tokenProvider = { GlobalTokenStore.getToken() })
                val repository = Repository(api, GlobalTokenStore)
                
                // Use coroutine to make API call
                context.lifecycleScope.launch {
                    try {
                        val result = if (selectedRole == UserRole.OWNER) {
                            // Call company signup for owners
                            repository.signupOwner(
                                name, email, phone, password,
                                companyName, companyDescription, companyAddress,
                                companyPhone, companyEmail, companyWebsite
                            )
                        } else {
                            // Call regular signup for contractors/suppliers
                            repository.signup(name, email, password, selectedRole)
                        }
                        
                        if (result.isSuccess) {
                            val response = result.getOrNull()
                            if (response?.success == true) {
                                // Update AuthManager with real user data
                                AuthManager.currentUserRole = selectedRole
                                onSignupSuccess()
                            } else {
                                errorMessage = response?.message ?: "Signup failed"
                            }
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Signup failed"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Network error: ${e.message}"
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(LocaleManager.getString("Signup"))
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onLoginClick) {
            Text(LocaleManager.getString("Already have an account? Login"))
        }
    }
}



@Composable
fun ContractorTypeSelectScreen(onTypeSelected: (ContractorType) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocaleManager.getString("Select Contractor Type"),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        ContractorType.values().forEach { type ->
            Button(
                onClick = { onTypeSelected(type) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(type.name)
            }
        }
    }
}

@Composable
fun OwnerDashboard(
    onBack: () -> Unit,
    onSiteClick: (String) -> Unit,
    onSettings: () -> Unit,
    context: ComponentActivity
) {
    var selectedTab by remember { mutableStateOf("Overview") }
    var showCreateSiteDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Overview", "Sites", "Reports")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(LocaleManager.getString("Owner Dashboard")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )

            // Horizontal scrollable tabs
            HorizontalScrollableTabs(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when (selectedTab) {
                "Overview" -> OwnerOverviewTab(onSiteClick)
                "Sites" -> OwnerSitesTab(onSiteClick)
                "Reports" -> OwnerReportsTab()
            }
        }
        
        // Floating Action Button for creating sites
        FloatingActionButton(
            onClick = { showCreateSiteDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Create Site",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
    
    // Create Site Dialog
    if (showCreateSiteDialog) {
        CreateSiteDialog(
            onDismiss = { showCreateSiteDialog = false },
            onSiteCreated = { 
                showCreateSiteDialog = false
                // Refresh the sites list
            }
        )
    }
}

@Composable
fun OwnerOverviewTab(onSiteClick: (String) -> Unit) {
    var sites by remember { mutableStateOf<List<SiteItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Create API client and repository
    val api = remember { ApiClientFactory.create(tokenProvider = { GlobalTokenStore.getToken() }) }
    val repository = remember { Repository(api, GlobalTokenStore) }
    
    // Load sites when component loads
    LaunchedEffect(Unit) {
        try {
            val result = repository.getSites()
            if (result.isSuccess) {
                val apiSites = result.getOrNull() ?: emptyList()
                sites = apiSites.map { site ->
                    SiteItem(
                        id = site.id,
                        name = site.name,
                        location = site.location,
                        status = when (site.status) {
                            "NOT_STARTED" -> "Planning"
                            "IN_PROGRESS" -> "In Progress"
                            "COMPLETED" -> "Completed"
                            "ON_HOLD" -> "On Hold"
                            else -> site.status
                        },
                        progress = site.progress,
                        budget = site.budget,
                        startDate = site.startDate
                    )
                }
            }
        } catch (e: Exception) {
            // Handle error silently for overview
        } finally {
            isLoading = false
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Total Sites",
                    value = if (isLoading) "..." else sites.size.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryCard(
                    title = "Active",
                    value = if (isLoading) "..." else sites.count { it.status == "In Progress" }.toString(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Budget",
                    value = if (isLoading) "..." else LocaleManager.formatCurrency("₹${sites.sumOf { it.budget.toDoubleOrNull() ?: 0.0 }.toInt()}"),
                    color = MaterialTheme.colorScheme.tertiary
                )
                SummaryCard(
                    title = "Revenue",
                    value = if (isLoading) "..." else LocaleManager.formatCurrency("₹${(sites.sumOf { it.budget.toDoubleOrNull() ?: 0.0 } * 0.6).toInt()}"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Text(
                text = LocaleManager.getString("Recent Sites"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(sites.take(3)) { site ->
                SiteCard(site = site, onClick = { onSiteClick(site.id) })
            }
        }
    }
}

@Composable
fun OwnerSitesTab(onSiteClick: (String) -> Unit) {
    var showCreateSiteDialog by remember { mutableStateOf(false) }
    var sites by remember { mutableStateOf<List<SiteItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableStateOf(0) }
    
    // Create API client and repository
    val api = remember { ApiClientFactory.create(tokenProvider = { GlobalTokenStore.getToken() }) }
    val repository = remember { Repository(api, GlobalTokenStore) }
    
    // Load sites when component loads or refresh is triggered
    LaunchedEffect(refreshTrigger) {
        try {
            val result = repository.getSites()
            if (result.isSuccess) {
                val apiSites = result.getOrNull() ?: emptyList()
                sites = apiSites.map { site ->
                    SiteItem(
                        id = site.id,
                        name = site.name,
                        location = site.location,
                        status = when (site.status) {
                            "NOT_STARTED" -> "Planning"
                            "IN_PROGRESS" -> "In Progress"
                            "COMPLETED" -> "Completed"
                            "ON_HOLD" -> "On Hold"
                            else -> site.status
                        },
                        progress = site.progress,
                        budget = site.budget,
                        startDate = site.startDate
                    )
                }
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Failed to load sites"
            }
        } catch (e: Exception) {
            errorMessage = "Network error: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Create Site Button
        Button(
            onClick = { showCreateSiteDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Site")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Sites List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sites) { site ->
                    SiteCard(site = site, onClick = { onSiteClick(site.id) })
                }
            }
        }
    }
    
    // Create Site Dialog
    if (showCreateSiteDialog) {
        CreateSiteDialog(
            onDismiss = { showCreateSiteDialog = false },
            onSiteCreated = { 
                showCreateSiteDialog = false
                // Trigger refresh by incrementing the refresh trigger
                refreshTrigger++
            }
        )
    }
}

@Composable
fun OwnerReportsTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Financial Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Budget: ${LocaleManager.formatCurrency("₹8500000")}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Total Spent: ${LocaleManager.formatCurrency("₹5200000")}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Remaining: ${LocaleManager.formatCurrency("₹3300000")}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ContractorDashboard(
    onBack: () -> Unit,
    onSiteClick: (String) -> Unit,
    onSettings: () -> Unit,
    contractorType: ContractorType,
    context: ComponentActivity
) {
    var selectedTab by remember { mutableStateOf("Sites") }
    val tabs = listOf("Sites", "Tasks", "Materials")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(LocaleManager.getString("Contractor Dashboard").replace("{type}", contractorType.name)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )

        // Horizontal scrollable tabs
        HorizontalScrollableTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        when (selectedTab) {
            "Sites" -> ContractorSitesTab(onSiteClick, contractorType)
            "Tasks" -> ContractorTasksTab(contractorType)
            "Materials" -> ContractorMaterialsTab()
        }
    }
}

@Composable
fun ContractorSitesTab(onSiteClick: (String) -> Unit, contractorType: ContractorType) {
    val assignedSites = MockData.contractorSitesByType[contractorType] ?: emptyList()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(assignedSites) { site ->
            SiteCard(site = site, onClick = { onSiteClick(site.id) })
        }
    }
}

@Composable
fun ContractorTasksTab(contractorType: ContractorType) {
    val tasks = MockData.tasks.filter { it.contractorType == contractorType }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) { task ->
            TaskCard(task = task, onEdit = {})
        }
    }
}

@Composable
fun ContractorMaterialsTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(MockData.materials) { material ->
            MaterialDetailCard(material = material)
        }
    }
}

@Composable
fun SupplierDashboard(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onCatalog: () -> Unit,
    context: ComponentActivity
) {
    var selectedTab by remember { mutableStateOf("Requests") }
    val tabs = listOf("Requests", "Orders", "Payments")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(LocaleManager.getString("Supplier Dashboard")) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onCatalog) {
                    Icon(Icons.Default.Inventory, contentDescription = "Catalog")
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )

        // Horizontal scrollable tabs
        HorizontalScrollableTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        when (selectedTab) {
            "Requests" -> SupplierRequestsTab()
            "Orders" -> SupplierOrdersTab()
            "Payments" -> SupplierPaymentsTab()
        }
    }
}

@Composable
fun SupplierRequestsTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(MockData.materialRequests) { request ->
            MaterialRequestCard(
                request = request,
                onUpdate = {}
            )
        }
    }
}

@Composable
fun SupplierOrdersTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(MockData.materialRequests.filter { it.status == "Delivered" }) { request ->
            MaterialRequestCard(
                request = request,
                onUpdate = {}
            )
        }
    }
}

@Composable
fun SupplierPaymentsTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(MockData.payments) { payment ->
            PaymentDetailCard(payment = payment)
        }
    }
}

@Composable
fun SiteDetailScreen(
    site: SiteItem,
    onBack: () -> Unit,
    context: ComponentActivity
) {
    var selectedTab by remember { mutableStateOf("Overview") }
    val tabs = listOf("Overview", "Materials", "Payments", "Tasks", "Photos", "Documents", "Issues")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(site.name) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Horizontal scrollable tabs
        HorizontalScrollableTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        when (selectedTab) {
            "Overview" -> SiteOverviewTab(site)
            "Materials" -> MaterialsTab(site)
            "Payments" -> PaymentsTab(site)
            "Tasks" -> TasksTab(site)
            "Photos" -> PhotosTab(site)
            "Documents" -> DocumentsTab(site)
            "Issues" -> IssuesTab(site)
        }
    }
}

@Composable
fun SiteOverviewTab(site: SiteItem) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = LocaleManager.getString("Site Information"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${LocaleManager.getString("Location")}: ${site.location}")
                    Text("${LocaleManager.getString("Progress")}: ${(site.progress * 100).toInt()}%")
                    Text("${LocaleManager.getString("Budget")}: ${LocaleManager.formatCurrency(site.budget)}")
                    Text("Start Date: ${site.startDate}")
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Add Material",
                    icon = Icons.Default.Add,
                    color = MaterialTheme.colorScheme.primary
                ) {}
                ActionButton(
                    text = "Add Payment",
                    icon = Icons.Default.Payment,
                    color = MaterialTheme.colorScheme.secondary
                ) {}
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Take Photo",
                    icon = Icons.Default.CameraAlt,
                    color = MaterialTheme.colorScheme.tertiary
                ) {}
                ActionButton(
                    text = "Add Task",
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.primary
                ) {}
            }
        }
    }
}

@Composable
fun MaterialsTab(site: SiteItem) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(MockData.materials) { material ->
            MaterialDetailCard(material = material)
        }
    }
}

@Composable
fun PaymentsTab(site: SiteItem) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(MockData.payments) { payment ->
            PaymentDetailCard(payment = payment)
        }
    }
}

@Composable
fun TasksTab(site: SiteItem) {
    val siteTasks = MockData.tasks.filter { it.siteId == site.id }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(siteTasks) { task ->
            TaskCard(task = task, onEdit = {})
        }
    }
}

@Composable
fun PhotosTab(site: SiteItem) {
    if (MockData.photos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(LocaleManager.getString("No photos found"))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MockData.photos.size) { index ->
                PhotoGridItem(photo = MockData.photos[index])
            }
        }
    }
}

@Composable
fun DocumentsTab(site: SiteItem) {
    if (MockData.documents.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(LocaleManager.getString("No documents found"))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MockData.documents) { document ->
                DocumentCard(document = document, onClick = {})
            }
        }
    }
}

@Composable
fun IssuesTab(site: SiteItem) {
    val siteIssues = MockData.issues.filter { it.siteId == site.id }
    
    if (siteIssues.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(LocaleManager.getString("No issues found"))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(siteIssues) { issue ->
                IssueCard(issue = issue, onClick = {})
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(LocaleManager.currentLanguage) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(LocaleManager.getString("Settings")) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = LocaleManager.getString("Select Language"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(LocaleManager.getString("Language")) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("English", "Hindi", "Marathi").forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    selectedLanguage = language
                                    LocaleManager.currentLanguage = language
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun SupplierCatalogScreen(
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Supplier Catalog") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MockData.suppliers) { supplier ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = supplier.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Materials: ${supplier.materials.joinToString(", ")}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateSiteDialog(
    onDismiss: () -> Unit,
    onSiteCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var buildingType by remember { mutableStateOf("") }
    var floors by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var expectedEndDate by remember { mutableStateOf("") }
    var siteArea by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    // Create API client and repository
    val api = remember { ApiClientFactory.create(tokenProvider = { GlobalTokenStore.getToken() }) }
    val repository = remember { Repository(api, GlobalTokenStore) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Site") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Site Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location *") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget (₹) *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = buildingType,
                    onValueChange = { buildingType = it },
                    label = { Text("Building Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = floors,
                    onValueChange = { floors = it },
                    label = { Text("Number of Floors") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = expectedEndDate,
                    onValueChange = { expectedEndDate = it },
                    label = { Text("Expected End Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = siteArea,
                    onValueChange = { siteArea = it },
                    label = { Text("Site Area (sq ft)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || location.isBlank() || budget.isBlank()) {
                        errorMessage = "Please fill in all required fields"
                        return@Button
                    }
                    
                    isLoading = true
                    errorMessage = ""
                    
                    // Create site request
                    val createSiteRequest = CreateSiteRequest(
                        name = name,
                        location = location,
                        address = address.takeIf { it.isNotBlank() },
                        startDate = startDate.takeIf { it.isNotBlank() },
                        expectedEndDate = expectedEndDate.takeIf { it.isNotBlank() },
                        description = description.takeIf { it.isNotBlank() },
                        budget = budget.toDoubleOrNull(),
                        latitude = latitude.toDoubleOrNull(),
                        longitude = longitude.toDoubleOrNull(),
                        siteArea = siteArea.toDoubleOrNull(),
                        buildingType = buildingType.takeIf { it.isNotBlank() },
                        floors = floors.toIntOrNull()
                    )
                    
                    // Make API call
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val result = repository.createSite(createSiteRequest)
                            if (result.isSuccess) {
                                onSiteCreated()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to create site"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Network error"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Site")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // Date picker dialogs
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showStartDatePicker = false
                        // Format date as YYYY-MM-DD
                        val date = java.util.Date()
                        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        startDate = formatter.format(date)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState()
            )
        }
    }
    
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEndDatePicker = false
                        // Format date as YYYY-MM-DD
                        val date = java.util.Date()
                        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        expectedEndDate = formatter.format(date)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState()
            )
        }
    }
}

// API Data Classes
data class CreateSiteRequest(
    val name: String,
    val location: String,
    val address: String? = null,
    val startDate: String? = null,
    val expectedEndDate: String? = null,
    val description: String? = null,
    val budget: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val siteArea: Double? = null,
    val buildingType: String? = null,
    val floors: Int? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

data class CompanySignupRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val companyName: String,
    val companyDescription: String,
    val companyAddress: String,
    val companyPhone: String,
    val companyEmail: String,
    val companyWebsite: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val user: User? = null,
    val message: String? = null
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val phone: String? = null,
    val companyName: String? = null
)

data class Site(
    val id: String,
    val name: String,
    val location: String,
    val address: String? = null,
    val status: String,
    val progress: Float,
    val budget: String,
    val startDate: String,
    val description: String? = null,
    val ownerId: String
)

// Token Store Interface
interface TokenStore {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}

// Global Token Store
object GlobalTokenStore : TokenStore {
    private var token: String? = null
    
    override fun getToken(): String? = token
    override fun saveToken(token: String) { this.token = token }
    override fun clearToken() { this.token = null }
}

// API Client Factory
object ApiClientFactory {
    fun create(tokenProvider: () -> String?): ApiClient {
        return ApiClientImpl(tokenProvider)
    }
}

// API Client Interface
interface ApiClient {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun signup(request: SignupRequest): Result<LoginResponse>
    suspend fun signupOwner(request: CompanySignupRequest): Result<LoginResponse>
    suspend fun getSites(): Result<List<Site>>
    suspend fun createSite(request: CreateSiteRequest): Result<Site>
}

// API Client Implementation
class ApiClientImpl(private val tokenProvider: () -> String?) : ApiClient {
    private val baseUrl = "https://shiv-construction-backend.onrender.com/api" // Live Render backend
    private val client = OkHttpClient()
    
    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("email", request.email)
                    put("password", request.password)
                }

                val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                val httpRequest = Request.Builder()
                    .url("$baseUrl/auth/login")
                    .post(requestBody)
                    .build()

                val response = client.newCall(httpRequest).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBody)
                    val success = jsonResponse.getBoolean("success")
                    val token = jsonResponse.optString("token", null)
                    val userJson = jsonResponse.optJSONObject("user")
                    val user = if (userJson != null) {
                        User(
                            id = userJson.getString("_id"),
                            name = userJson.getString("name"),
                            email = userJson.getString("email"),
                            role = userJson.getString("role"),
                            phone = userJson.optString("phone", null),
                            companyName = userJson.optString("companyName", null)
                        )
                    } else null

                    Result.success(LoginResponse(success, token, user))
                } else {
                    val message = try {
                        JSONObject(responseBody).optString("message", "Login failed")
                    } catch (e: Exception) {
                        "Login failed"
                    }
                    Result.success(LoginResponse(false, message = message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun signup(request: SignupRequest): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("name", request.name)
                    put("email", request.email)
                    put("password", request.password)
                    put("role", request.role)
                }

                val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                val httpRequest = Request.Builder()
                    .url("$baseUrl/auth/signup")
                    .post(requestBody)
                    .build()

                val response = client.newCall(httpRequest).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBody)
                    val token = jsonResponse.optString("token", null)
                    val userJson = jsonResponse.optJSONObject("user")
                    val user = if (userJson != null) {
                        User(
                            id = userJson.getString("_id"),
                            name = userJson.getString("name"),
                            email = userJson.getString("email"),
                            role = userJson.getString("role"),
                            phone = userJson.optString("phone", null),
                            companyName = userJson.optString("companyName", null)
                        )
                    } else null

                    Result.success(LoginResponse(true, token, user))
                } else {
                    val message = try {
                        JSONObject(responseBody).optString("error", "Signup failed")
                    } catch (e: Exception) {
                        "Signup failed"
                    }
                    Result.success(LoginResponse(false, message = message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun signupOwner(request: CompanySignupRequest): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("name", request.name)
                    put("email", request.email)
                    put("phone", request.phone)
                    put("password", request.password)
                    put("companyName", request.companyName)
                    put("companyDescription", request.companyDescription)
                    put("companyAddress", request.companyAddress)
                    put("companyPhone", request.companyPhone)
                    put("companyEmail", request.companyEmail)
                    put("companyWebsite", request.companyWebsite)
                }

                val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                val httpRequest = Request.Builder()
                    .url("$baseUrl/auth/signup-owner")
                    .post(requestBody)
                    .build()

                val response = client.newCall(httpRequest).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBody)
                    val token = jsonResponse.optString("token", null)
                    val userJson = jsonResponse.optJSONObject("user")
                    val user = if (userJson != null) {
                        User(
                            id = userJson.getString("_id"),
                            name = userJson.getString("name"),
                            email = userJson.getString("email"),
                            role = userJson.getString("role"),
                            phone = userJson.optString("phone", null)
                        )
                    } else null
                    Result.success(LoginResponse(true, token, user))
                } else {
                    val message = try {
                        JSONObject(responseBody).optString("error", "Company signup failed")
                    } catch (e: Exception) {
                        "Company signup failed"
                    }
                    Result.success(LoginResponse(false, message = message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun getSites(): Result<List<Site>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenProvider()
                val httpRequest = Request.Builder()
                    .url("$baseUrl/sites")
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                
                val response = client.newCall(httpRequest).execute()
                val responseBody = response.body?.string() ?: ""
                
                if (response.isSuccessful) {
                    val sitesArray = JSONArray(responseBody)
                    val sites = mutableListOf<Site>()
                    
                    for (i in 0 until sitesArray.length()) {
                        val siteJson = sitesArray.getJSONObject(i)
                        sites.add(
                            Site(
                                id = siteJson.getString("_id"),
                                name = siteJson.getString("name"),
                                location = siteJson.optString("location", ""),
                                address = siteJson.optString("address", null),
                                status = siteJson.optString("status", "NOT_STARTED"),
                                progress = (siteJson.optDouble("progressPercent", 0.0) / 100.0).toFloat(),
                                budget = siteJson.optDouble("budget", 0.0).toString(),
                                startDate = siteJson.optString("startDate", ""),
                                description = siteJson.optString("description", null),
                                ownerId = siteJson.getString("owner")
                            )
                        )
                    }
                    
                    Result.success(sites)
                } else {
                    Result.failure(Exception("Failed to fetch sites"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun createSite(request: CreateSiteRequest): Result<Site> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenProvider()
                val json = JSONObject().apply {
                    put("name", request.name)
                    put("location", request.location)
                    request.address?.let { put("address", it) }
                    request.startDate?.let { put("startDate", it) }
                    request.expectedEndDate?.let { put("expectedEndDate", it) }
                    request.description?.let { put("description", it) }
                    request.budget?.let { put("budget", it) }
                    request.latitude?.let { put("latitude", it) }
                    request.longitude?.let { put("longitude", it) }
                    request.siteArea?.let { put("siteArea", it) }
                    request.buildingType?.let { put("buildingType", it) }
                    request.floors?.let { put("floors", it) }
                }
                
                val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                val httpRequest = Request.Builder()
                    .url("$baseUrl/sites")
                    .addHeader("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()
                
                val response = client.newCall(httpRequest).execute()
                val responseBody = response.body?.string() ?: ""
                
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBody)
                    val siteJson = jsonResponse.getJSONObject("site")
                    val site = Site(
                        id = siteJson.getString("_id"),
                        name = siteJson.getString("name"),
                        location = siteJson.optString("location", ""),
                        address = siteJson.optString("address", null),
                        status = siteJson.optString("status", "NOT_STARTED"),
                        progress = (siteJson.optDouble("progressPercent", 0.0) / 100.0).toFloat(),
                        budget = siteJson.optDouble("budget", 0.0).toString(),
                        startDate = siteJson.optString("startDate", ""),
                        description = siteJson.optString("description", null),
                        ownerId = siteJson.getString("owner")
                    )
                    Result.success(site)
                } else {
                    val message = try {
                        JSONObject(responseBody).optString("error", "Failed to create site")
                    } catch (e: Exception) {
                        "Failed to create site"
                    }
                    Result.failure(Exception(message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

// Repository
class Repository(private val apiClient: ApiClient, private val tokenStore: TokenStore) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        val result = apiClient.login(LoginRequest(email, password))
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response?.success == true && response.token != null) {
                tokenStore.saveToken(response.token)
            }
        }
        return result
    }

    suspend fun signup(name: String, email: String, password: String, role: UserRole): Result<LoginResponse> {
        val result = apiClient.signup(SignupRequest(name, email, password, role.name))
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response?.success == true && response.token != null) {
                tokenStore.saveToken(response.token)
            }
        }
        return result
    }

    suspend fun signupOwner(
        name: String, 
        email: String, 
        phone: String, 
        password: String,
        companyName: String,
        companyDescription: String,
        companyAddress: String,
        companyPhone: String,
        companyEmail: String,
        companyWebsite: String
    ): Result<LoginResponse> {
        val result = apiClient.signupOwner(CompanySignupRequest(
            name, email, phone, password,
            companyName, companyDescription, companyAddress,
            companyPhone, companyEmail, companyWebsite
        ))
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response?.success == true && response.token != null) {
                tokenStore.saveToken(response.token)
            }
        }
        return result
    }

    suspend fun getSites(): Result<List<Site>> {
        return apiClient.getSites()
    }

    suspend fun createSite(request: CreateSiteRequest): Result<Site> {
        return apiClient.createSite(request)
    }

    fun logout() {
        tokenStore.clearToken()
    }
}

