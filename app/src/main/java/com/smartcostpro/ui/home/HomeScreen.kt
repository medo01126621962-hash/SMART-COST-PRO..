package com.smartcostpro.ui.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcostpro.domain.model.AuthSession
import com.smartcostpro.domain.model.ExpenseType
import com.smartcostpro.ui.theme.CardBorderSubtle
import com.smartcostpro.ui.theme.DeepSlate
import com.smartcostpro.ui.theme.EmeraldGreen
import com.smartcostpro.ui.theme.PearlWhite
import com.smartcostpro.ui.theme.PureWhite
import com.smartcostpro.ui.theme.RoyalGold
import com.smartcostpro.ui.theme.RoyalGoldDark
import com.smartcostpro.ui.theme.RoyalGoldLight
import com.smartcostpro.ui.theme.SmartCostProTheme
import com.smartcostpro.ui.theme.TextDark
import com.smartcostpro.ui.theme.TextMuted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class DashboardCategory(
    val id: String,
    val title: String,
    val typeKey: String?,
    val icon: ImageVector,
    val accent: Color = RoyalGoldDark
)

private val dashboardCategories = listOf(
    DashboardCategory("house", "مصروفات المنزل", ExpenseType.HOUSE.key, Icons.Default.Home),
    DashboardCategory("medical", "المصروفات الطبية", ExpenseType.MEDICAL.key, Icons.Default.MedicalServices),
    DashboardCategory("work", "مصروفات العمل", ExpenseType.WORK.key, Icons.Default.Work),
    DashboardCategory("personal", "المصروفات الشخصية", ExpenseType.PERSONAL.key, Icons.Default.ShoppingBag),
    DashboardCategory("fuel", "وقود السيارة", ExpenseType.VEHICLE_FUEL.key, Icons.Default.LocalGasStation),
    DashboardCategory("maintenance", "صيانة السيارة", ExpenseType.VEHICLE_MAINT.key, Icons.Default.DirectionsCar),
    DashboardCategory("oil", "الزيوت والفلاتر", ExpenseType.VEHICLE_OIL.key, Icons.Default.Build),
    DashboardCategory("students", "التعليم والطلاب", ExpenseType.STUDENT.key, Icons.Default.School),
    DashboardCategory("accidents", "الحوادث والتأمين", null, Icons.Default.Shield),
    DashboardCategory("associations", "الجمعيات المالية", null, Icons.Default.Group),
    DashboardCategory("car_income", "دخل الرحلات", null, Icons.Default.Commute, EmeraldGreen),
    DashboardCategory("reports", "التقارير المفصلة", null, Icons.Default.Assessment)
)

/**
 * The Stitch dashboard layout implemented as a native Compose screen.
 * Financial figures and recent operations come from the existing Room expense table.
 */
