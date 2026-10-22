package com.smartcostpro.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcostpro.R
import com.smartcostpro.domain.model.AuthSession
import com.smartcostpro.ui.theme.CardBorderSubtle
import com.smartcostpro.ui.theme.CoralRed
import com.smartcostpro.ui.theme.CoralRedBg
import com.smartcostpro.ui.theme.DeepSlate
import com.smartcostpro.ui.theme.EmeraldGreen
import com.smartcostpro.ui.theme.EmeraldGreenBg
import com.smartcostpro.ui.theme.GoldAccent
import com.smartcostpro.ui.theme.GoldBorder
import com.smartcostpro.ui.theme.GoldBorderLight
import com.smartcostpro.ui.theme.GoldPrimary
import com.smartcostpro.ui.theme.GoldSurface
import com.smartcostpro.ui.theme.PearlWhite
import com.smartcostpro.ui.theme.PureWhite
import com.smartcostpro.ui.theme.RoyalGold
import com.smartcostpro.ui.theme.RoyalGoldDark
import com.smartcostpro.ui.theme.RoyalGoldLight
import com.smartcostpro.ui.theme.SmartCostProTheme
import com.smartcostpro.ui.theme.SurfaceSlate
import com.smartcostpro.ui.theme.TextDark
import com.smartcostpro.ui.theme.TextMuted
import com.smartcostpro.ui.theme.TextPlaceholder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gateway Model for 8 Executive Action Gateways
 */
data class GatewayItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String? = null,
    val accentColor: Color = RoyalGold
)

/**
 * Production-ready HomeScreen Composable adhering to Executive Luxury Theme & RTL Arabic guidelines.
 */
