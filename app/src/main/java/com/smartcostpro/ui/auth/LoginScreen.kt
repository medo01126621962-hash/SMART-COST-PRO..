package com.smartcostpro.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.smartcostpro.R
import com.smartcostpro.ui.theme.CardBorderSubtle
import com.smartcostpro.ui.theme.CoralRed
import com.smartcostpro.ui.theme.DeepSlate
import com.smartcostpro.ui.theme.EmeraldGreen
import com.smartcostpro.ui.theme.GoldBorder
import com.smartcostpro.ui.theme.GoldBorderLight
import com.smartcostpro.ui.theme.GoldPrimary
import com.smartcostpro.ui.theme.GoldSurface
import com.smartcostpro.ui.theme.PearlWhite
import com.smartcostpro.ui.theme.PureWhite
import com.smartcostpro.ui.theme.RoyalGold
import com.smartcostpro.ui.theme.RoyalGoldDark
import com.smartcostpro.ui.theme.SmartCostProTheme
import com.smartcostpro.ui.theme.TextDark
import com.smartcostpro.ui.theme.TextMuted
import com.smartcostpro.ui.theme.TextPlaceholder

/**
 * Production-ready LoginScreen pixel-perfectly matching the visual reference (image_880d3124.png).
 * Incorporates the official logo emblem (unnamed_fdb58db1.jpg), RTL Arabic & English toggling,
 * AES-256 genuine encryption, BiometricPrompt, Google Sign-In, and full validation.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isArabic = uiState.language == "ar"
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.generalSuccess) {
        uiState.generalSuccess?.let { snackbarHostState.showSnackbar(it) }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color(0xFFF6F8FA),
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 460.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Top Navigation & Action Row (EN Toggle, Status Dot, Arrow)
                    TopUtilityRow(
                        isArabic = isArabic,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onArrowClick = {
                            viewModel.loginAsGuest(onLoginSuccess)
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. Official Brand Card & Emblems
                    BrandHeaderSection(isArabic = isArabic)

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. Main White Login Card
                    MainLoginCard(
                        uiState = uiState,
                        isArabic = isArabic,
                        onIdentifierChange = { viewModel.onIdentifierChange(it) },
                        onPasswordChange = { viewModel.onPasswordChange(it) },
                        onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
                        onToggleRememberDevice = { viewModel.toggleRememberDevice() },
                        onForgotPasswordClick = { viewModel.openForgotPasswordDialog() },
                        onLoginClick = { viewModel.login(onLoginSuccess) },
                        onBiometricClick = {
                            val activity = context as? FragmentActivity
                            if (activity != null) {
                                val (available, errorMsg) = BiometricHelper.isBiometricAvailable(context, isArabic)
                                if (available) {
                                    BiometricHelper.showBiometricPrompt(
                                        activity = activity,
                                        title = if (isArabic) "التحقق البيومتري المعتمد" else "Biometric Authentication",
                                        subtitle = if (isArabic) "يرجى مسح البصمة لتسجيل الدخول السريع" else "Scan your biometric credential to sign in",
                                        negativeButtonText = if (isArabic) "إلغاء" else "Cancel",
                                        onSuccess = { _ -> viewModel.loginWithBiometrics(onLoginSuccess) },
                                        onError = { err -> viewModel.showInfoMessage(err) }
                                    )
                                } else {
                                    viewModel.showInfoMessage(errorMsg ?: "البصمة غير متاحة")
                                }
                            } else {
                                viewModel.loginWithBiometrics(onLoginSuccess)
                            }
                        },
                        onGoogleSignInClick = { viewModel.onGoogleSignInClicked() }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // 4. Footer Links ("ليس لديك حساب؟ سجل الآن" & "الدخول كزائر")
                    FooterLinksSection(
                        isArabic = isArabic,
                        onRegisterClick = { viewModel.openRegisterDialog() },
                        onGuestClick = { viewModel.loginAsGuest(onLoginSuccess) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Forgot Password Dialog
        if (uiState.showForgotPasswordDialog) {
            ForgotPasswordDialog(
                isArabic = isArabic,
                email = uiState.forgotPasswordEmail,
                errorMessage = uiState.forgotPasswordError,
                isLoading = uiState.isForgotPasswordLoading,
                onEmailChange = { viewModel.onForgotPasswordEmailChange(it) },
                onDismiss = { viewModel.closeForgotPasswordDialog() },
                onSubmit = { viewModel.submitForgotPassword() }
            )
        }

        // Register Dialog
        if (uiState.showRegisterDialog) {
            RegisterDialog(
                uiState = uiState,
                isArabic = isArabic,
                onNameChange = { viewModel.onRegisterNameChange(it) },
                onEmailChange = { viewModel.onRegisterEmailChange(it) },
                onPasswordChange = { viewModel.onRegisterPasswordChange(it) },
                onConfirmPasswordChange = { viewModel.onRegisterConfirmPasswordChange(it) },
                onDismiss = { viewModel.closeRegisterDialog() },
                onSubmit = { viewModel.submitRegister(onLoginSuccess) }
            )
        }

        // Information / Setup Dialog
        uiState.infoMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissInfoMessage() },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تنبيه النظام المالي" else "System Notice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextDark
                        )
                    }
                },
                text = {
                    Text(
                        text = msg,
                        fontSize = 14.sp,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.dismissInfoMessage() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepSlate,
                            contentColor = RoyalGold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isArabic) "حسناً" else "OK", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}

/**
 * 1. Top Utility Bar: EN Toggle, Status Dot, Arrow
 */