@Composable
fun HomeScreen(
    session: AuthSession? = null,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val emptyPreviewState = remember {
            MutableStateFlow(HomeDashboardUiState(isLoading = false))
        }
        val stateFlow = viewModel?.uiState ?: emptyPreviewState
        val uiState by stateFlow.collectAsState()
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        var selectedTab by remember { mutableStateOf(0) }
        var showAddExpense by remember { mutableStateOf(false) }
        var showSearch by remember { mutableStateOf(false) }
        var showNotifications by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        var showAllExpenses by remember { mutableStateOf(false) }
        var showBudgetDialog by remember { mutableStateOf(false) }
        var showOverflowMenu by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf<DashboardCategory?>(null) }
        var expenseTitle by remember { mutableStateOf("") }
        var expenseAmount by remember { mutableStateOf("") }
        var expenseType by remember { mutableStateOf(ExpenseType.HOUSE) }
        var expenseTypeMenuExpanded by remember { mutableStateOf(false) }
        var expenseFormError by remember { mutableStateOf<String?>(null) }
        var budgetText by remember { mutableStateOf("") }
        var budgetError by remember { mutableStateOf<String?>(null) }
        var isSavingExpense by remember { mutableStateOf(false) }

        val monthText = remember(uiState.selectedMonth) {
            uiState.selectedMonth.atDay(1)
                .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar", "SA")))
        }
        val monthExpenses = uiState.monthExpenses
        val totalExpenses = HomeDashboardCalculations.totalForMonth(
            uiState.allExpenses,
            uiState.selectedMonth
        )
        val recentExpenses = monthExpenses.take(4)
        val searchResults = uiState.allExpenses
            .filter { item ->
                searchQuery.isNotBlank() && (
                    item.title.contains(searchQuery, ignoreCase = true) ||
                        item.category.contains(searchQuery, ignoreCase = true) ||
                        item.type.contains(searchQuery, ignoreCase = true) ||
                        item.amount.toString().contains(searchQuery.trim())
                    )
            }
            .take(12)

        val voiceSearchLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val spokenText = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                if (!spokenText.isNullOrBlank()) {
                    searchQuery = spokenText
                    showSearch = true
                }
            }
        }

        fun shareMonthlyReport() {
            val report = buildString {
                append("SMART COST PRO")
                append("\nكشف المصروفات لشهر ")
                append(monthText)
                append("\nإجمالي المصروفات: ")
                append(formatMoney(totalExpenses))
                append(" ر.س")
                append("\nعدد العمليات: ")
                append(monthExpenses.size)
                monthExpenses.forEach { item ->
                    append("\n- ")
                    append(item.date)
                    append(" | ")
                    append(item.title)
                    append(" | ")
                    append(formatMoney(item.amount))
                    append(" ر.س")
                }
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "كشف المصروفات - " + monthText)
                putExtra(Intent.EXTRA_TEXT, report)
            }
            try {
                context.startActivity(Intent.createChooser(intent, "مشاركة كشف المصروفات"))
            } catch (_: ActivityNotFoundException) {
                scope.launch { snackbarHostState.showSnackbar("لا يوجد تطبيق متاح لمشاركة الكشف") }
            }
        }

        fun selectBottomTab(index: Int) {
            selectedTab = index
            when (index) {
                0 -> Unit
                1 -> showAllExpenses = true
                2 -> shareMonthlyReport()
                3 -> showSettings = true
            }
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = PearlWhite,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column(modifier = Modifier.background(PearlWhite)) {
                    DashboardHeader(
                        userName = session?.user?.name?.takeIf { it.isNotBlank() }
                            ?: if (session?.isGuest == true) "زائر" else "حسابي",
                        unreadCount = 0,
                        onSearch = { showSearch = true },
                        onVoiceSearch = {
                            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "قل كلمة للبحث في المصروفات")
                            }
                            try {
                                voiceSearchLauncher.launch(speechIntent)
                            } catch (_: ActivityNotFoundException) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("البحث الصوتي غير متاح على هذا الجهاز")
                                }
                            }
                        },
                        onNotifications = { showNotifications = true },
                        onSettings = { showSettings = true },
                        overflowExpanded = showOverflowMenu,
                        onOverflowClick = { showOverflowMenu = true },
                        onOverflowDismiss = { showOverflowMenu = false },
                        onSignOut = {
                            showOverflowMenu = false
                            onSignOut()
                        }
                    )
                    DashboardTickerBar()
                }
            },
            bottomBar = {
                DashboardBottomBar(
                    selectedIndex = selectedTab,
                    onSelected = ::selectBottomTab
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        expenseFormError = null
                        showAddExpense = true
                    },
                    containerColor = RoyalGoldDark,
                    contentColor = PureWhite,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("إضافة مصروف", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("dashboard_add_expense_fab")
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                WelcomeAndMonthSelector(
                    monthText = monthText,
                    onPrevious = { viewModel?.moveMonth(-1) },
                    onNext = { viewModel?.moveMonth(1) }
                )

                FinancialSummaryCard(
                    totalExpenses = totalExpenses,
                    monthlyBudget = uiState.monthlyBudget,
                    onEditBudget = {
                        budgetText = uiState.monthlyBudget?.toString().orEmpty()
                        budgetError = null
                        showBudgetDialog = true
                    },
                    monthText = monthText
                )

                Button(
                    onClick = {
                        expenseFormError = null
                        showAddExpense = true
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp).testTag("dashboard_add_expense_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepSlate,
                        contentColor = RoyalGoldLight
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = RoyalGoldLight)
                    Spacer(Modifier.width(8.dp))
                    Text("إضافة عملية صرف جديدة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            budgetText = uiState.monthlyBudget?.toString().orEmpty()
                            budgetError = null
                            showBudgetDialog = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(11.dp),
                        contentPadding = PaddingValues(vertical = 11.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = RoyalGoldDark, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("تعديل الميزانية", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = ::shareMonthlyReport,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(11.dp),
                        contentPadding = PaddingValues(vertical = 11.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = RoyalGoldDark, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("مشاركة الكشف", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = CardBorderSubtle.copy(alpha = 0.45f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            "١٢ قسمًا",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("الأقسام المالية", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("افتح قسمًا لعرض بيانات الشهر المحدد", fontSize = 11.sp, color = TextMuted)
                    }
                }

                dashboardCategories.chunked(2).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowCategories.forEach { category ->
                            val amount = category.typeKey?.let {
                                HomeDashboardCalculations.totalForType(monthExpenses, it)
                            }
                            val percentage = if (amount != null) {
                                HomeDashboardCalculations.percentage(amount, totalExpenses)
                            } else {
                                0
                            }
                            DashboardCategoryCard(
                                category = category,
                                amount = amount,
                                percentage = percentage,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (category.id == "reports") {
                                        shareMonthlyReport()
                                    } else {
                                        selectedCategory = category
                                    }
                                }
                            )
                        }
                        if (rowCategories.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showAllExpenses = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("عرض الكل", color = RoyalGoldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = RoyalGoldDark, modifier = Modifier.size(16.dp))
                    }
                    Text("آخر المصروفات المسجلة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }

                if (uiState.isLoading) {
                    Card(colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("جارٍ تحميل المصروفات المحفوظة...", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                } else if (uiState.errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F0)),
                        border = BorderStroke(1.dp, Color(0xFFF4B8B3))
                    ) {
                        Text(
                            uiState.errorMessage,
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            color = Color(0xFF9F2922),
                            fontSize = 13.sp
                        )
                    }
                } else if (recentExpenses.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, CardBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = RoyalGoldDark, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("لا توجد مصروفات مسجلة في هذا الشهر", fontWeight = FontWeight.Bold, color = TextDark)
                            Text("أضف أول مصروف لتحديث ملخصك المالي", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                } else {
                    recentExpenses.forEach { expense ->
                        RecentExpenseCard(
                            title = expense.title,
                            categoryName = titleForType(expense.type),
                            date = expense.date,
                            amount = expense.amount,
                            onClick = {
                                selectedCategory = dashboardCategories.firstOrNull { it.typeKey == expense.type }
                            }
                        )
                    }
                }

                if (uiState.allExpenses.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "الأرقام المعروضة مأخوذة من السجلات المحفوظة على الجهاز",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }

        if (showAddExpense) {
            AlertDialog(
                onDismissRequest = {
                    if (!isSavingExpense) showAddExpense = false
                },
                title = {
                    Text("إضافة مصروف جديد", fontWeight = FontWeight.Bold, color = TextDark)
                },
                text = {
                    Column(
                        modifier = Modifier.heightIn(max = 500.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("سيُحفظ المصروف في قاعدة البيانات المحلية.", color = TextMuted, fontSize = 12.sp)
                        OutlinedTextField(
                            value = expenseTitle,
                            onValueChange = { expenseTitle = it; expenseFormError = null },
                            label = { Text("بيان المصروف") },
                            placeholder = { Text("مثال: مشتريات المنزل") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = expenseAmount,
                            onValueChange = { expenseAmount = it; expenseFormError = null },
                            label = { Text("المبلغ") },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box {
                            OutlinedButton(
                                onClick = { expenseTypeMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(titleForType(expenseType.key), modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ChevronLeft, contentDescription = "اختيار النوع")
                            }
                            DropdownMenu(
                                expanded = expenseTypeMenuExpanded,
                                onDismissRequest = { expenseTypeMenuExpanded = false }
                            ) {
                                ExpenseType.values().forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(titleForType(type.key)) },
                                        onClick = {
                                            expenseType = type
                                            expenseTypeMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Text("الشهر المختار: " + monthText, fontSize = 11.sp, color = TextMuted)
                        if (expenseFormError != null) {
                            Text(expenseFormError.orEmpty(), color = Color(0xFFB91C1C), fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = !isSavingExpense,
                        onClick = {
                            val amount = expenseAmount.trim().replace(',', '.').toDoubleOrNull()
                            if (expenseTitle.isBlank()) {
                                expenseFormError = "اكتب بيان المصروف"
                            } else if (amount == null || !amount.isFinite() || amount <= 0.0) {
                                expenseFormError = "أدخل مبلغًا صحيحًا أكبر من صفر"
                            } else if (viewModel == null) {
                                expenseFormError = "الحفظ متاح عند تشغيل التطبيق مع قاعدة البيانات"
                            } else {
                                isSavingExpense = true
                                scope.launch {
                                    val result = viewModel.addExpense(
                                        title = expenseTitle,
                                        amount = amount,
                                        type = expenseType
                                    )
                                    isSavingExpense = false
                                    result.fold(
                                        onSuccess = {
                                            showAddExpense = false
                                            expenseTitle = ""
                                            expenseAmount = ""
                                            expenseType = ExpenseType.HOUSE
                                            expenseFormError = null
                                            snackbarHostState.showSnackbar("تم حفظ المصروف محليًا بنجاح")
                                        },
                                        onFailure = { error ->
                                            expenseFormError = error.message ?: "تعذّر حفظ المصروف"
                                        }
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepSlate,
                            contentColor = RoyalGoldLight
                        )
                    ) {
                        Text(if (isSavingExpense) "جارٍ الحفظ..." else "حفظ المصروف")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { if (!isSavingExpense) showAddExpense = false }) {
                        Text("إلغاء")
                    }
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(18.dp)
            )
        }

        if (showSearch) {
            AlertDialog(
                onDismissRequest = { showSearch = false },
                title = { Text("البحث في المصروفات", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("وصف أو نوع أو مبلغ") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "مسح البحث")
                                    }
                                }
                            }
                        )
                        if (searchQuery.isNotBlank()) {
                            if (searchResults.isEmpty()) {
                                Text("لا توجد نتائج مطابقة.", color = TextMuted, fontSize = 12.sp)
                            } else {
                                searchResults.forEach { item ->
                                    RecentExpenseCard(
                                        title = item.title,
                                        categoryName = titleForType(item.type),
                                        date = item.date,
                                        amount = item.amount,
                                        onClick = {
                                            showSearch = false
                                            selectedCategory = dashboardCategories.firstOrNull { it.typeKey == item.type }
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSearch = false }) { Text("إغلاق") }
                }
            )
        }

        if (showNotifications) {
            AlertDialog(
                onDismissRequest = { showNotifications = false },
                title = { Text("التنبيهات", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "لا توجد خدمة تنبيهات مربوطة ببيانات التطبيق حاليًا. لن نعرض تنبيهات أو عمليات مزامنة وهمية.",
                        color = TextMuted
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showNotifications = false }) { Text("حسنًا") }
                }
            )
        }

        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("إعدادات الحساب", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("الحساب: " + (session?.user?.name?.takeIf { it.isNotBlank() } ?: "حساب محلي"), color = TextDark)
                        Text("المصروفات في هذه الشاشة تُقرأ من Room على هذا الجهاز.", color = TextMuted, fontSize = 12.sp)
                        Text("الميزانية الشهرية تحفظ محليًا كتفضيل للشاشة.", color = TextMuted, fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSettings = false
                            onSignOut()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepSlate, contentColor = RoyalGoldLight)
                    ) { Text("تسجيل الخروج") }
                },
                dismissButton = {
                    TextButton(onClick = { showSettings = false }) { Text("إغلاق") }
                }
            )
        }

        if (showBudgetDialog) {
            AlertDialog(
                onDismissRequest = { showBudgetDialog = false },
                title = { Text("ميزانية شهر " + monthText, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("حدد سقف الصرف لهذا الشهر. لن نضع قيمة افتراضية بدلًا منك.", color = TextMuted, fontSize = 12.sp)
                        OutlinedTextField(
                            value = budgetText,
                            onValueChange = { budgetText = it; budgetError = null },
                            label = { Text("الميزانية الشهرية") },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (budgetError != null) Text(budgetError.orEmpty(), color = Color(0xFFB91C1C), fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = budgetText.trim().replace(',', '.').toDoubleOrNull()
                            if (amount == null || !amount.isFinite() || amount <= 0.0) {
                                budgetError = "أدخل ميزانية صحيحة أكبر من صفر"
                            } else {
                                viewModel?.saveMonthlyBudget(amount)
                                if (viewModel == null) {
                                    emptyPreviewState.value = emptyPreviewState.value.copy(monthlyBudget = amount)
                                }
                                showBudgetDialog = false
                                scope.launch { snackbarHostState.showSnackbar("تم حفظ سقف الميزانية لهذا الشهر") }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepSlate, contentColor = RoyalGoldLight)
                    ) { Text("حفظ الميزانية") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel?.saveMonthlyBudget(null)
                            if (viewModel == null) {
                                emptyPreviewState.value = emptyPreviewState.value.copy(monthlyBudget = null)
                            }
                            showBudgetDialog = false
                        }
                    ) { Text("مسح الميزانية") }
                }
            )
        }

        if (showAllExpenses) {
            AlertDialog(
                onDismissRequest = { showAllExpenses = false },
                title = { Text("مصروفات " + monthText, fontWeight = FontWeight.Bold) },
                text = {
                    if (monthExpenses.isEmpty()) {
                        Text("لا توجد مصروفات لهذا الشهر.", color = TextMuted)
                    } else {
                        Column(
                            modifier = Modifier.heightIn(max = 470.dp).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            monthExpenses.forEach { item ->
                                RecentExpenseCard(
                                    title = item.title,
                                    categoryName = titleForType(item.type),
                                    date = item.date,
                                    amount = item.amount,
                                    onClick = {
                                        showAllExpenses = false
                                        selectedCategory = dashboardCategories.firstOrNull { it.typeKey == item.type }
                                    }
                                )
                            }
                            Surface(
                                color = RoyalGoldLight.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    "الإجمالي: " + formatMoney(totalExpenses) + " ر.س",
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showAllExpenses = false }) { Text("إغلاق") } },
                containerColor = PureWhite
            )
        }

        selectedCategory?.let { category ->
            val categoryRows = category.typeKey?.let { key ->
                monthExpenses.filter { it.type.equals(key, ignoreCase = true) }
            }.orEmpty()
            val categoryTotal = categoryRows.sumOf { it.amount }
            AlertDialog(
                onDismissRequest = { selectedCategory = null },
                title = { Text(category.title, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.heightIn(max = 430.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (category.typeKey == null) {
                            Text(
                                when (category.id) {
                                    "accidents" -> "قسم الحوادث ظاهر في التصميم، لكنه لا يملك جدول بيانات مستقلًا في قاعدة Room الحالية."
                                    "associations" -> "الجمعيات تحتاج نموذج بيانات مستقلًا. لن نسجلها كمصروف عادي حتى لا تختلط الحسابات."
                                    "car_income" -> "دخل الرحلات دخلٌ وليس مصروفًا، ولا يوجد له جدول دخل في قاعدة Room الحالية."
                                    else -> "هذا القسم يحتاج ربطًا بمصدر البيانات قبل عرض أرقام فعلية."
                                },
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        } else {
                            Text("إجمالي هذا القسم في الشهر المحدد: " + formatMoney(categoryTotal) + " ر.س", fontWeight = FontWeight.Bold, color = TextDark)
                            if (categoryRows.isEmpty()) {
                                Text("لا توجد عمليات مسجلة في هذا القسم لهذا الشهر.", color = TextMuted, fontSize = 12.sp)
                            } else {
                                categoryRows.forEach { item ->
                                    RecentExpenseCard(
                                        title = item.title,
                                        categoryName = titleForType(item.type),
                                        date = item.date,
                                        amount = item.amount,
                                        onClick = {}
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    if (category.typeKey != null) {
                        Button(
                            onClick = {
                                expenseType = ExpenseType.fromKey(category.typeKey) ?: ExpenseType.HOUSE
                                expenseFormError = null
                                selectedCategory = null
                                showAddExpense = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepSlate, contentColor = RoyalGoldLight)
                        ) { Text("إضافة في هذا القسم") }
                    } else {
                        TextButton(onClick = { selectedCategory = null }) { Text("حسنًا") }
                    }
                },
                dismissButton = {
                    if (category.typeKey != null) {
                        TextButton(onClick = { selectedCategory = null }) { Text("إغلاق") }
                    }
                },
                containerColor = PureWhite
            )
        }
    }
}

@Composable
private fun DashboardHeader(
    userName: String,
    unreadCount: Int,
    onSearch: () -> Unit,
    onVoiceSearch: () -> Unit,
    onNotifications: () -> Unit,
    onSettings: () -> Unit,
    overflowExpanded: Boolean,
    onOverflowClick: () -> Unit,
    onOverflowDismiss: () -> Unit,
    onSignOut: () -> Unit
) {
    Surface(
        color = PearlWhite,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Default.Search, contentDescription = "بحث", tint = TextDark)
                }
                IconButton(onClick = onVoiceSearch) {
                    Icon(Icons.Default.Mic, contentDescription = "بحث صوتي", tint = TextDark)
                }
                Box {
                    IconButton(onClick = onNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = "التنبيهات", tint = TextDark)
                    }
                    if (unreadCount > 0) {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd).padding(top = 3.dp, end = 3.dp),
                            containerColor = Color(0xFFDC2626)
                        ) { Text(unreadCount.toString(), color = PureWhite, fontSize = 9.sp) }
                    }
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "الإعدادات", tint = TextDark)
                }
                Box {
                    IconButton(onClick = onOverflowClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "المزيد", tint = TextDark)
                    }
                    DropdownMenu(expanded = overflowExpanded, onDismissRequest = onOverflowDismiss) {
                        DropdownMenuItem(
                            text = { Text("تسجيل الخروج") },
                            leadingIcon = { Icon(Icons.Default.Close, contentDescription = null) },
                            onClick = onSignOut
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(userName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Surface(
                            color = RoyalGoldLight,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.3f))
                        ) {
                            Text("PRO ✦", modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), color = RoyalGoldDark, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Text("SMART COST PRO", fontSize = 10.sp, color = TextMuted)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                        .border(2.dp, RoyalGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "الملف الشخصي", tint = RoyalGoldDark, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DashboardTickerBar() {
    Surface(
        color = Color(0xFFF0F3FF),
        modifier = Modifier.fillMaxWidth().height(36.dp),
        border = BorderStroke(1.dp, CardBorderSubtle)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = RoyalGoldLight, shape = RoundedCornerShape(5.dp)) {
                Text("محلي", modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), color = RoyalGoldDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "الملخص يعتمد على المصروفات المحفوظة على الجهاز  •  أضف عملية لتحديث إجمالي الشهر  •  لا تُعرض بيانات تجريبية  •  ",
                modifier = Modifier.weight(1f).basicMarquee(iterations = Int.MAX_VALUE),
                maxLines = 1,
                color = TextDark,
                fontSize = 11.sp
            )
            Spacer(Modifier.width(6.dp))
            Text("SC", color = RoyalGoldDark, fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
    }
}

@Composable
private fun WelcomeAndMonthSelector(
    monthText: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(PureWhite)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.ChevronRight, contentDescription = "الشهر السابق", modifier = Modifier.size(18.dp), tint = TextDark)
            }
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = RoyalGoldDark)
            Text(monthText, modifier = Modifier.padding(horizontal = 5.dp), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextDark)
            IconButton(onClick = onNext, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "الشهر التالي", modifier = Modifier.size(18.dp), tint = TextDark)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("مرحبًا بك 👋", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Text("نظرة عامة على وضعك المالي", fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
private fun FinancialSummaryCard(
    totalExpenses: Double,
    monthlyBudget: Double?,
    onEditBudget: () -> Unit,
    monthText: String
) {
    val ratio = if (monthlyBudget != null && monthlyBudget > 0) {
        (totalExpenses / monthlyBudget * 100.0).coerceAtLeast(0.0)
    } else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color(0xFFFFFFFF), Color(0xFFFFFCF1))
                    )
                )
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = Color(0xFFF0F3FF), shape = RoundedCornerShape(12.dp)) {
                    Text("ملخص فعلي", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = RoyalGoldDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("إجمالي المصروفات الشهرية", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = RoyalGoldDark, modifier = Modifier.size(17.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(formatMoney(totalExpenses), fontSize = 31.sp, fontWeight = FontWeight.Black, color = RoyalGoldDark)
                Spacer(Modifier.width(5.dp))
                Text("ر.س", modifier = Modifier.padding(bottom = 4.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            if (ratio != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatPercent(ratio), fontSize = 11.sp, color = RoyalGoldDark, fontWeight = FontWeight.Bold)
                    Text("المصروف من الميزانية المحددة لشهر " + monthText, fontSize = 10.sp, color = TextMuted)
                }
                LinearProgressIndicator(
                    progress = { (ratio / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(5.dp)),
                    color = RoyalGoldDark,
                    trackColor = Color(0xFFE7EAF1)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onEditBudget),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("تحديد الميزانية", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalGoldDark)
                    Text("لم تُحدد ميزانية لهذا الشهر", fontSize = 10.sp, color = TextMuted)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                SummaryMiniCard(
                    title = "الدخل الشهري",
                    value = "غير مسجل",
                    caption = "لا يوجد مصدر دخل مرتبط حاليًا",
                    icon = Icons.Default.Savings,
                    modifier = Modifier.weight(1f),
                    color = EmeraldGreen
                )
                SummaryMiniCard(
                    title = "صافي الشهر",
                    value = "غير متاح",
                    caption = "يتطلب تسجيل الدخل",
                    icon = Icons.Default.Analytics,
                    modifier = Modifier.weight(1f),
                    color = RoyalGoldDark
                )
            }
        }
    }
}

@Composable
private fun SummaryMiniCard(
    title: String,
    value: String,
    caption: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color
) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF3F5FB),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = TextMuted, fontSize = 10.sp)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            }
            Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(caption, color = TextMuted, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DashboardCategoryCard(
    category: DashboardCategory,
    amount: Double?,
    percentage: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .heightIn(min = 108.dp)
            .clickable(onClick = onClick)
            .testTag("dashboard_category_" + category.id),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, CardBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(11.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (amount != null && percentage > 0) {
                    Text(formatPercent(percentage.toDouble()), fontSize = 10.sp, color = TextMuted)
                } else {
                    Spacer(Modifier.width(1.dp))
                }
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0xFFF0F3FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(category.icon, contentDescription = null, tint = category.accent, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(category.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            when {
                amount != null -> Text(formatMoney(amount) + " ر.س", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = RoyalGoldDark, maxLines = 1)
                category.id == "reports" -> Text("مشاركة كشف الشهر", fontSize = 10.sp, color = TextMuted)
                else -> Text("قسم مستقل", fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun RecentExpenseCard(
    title: String,
    categoryName: String,
    date: String,
    amount: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, CardBorderSubtle)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("- " + formatMoney(amount) + " ر.س", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(formatExpenseDate(date), color = TextMuted, fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f, fill = false)) {
                    Text(title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(categoryName, color = TextMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)).background(Color(0xFFF0F3FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = RoyalGoldDark, modifier = Modifier.size(19.dp))
                }
            }
        }
    }
}

@Composable
private fun DashboardBottomBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val labels = listOf("الرئيسية", "المصروفات", "التقارير", "الإعدادات")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.ReceiptLong,
        Icons.Default.Assessment,
        Icons.Default.Settings
    )
    NavigationBar(containerColor = PureWhite, tonalElevation = 8.dp) {
        labels.forEachIndexed { index, label ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onSelected(index) },
                icon = { Icon(icons[index], contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RoyalGoldDark,
                    selectedTextColor = RoyalGoldDark,
                    indicatorColor = RoyalGoldLight,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}

private fun titleForType(typeKey: String): String =
    dashboardCategories.firstOrNull { it.typeKey == typeKey }?.title ?: typeKey

private fun formatMoney(amount: Double): String =
    NumberFormat.getNumberInstance(Locale("ar", "EG")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }.format(amount)

private fun formatPercent(value: Double): String =
    NumberFormat.getNumberInstance(Locale("ar", "EG")).apply {
        maximumFractionDigits = 1
        minimumFractionDigits = 0
    }.format(value) + "٪"

private fun formatExpenseDate(value: String): String = try {
    LocalDate.parse(value).format(DateTimeFormatter.ofPattern("d MMM", Locale("ar", "EG")))
} catch (_: Exception) {
    value
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun HomeScreenPreview() {
    SmartCostProTheme {
        HomeScreen()
    }
}