@Composable
fun HomeScreen(
    session: AuthSession? = null,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Force Arabic RTL layout for complete native compliance
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        var selectedBottomNav by remember { mutableIntStateOf(0) }

        // Live Clock State (updates every minute)
        var currentTimeText by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            val formatter = SimpleDateFormat("hh:mm a • d MMMM", Locale.forLanguageTag("ar"))
            while (true) {
                currentTimeText = formatter.format(Date())
                delay(30000L) // update every 30s
            }
        }

        // Quick Expense Dialog State
        var showQuickExpenseDialog by remember { mutableStateOf(false) }
        var quickExpenseTitle by remember { mutableStateOf("") }
        var quickExpenseAmount by remember { mutableStateOf("") }

        // Search Bar Dialog State
        var showSearchDialog by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }

        // Overflow Menu State
        var showOverflowMenu by remember { mutableStateOf(false) }

        // 8 Executive Gateways definition
        val gateways = remember {
            listOf(
                GatewayItem(
                    id = "income",
                    title = "الدخل والحسابات",
                    subtitle = "إجمالي الودائع والتدفقات",
                    icon = Icons.Default.AccountBalance,
                    badge = "نشط",
                    accentColor = EmeraldGreen
                ),
                GatewayItem(
                    id = "daily_expenses",
                    title = "المصروفات اليومية",
                    subtitle = "تسجيل الفواتير اللحظية",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    badge = "يومي",
                    accentColor = RoyalGold
                ),
                GatewayItem(
                    id = "budgets",
                    title = "الموازنة التقديرية",
                    subtitle = "متابعة الانحرافات والأسقف",
                    icon = Icons.Default.PieChart,
                    badge = "65%",
                    accentColor = CoralRed
                ),
                GatewayItem(
                    id = "project_costing",
                    title = "تكاليف المشاريع",
                    subtitle = "كلفة العقود والمشتريات",
                    icon = Icons.Default.Business,
                    badge = "4 مشاريع",
                    accentColor = RoyalGold
                ),
                GatewayItem(
                    id = "reports",
                    title = "التقارير المالية والـ PDF",
                    subtitle = "تصدير القوائم والتحليلات",
                    icon = Icons.Default.Assessment,
                    badge = "PDF",
                    accentColor = DeepSlate
                ),
                GatewayItem(
                    id = "vendors",
                    title = "العملاء والموردين",
                    subtitle = "الذمم المدينة والدائنة",
                    icon = Icons.Default.People,
                    accentColor = EmeraldGreen
                ),
                GatewayItem(
                    id = "ai_advisor",
                    title = "المستشار المالي الذكي",
                    subtitle = "توصيات خفض الهدر المالي",
                    icon = Icons.Default.AutoAwesome,
                    badge = "ذكاء اصطناعي",
                    accentColor = RoyalGoldDark
                ),
                GatewayItem(
                    id = "settings",
                    title = "إعدادات الحساب والنظام",
                    subtitle = "المزامنة والصلاحيات",
                    icon = Icons.Default.Settings,
                    accentColor = DeepSlate
                )
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = PearlWhite,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showQuickExpenseDialog = true },
                    containerColor = RoyalGold,
                    contentColor = DeepSlate,
                    shape = RoundedCornerShape(18.dp),
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(6.dp),
                    modifier = Modifier.testTag("fab_quick_expense")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة تكلفة",
                            tint = DeepSlate,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تسجيل تكلفة سريعة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepSlate
                        )
                    }
                }
            },
            bottomBar = {
                ExecutiveBottomBar(
                    selectedIndex = selectedBottomNav,
                    onSelect = { index ->
                        selectedBottomNav = index
                        val label = when (index) {
                            0 -> "الرئيسية"
                            1 -> "العمليات"
                            2 -> "التحليلات"
                            else -> "المزيد"
                        }
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("الانتقال إلى: $label")
                        }
                    }
                )
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 88.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_screen_grid")
            ) {
                // Section 1: Top App Bar (Executive Header)
                item(span = { GridItemSpan(2) }) {
                    ExecutiveHeader(
                        userName = session?.user?.name ?: "م. سيف الدين العمري",
                        userRole = if (session?.isGuest == true) "جلسة زائر • الوضع المحلي" else "المدير التنفيذي • الحساب الماسي",
                        currentTime = currentTimeText,
                        onSearchClick = { showSearchDialog = true },
                        onVoiceSearchClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("جاري الاستماع للبحث الصوتي الذكي...")
                            }
                        },
                        onOverflowClick = { showOverflowMenu = true },
                        showOverflowMenu = showOverflowMenu,
                        onDismissOverflow = { showOverflowMenu = false },
                        onSignOut = onSignOut
                    )
                }

                // Section 2: Live Financial News & Ticker Bar
                item(span = { GridItemSpan(2) }) {
                    LiveFinancialNewsTicker(
                        onNewsClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("عرض تفاصيل المؤشرات والتنبيهات المالية")
                            }
                        }
                    )
                }

                // Section 3: Financial Summary / KPI Cards
                item(span = { GridItemSpan(2) }) {
                    FinancialSummaryCards()
                }

                // Section Title: 8-Gateway Action Grid
                item(span = { GridItemSpan(2) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(RoyalGold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بوابات الوصول السريع التنفيذية",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                // 8 Gateway Cards (2 per row)
                items(gateways) { gateway ->
                    GatewayCard(
                        gateway = gateway,
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("فتح بوابة: ${gateway.title}")
                            }
                        }
                    )
                }
            }
        }

        // Quick Expense Dialog
        if (showQuickExpenseDialog) {
            AlertDialog(
                onDismissRequest = { showQuickExpenseDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تسجيل تكلفة سريعة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "أدخل بيانات التكلفة لحفظها مباشرة في السجل المالي:",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        OutlinedTextField(
                            value = quickExpenseTitle,
                            onValueChange = { quickExpenseTitle = it },
                            label = { Text("بيان التكلفة / البند") },
                            placeholder = { Text("مثال: صيانة معدات، وقود، عمالة") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = quickExpenseAmount,
                            onValueChange = { quickExpenseAmount = it },
                            label = { Text("المبلغ (ر.س)") },
                            placeholder = { Text("0.00") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (quickExpenseTitle.isNotBlank() && quickExpenseAmount.isNotBlank()) {
                                showQuickExpenseDialog = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("تم تسجيل: $quickExpenseTitle بمبلغ $quickExpenseAmount ر.س بنجاح")
                                    quickExpenseTitle = ""
                                    quickExpenseAmount = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepSlate,
                            contentColor = RoyalGold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("حفظ التكلفة", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showQuickExpenseDialog = false }) {
                        Text("إلغاء", color = TextMuted)
                    }
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(18.dp)
            )
        }

        // Search Bar Dialog
        if (showSearchDialog) {
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                title = {
                    Text("البحث المالي الشامل", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث عن فاتورة، مشروع، عميل، أو عقد...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = RoyalGold)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "مسح")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSearchDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("نتائج البحث عن: $searchQuery")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepSlate,
                            contentColor = RoyalGold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("بحث", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSearchDialog = false }) {
                        Text("إغلاق", color = TextMuted)
                    }
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}

/**
 * 1. Top App Bar (Executive Header)
 */
@Composable
private fun ExecutiveHeader(
    userName: String,
    userRole: String,
    currentTime: String,
    onSearchClick: () -> Unit,
    onVoiceSearchClick: () -> Unit,
    onOverflowClick: () -> Unit,
    showOverflowMenu: Boolean,
    onDismissOverflow: () -> Unit,
    onSignOut: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CardBorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("executive_header_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // User Profile Section (Right in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Squircle Avatar with Active Badge
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = EmeraldGreen,
                                modifier = Modifier
                                    .size(10.dp)
                                    .border(1.5.dp, PureWhite, CircleShape)
                            )
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(DeepSlate, SurfaceSlate)
                                    )
                                )
                                .border(1.5.dp, RoyalGold, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(2).uppercase(),
                                color = RoyalGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = userName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = userRole,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = RoyalGoldDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Actions & Utilities (Left in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Search Button
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(PearlWhite, CircleShape)
                            .border(1.dp, CardBorderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = TextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Voice Search Button
                    IconButton(
                        onClick = onVoiceSearchClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(PearlWhite, CircleShape)
                            .border(1.dp, CardBorderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "بحث صوتي",
                            tint = RoyalGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Overflow Menu
                    Box {
                        IconButton(
                            onClick = onOverflowClick,
                            modifier = Modifier
                                .size(38.dp)
                                .background(PearlWhite, CircleShape)
                                .border(1.dp, CardBorderSubtle, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "خيارات إضافية",
                                tint = TextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = onDismissOverflow,
                            modifier = Modifier.background(PureWhite)
                        ) {
                            DropdownMenuItem(
                                text = { Text("تحديث البيانات") },
                                onClick = onDismissOverflow,
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = RoyalGold)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("المزامنة السحابية") },
                                onClick = onDismissOverflow,
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = EmeraldGreen)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تسجيل الخروج", color = CoralRed) },
                                onClick = {
                                    onDismissOverflow()
                                    onSignOut()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = CoralRed)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Digital Live Clock & Date Badge
            Surface(
                color = GoldSurface,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = RoyalGoldDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentTime.isNotBlank()) currentTime else "مباشر • النظام المالي متصل ومحدث",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepSlate
                    )
                }
            }
        }
    }
}

/**
 * 2. Live Financial News & Ticker Bar (شريط التنبيهات والأخبار المالي المتحرك)
 */
@Composable
private fun LiveFinancialNewsTicker(
    onNewsClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GoldSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GoldBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNewsClick() }
            .testTag("financial_news_ticker")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Prominent Brand Emblem / Logo
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .border(1.dp, RoyalGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_smart_cost_logo),
                    contentDescription = "Smart Cost Pro Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Scrolling Marquee News Ticker
            Text(
                text = "💡 تنبيه الميزانية: تم استهلاك 65% من مخصصات التشغيل لشهر مارس  •  📈 مؤشر التوفير: ارتفاع معدل خفض التكاليف بنسبة +8.4%  •  🔔 فاتورة جديدة مستحقة خلال 48 ساعة  •  💵 استقرار أسعار الصرف ومعدل السيولة النقدية بنسبة 98.2%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DeepSlate,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee(iterations = Int.MAX_VALUE)
            )
        }
    }
}