@Composable
private fun TopUtilityRow(
    isArabic: Boolean,
    onToggleLanguage: () -> Unit,
    onArrowClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Language Toggle Pill ("EN" or "AR")
        Surface(
            color = Color(0xFFFFFDF5),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier
                .clickable { onToggleLanguage() }
                .testTag("language_toggle_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isArabic) "EN" else "AR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = RoyalGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Center Live Status Indicator
        Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF34D399))
                )
            }
        }

        // Top Forward / Back Arrow
        IconButton(
            onClick = onArrowClick,
            modifier = Modifier
                .size(36.dp)
                .background(PureWhite, CircleShape)
                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                .testTag("top_arrow_button")
        ) {
            Icon(
                imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigation Arrow",
                tint = Color(0xFF475569),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * 2. Official Brand Card & Emblems (Strictly matching image_880d3124.png)
 */
@Composable
private fun BrandHeaderSection(isArabic: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Logo Squircle Card with Navy/Gold Shield Badge
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(108.dp)
        ) {
            // White Squircle Card with Shadow
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_smart_cost_official_logo),
                        contentDescription = "Official Smart Cost Pro Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )
                }
            }

            // Circular Navy Badge with Gold Shield Icon at bottom-end corner
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, PureWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title: Smart Cost Pro (Pro in Royal Gold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Smart Cost ",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "Pro",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalGold,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Subtitle: منظومة الإدارة والتكاليف التنفيذية الذكية
        Text(
            text = if (isArabic) "منظومة الإدارة والتكاليف التنفيذية الذكية" else "Executive Smart Cost & Management System",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Verified Developer / User Badge: eng.mamdouh saad
        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 1.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "eng.mamdouh saad",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A),
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Badge",
                    tint = RoyalGold,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * 3. Main White Login Card (100% matched to image_880d3124.png)
 */
@Composable
private fun MainLoginCard(
    uiState: LoginUiState,
    isArabic: Boolean,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleRememberDevice: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBiometricClick: () -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("main_login_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Gold Accent Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, RoyalGold, Color.Transparent)
                        )
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field 1: Identifier (Username or Email)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isArabic) "اسم المستخدم أو البريد الإلكتروني" else "Username or Email",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Identifier Input Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(
                        1.dp,
                        if (uiState.identifierError != null) CoralRed else Color(0xFFE2E8F0),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Prefix Icon Container Box
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = uiState.identifier,
                        onValueChange = onIdentifierChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(RoyalGold),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        decorationBox = { innerTextField ->
                            if (uiState.identifier.isEmpty()) {
                                Text(
                                    text = if (isArabic) "مثال: CEO@smartcost.pro أو 29401" else "e.g., CEO@smartcost.pro or 29401",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("login_identifier_input")
                    )
                }
            }

            if (uiState.identifierError != null) {
                Text(
                    text = uiState.identifierError,
                    fontSize = 11.sp,
                    color = CoralRed,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 2: Password with AES-256 Bit Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "كلمة المرور المشفرة" else "Encrypted Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // AES-256 Bit Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "bit AES-256",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF059669),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Password Input Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(
                        1.dp,
                        if (uiState.passwordError != null) CoralRed else Color(0xFFE2E8F0),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Password Visibility Toggle Box
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable { onTogglePasswordVisibility() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(RoyalGold),
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
                        decorationBox = { innerTextField ->
                            if (uiState.password.isEmpty()) {
                                Text(
                                    text = "••••••••••••",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("login_password_input")
                    )
                }
            }

            if (uiState.passwordError != null) {
                Text(
                    text = uiState.passwordError,
                    fontSize = 11.sp,
                    color = CoralRed,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Remember Device Checkbox & Forgot Password Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Remember Device Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onToggleRememberDevice() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (uiState.rememberDevice) Color(0xFF0F172A) else PureWhite)
                            .border(
                                1.5.dp,
                                if (uiState.rememberDevice) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.rememberDevice) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تذكر هذا الجهاز الآمن" else "Remember this secure device",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                // Forgot Password Gold Link
                Text(
                    text = if (isArabic) "استعادة كلمة المرور / الرمز؟" else "Forgot password / code?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RoyalGold,
                    modifier = Modifier
                        .clickable { onForgotPasswordClick() }
                        .testTag("forgot_password_link")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Error banner if any
            if (uiState.generalError != null) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = uiState.generalError,
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Primary Dark Login Button with Gold Arrow
            Button(
                onClick = onLoginClick,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = PureWhite,
                    disabledContainerColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_login_button")
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = RoyalGold,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isArabic) "تسجيل الدخول" else "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Divider: "أو تسجيل الدخول السريع"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                Text(
                    text = if (isArabic) "أو تسجيل الدخول السريع" else "Or quick sign in",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Biometric Card (Face ID / البصمة)
            Surface(
                color = Color(0xFFFFFDF5),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBiometricClick() }
                    .testTag("biometric_login_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "الدخول السريع عبر Face ID / البصمة" else "Quick Sign-In via Face ID / Biometrics",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "التحقق البيومتري المعتمد لـ Android Keystore" else "Hardware-backed Android Keystore Biometrics",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Fingerprint Squircle Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PureWhite)
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Fingerprint",
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google Sign-In Button
            Button(
                onClick = onGoogleSignInClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = Color(0xFF0F172A)
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_login_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Google 4-Color Icon Graphic representation
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4285F4)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isArabic) "الدخول بحساب Google" else "Sign in with Google",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }
    }
}

/**
 * 4. Footer Links ("ليس لديك حساب؟ سجل الآن" & "الدخول كزائر")
 */
@Composable
private fun FooterLinksSection(
    isArabic: Boolean,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Register Link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isArabic) "ليس لديك حساب؟ " else "Don't have an account? ",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = if (isArabic) "سجل الآن" else "Register Now",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalGold,
                modifier = Modifier
                    .clickable { onRegisterClick() }
                    .testTag("register_now_link")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Guest Mode Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable { onGuestClick() }
                .padding(8.dp)
                .testTag("continue_as_guest_link")
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isArabic) "الدخول كزائر" else "Continue as Guest",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
        }
    }
}

/**
 * Forgot Password Dialog
 */
@Composable
private fun ForgotPasswordDialog(
    isArabic: Boolean,
    email: String,
    errorMessage: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "استعادة كلمة المرور" else "Reset Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isArabic) {
                        "أدخل بريدك الإلكتروني المسجل لإرسال رمز استعادة الحساب:"
                    } else {
                        "Enter your registered email to receive a password reset code:"
                    },
                    fontSize = 13.sp,
                    color = TextMuted
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text(if (isArabic) "البريد الإلكتروني" else "Email") },
                    placeholder = { Text("name@smartcost.pro") },
                    singleLine = true,
                    isError = errorMessage != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalGold,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(text = errorMessage, fontSize = 11.sp, color = CoralRed)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepSlate,
                    contentColor = RoyalGold
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = RoyalGold,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (isArabic) "إرسال الرمز" else "Send Code", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel", color = TextMuted)
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(18.dp)
    )
}

/**
 * Register Dialog
 */
@Composable
private fun RegisterDialog(
    uiState: LoginUiState,
    isArabic: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "إنشاء حساب تنفيذي جديد" else "Create Executive Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.registerName,
                    onValueChange = onNameChange,
                    label = { Text(if (isArabic) "الاسم بالكامل" else "Full Name") },
                    placeholder = { Text("م. ممدوح سعد") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.registerEmail,
                    onValueChange = onEmailChange,
                    label = { Text(if (isArabic) "البريد الإلكتروني" else "Email") },
                    placeholder = { Text("name@smartcost.pro") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.registerPassword,
                    onValueChange = onPasswordChange,
                    label = { Text(if (isArabic) "كلمة المرور" else "Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.registerConfirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text(if (isArabic) "تأكيد كلمة المرور" else "Confirm Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.registerError != null) {
                    Text(text = uiState.registerError, fontSize = 11.sp, color = CoralRed)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !uiState.isRegisterLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepSlate,
                    contentColor = RoyalGold
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (uiState.isRegisterLoading) {
                    CircularProgressIndicator(color = RoyalGold, modifier = Modifier.size(18.dp))
                } else {
                    Text(if (isArabic) "إنشاء الحساب" else "Create Account", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel", color = TextMuted)
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(18.dp)
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun LoginScreenPreview() {
    SmartCostProTheme {
        // Preview dummy container
        Box(modifier = Modifier.fillMaxSize())
    }
}