/**
 * 3. Financial Summary / Executive KPI Cards
 */
@Composable
private fun FinancialSummaryCards() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Main Wallet Balance Card (Luxury Deep Obsidian / Slate with Gold Accents)
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepSlate),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("main_wallet_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                DeepSlate,
                                SurfaceSlate,
                                DeepSlate
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "إجمالي الرصيد التشغيلي للمؤسسة",
                            color = RoyalGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        // Growth Badge
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "+12.5% مقارنة بالشهر السابق",
                                color = EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Large Balance in Gold
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "485,250.00",
                            color = RoyalGold,
                            fontSize = 32.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ر.س",
                            color = RoyalGoldLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Progress Indicator
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "معدل تغطية التدفق النقدي",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "78% ممتاز",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.78f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(RoyalGold, EmeraldGreen)
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Quick Metrics Split (2 mini cards: Income & Expenses)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Income KPI Card
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "إجمالي الإيرادات",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "620,000.00 ر.س",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }
            }

            // Expenses KPI Card
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, CoralRed.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "إجمالي المصروفات",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CoralRedBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = CoralRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "134,750.00 ر.س",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralRed
                    )
                }
            }
        }
    }
}

/**
 * 4. Gateway Action Card (One of the 8 Gateways)
 */
@Composable
private fun GatewayCard(
    gateway: GatewayItem,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CardBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clickable { onClick() }
            .testTag("gateway_card_${gateway.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon in Gold / Tinted Container
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldSurface)
                        .border(1.dp, GoldBorderLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = gateway.icon,
                        contentDescription = gateway.title,
                        tint = gateway.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Optional Badge
                gateway.badge?.let { b ->
                    Surface(
                        color = GoldSurface,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, GoldBorderLight)
                    ) {
                        Text(
                            text = b,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = gateway.accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = gateway.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = gateway.subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 5. Executive Bottom Navigation Bar
 */
@Composable
private fun ExecutiveBottomBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    NavigationBar(
        containerColor = PureWhite,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, CardBorderSubtle))
            .testTag("executive_bottom_navigation")
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onSelect(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
            label = { Text("الرئيسية", fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepSlate,
                selectedTextColor = DeepSlate,
                indicatorColor = RoyalGold,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onSelect(1) },
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "العمليات") },
            label = { Text("العمليات", fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepSlate,
                selectedTextColor = DeepSlate,
                indicatorColor = RoyalGold,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onSelect(2) },
            icon = { Icon(Icons.Default.Analytics, contentDescription = "التحليلات") },
            label = { Text("التحليلات", fontWeight = if (selectedIndex == 2) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepSlate,
                selectedTextColor = DeepSlate,
                indicatorColor = RoyalGold,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )
        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onSelect(3) },
            icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "المزيد") },
            label = { Text("المزيد", fontWeight = if (selectedIndex == 3) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepSlate,
                selectedTextColor = DeepSlate,
                indicatorColor = RoyalGold,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )
    }
}

/**
 * Preview for Android Studio and local verification (Arabic RTL)
 */
@Preview(showBackground = true, locale = "ar")
@Composable
fun HomeScreenPreview() {
    SmartCostProTheme {
        HomeScreen()
    }
}
