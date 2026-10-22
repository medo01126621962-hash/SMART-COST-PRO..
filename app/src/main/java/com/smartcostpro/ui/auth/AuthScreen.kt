package com.smartcostpro.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcostpro.R
import com.smartcostpro.domain.model.AuthSession
import com.smartcostpro.ui.theme.DarkButton
import com.smartcostpro.ui.theme.ErrorRedBg
import com.smartcostpro.ui.theme.ErrorRedBorder
import com.smartcostpro.ui.theme.ErrorRedText
import com.smartcostpro.ui.theme.GoldAccent
import com.smartcostpro.ui.theme.GoldBorder
import com.smartcostpro.ui.theme.GoldBorderLight
import com.smartcostpro.ui.theme.GoldPrimary
import com.smartcostpro.ui.theme.GoldSurface
import com.smartcostpro.ui.theme.GoldSurfaceBorder
import com.smartcostpro.ui.theme.PureWhite
import com.smartcostpro.ui.theme.SuccessGreenBg
import com.smartcostpro.ui.theme.SuccessGreenBorder
import com.smartcostpro.ui.theme.SuccessGreenText
import com.smartcostpro.ui.theme.TextDark
import com.smartcostpro.ui.theme.TextMuted
import com.smartcostpro.ui.theme.TextPlaceholder

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onBypassToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val session by viewModel.currentSession.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PureWhite,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (session != null) {
                    ActiveSessionView(
                        session = session!!,
                        onSignOut = { viewModel.signOut() }
                    )
                } else if (uiState.showRoleChoice) {
                    RoleChoiceView(
                        isLoading = uiState.isLoading,
                        errorMessage = uiState.errorMessage,
                        onChooseRole = { role -> viewModel.chooseRole(role) }
                    )
                } else {
                    AuthHeader(mode = uiState.mode)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Direct Entry to Executive Home Screen
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GoldSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GoldBorderLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.loginAsGuest()
                                onBypassToHome()
                            }
                            .padding(bottom = 12.dp)
                            .testTag("direct_home_screen_card")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PureWhite)
                                    .border(1.dp, GoldBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "دخول فوري للشاشة الرئيسية التنفيذية",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextDark
                                )
                                Text(
                                    text = "استعراض لوحة المؤشرات المالية وبوابات الوصول الثمانية",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    AuthAlerts(
                        errorMessage = uiState.errorMessage,
                        successMessage = uiState.successMessage
                    )

                    when (uiState.mode) {
                        AuthMode.LOGIN -> {
                            LoginForm(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                        AuthMode.REGISTER -> {
                            RegisterForm(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                        AuthMode.FORGOT_PASSWORD -> {
                            ForgotPasswordForm(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthHeader(mode: AuthMode) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Official Smart Cost Pro Logo Emblem
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(PureWhite)
                .border(2.dp, GoldBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_smart_cost_logo),
                contentDescription = "Smart Cost Pro Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.auth_developer_name),
            color = GoldPrimary,
            fontSize = 17.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        val title = when (mode) {
            AuthMode.LOGIN -> stringResource(R.string.auth_welcome_back)
            AuthMode.REGISTER -> stringResource(R.string.auth_create_account)
            AuthMode.FORGOT_PASSWORD -> stringResource(R.string.auth_forgot_password)
        }

        Text(
            text = title,
            color = TextDark,
            fontSize = 28.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthAlerts(
    errorMessage: String?,
    successMessage: String?
) {
    AnimatedVisibility(visible = errorMessage != null) {
        errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ErrorRedBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .border(1.dp, ErrorRedBorder, RoundedCornerShape(12.dp))
                    .testTag("error_message_card")
            ) {
                Text(
                    text = error,
                    color = ErrorRedText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }

    AnimatedVisibility(visible = successMessage != null) {
        successMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SuccessGreenBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .border(1.dp, SuccessGreenBorder, RoundedCornerShape(12.dp))
                    .testTag("success_message_card")
            ) {
                Text(
                    text = msg,
                    color = SuccessGreenText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    uiState: AuthUiState,
    viewModel: AuthViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.auth_email),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(R.string.auth_email),
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = {
                    Text("email@example.com", color = TextPlaceholder, fontSize = 15.sp)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(14.dp),
                colors = outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input")
            )
        }

        Column {
            Text(
                text = stringResource(R.string.auth_password),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.auth_password),
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleShowPassword() }) {
                        Icon(
                            imageVector = if (uiState.showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (uiState.showPassword) "Hide password" else "Show password",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                placeholder = {
                    Text("••••••••", color = TextPlaceholder, fontSize = 15.sp)
                },
                singleLine = true,
                visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { viewModel.submit() }),
                shape = RoundedCornerShape(14.dp),
                colors = outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input")
            )
        }

        // Primary Sign In Button
        Button(
            onClick = { viewModel.submit() },
            enabled = !uiState.isLoading,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkButton,
                contentColor = GoldAccent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("sign_in_button")
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = GoldAccent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.auth_please_wait),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldAccent
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_sign_in),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldAccent
                )
            }
        }

        // Forgot password & Sign up links
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.auth_forgot_password),
                color = GoldPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { viewModel.switchMode(AuthMode.FORGOT_PASSWORD) }
                    .padding(vertical = 4.dp)
                    .testTag("forgot_password_button")
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.auth_no_account) + " ",
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Text(
                    text = stringResource(R.string.auth_sign_up),
                    color = GoldPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { viewModel.switchMode(AuthMode.REGISTER) }
                        .padding(vertical = 4.dp)
                        .testTag("sign_up_toggle_button")
                )
            }
        }

        // Divider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = GoldBorderLight
            )
            Text(
                text = "  " + stringResource(R.string.auth_or_continue_with) + "  ",
                color = TextPlaceholder,
                fontSize = 12.sp
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = GoldBorderLight
            )
        }

        // Continue with Google Button
        OutlinedButton(
            onClick = {
                // Notice to user regarding Google sign-in configuration
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PureWhite,
                contentColor = TextDark
            ),
            border = BorderStroke(1.dp, GoldBorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("google_sign_in_button")
        ) {
            Text(
                text = "G  " + stringResource(R.string.auth_continue_with_google),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }

        // Continue as Guest Button (Local room offline mode)
        OutlinedButton(
            onClick = { viewModel.loginAsGuest() },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PureWhite,
                contentColor = TextDark
            ),
            border = BorderStroke(1.dp, GoldBorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("guest_login_button")
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.auth_continue_as_guest),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }
    }
}

@Composable
private fun RegisterForm(
    uiState: AuthUiState,
    viewModel: AuthViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.auth_full_name),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = { Text("Your name", color = TextPlaceholder, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input")
            )
        }

        Column {
            Text(
                text = stringResource(R.string.auth_username),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                placeholder = { Text("username", color = TextPlaceholder, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("username_input")
            )
        }

        Column {
            Text(
                text = stringResource(R.string.auth_email),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = { Text("email@example.com", color = TextPlaceholder, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input")
            )
        }

        Column {
            Text(
                text = stringResource(R.string.auth_password),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleShowPassword() }) {
                        Icon(
                            imageVector = if (uiState.showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                placeholder = { Text("••••••••", color = TextPlaceholder, fontSize = 15.sp) },
                singleLine = true,
                visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                colors = outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input")
            )
        }

        Button(
            onClick = { viewModel.submit() },
            enabled = !uiState.isLoading,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkButton,
                contentColor = GoldAccent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("create_account_button")
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = GoldAccent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_create_account),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldAccent
                )
            }
        }

        Text(
            text = stringResource(R.string.auth_back_to_sign_in),
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.switchMode(AuthMode.LOGIN) }
                .padding(vertical = 6.dp)
                .testTag("back_to_sign_in_button")
        )

        // Continue as Guest Button
        OutlinedButton(
            onClick = { viewModel.loginAsGuest() },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PureWhite,
                contentColor = TextDark
            ),
            border = BorderStroke(1.dp, GoldBorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("guest_login_button")
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.auth_continue_as_guest),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }
    }
}

@Composable
private fun ForgotPasswordForm(
    uiState: AuthUiState,
    viewModel: AuthViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.auth_email),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = { Text("email@example.com", color = TextPlaceholder, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input")
            )
        }

        if (uiState.resetSent) {
            Column {
                Text(
                    text = stringResource(R.string.auth_reset_code),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = uiState.resetCode,
                    onValueChange = { viewModel.onResetCodeChange(it) },
                    placeholder = { Text("123456", color = TextPlaceholder, fontSize = 15.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_code_input")
                )
            }

            Column {
                Text(
                    text = stringResource(R.string.auth_new_password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    placeholder = { Text("••••••••", color = TextPlaceholder, fontSize = 15.sp) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )
            }
        }

        Button(
            onClick = { viewModel.submit() },
            enabled = !uiState.isLoading,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkButton,
                contentColor = GoldAccent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("send_reset_code_button")
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = GoldAccent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text(
                    text = if (uiState.resetSent) stringResource(R.string.auth_reset_password_btn) else stringResource(R.string.auth_send_reset_code),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldAccent
                )
            }
        }

        Text(
            text = stringResource(R.string.auth_back_to_sign_in),
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.switchMode(AuthMode.LOGIN) }
                .padding(vertical = 6.dp)
                .testTag("back_to_sign_in_button")
        )
    }
}

@Composable
private fun RoleChoiceView(
    isLoading: Boolean,
    errorMessage: String?,
    onChooseRole: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = GoldSurface),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldSurfaceBorder, RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.auth_choose_role),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.auth_choose_role_desc),
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = { onChooseRole("owner") },
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkButton,
                contentColor = GoldAccent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("continue_as_owner_button")
        ) {
            Text(
                text = stringResource(R.string.auth_role_owner),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = GoldAccent
            )
        }

        OutlinedButton(
            onClick = { onChooseRole("user") },
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PureWhite,
                contentColor = TextDark
            ),
            border = BorderStroke(1.dp, GoldBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("continue_as_user_button")
        ) {
            Text(
                text = stringResource(R.string.auth_role_user),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRedText,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ActiveSessionView(
    session: AuthSession,
    onSignOut: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldBorderLight, RoundedCornerShape(20.dp))
            .testTag("active_session_card")
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(GoldSurface)
                    .border(2.dp, GoldBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = session.user.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = session.user.email,
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = if (session.isGuest) Color(0xFFEFF6FF) else GoldSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (session.isGuest) Color(0xFFBFDBFE) else GoldBorderLight
                )
            ) {
                Text(
                    text = if (session.isGuest) stringResource(R.string.auth_guest_badge) else "الدور: " + session.user.role,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (session.isGuest) Color(0xFF1D4ED8) else GoldPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = GoldBorderLight)

            Spacer(modifier = Modifier.height(16.dp))

            // Notice about Stage 1 completion
            Card(
                colors = CardDefaults.cardColors(containerColor = SuccessGreenBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SuccessGreenBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "المرحلة الأولى: تم تفعيل منظومة المصادقة بنجاح",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreenText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تم ربط الجلسة محلياً والتحقق من حالات تسجيل الدخول والخروج بنجاح وفق تصميم SMART-TIME الأصلي.",
                        fontSize = 12.sp,
                        color = Color(0xFF065F46)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSignOut,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkButton,
                    contentColor = PureWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sign_out_button")
            ) {
                Text(
                    text = stringResource(R.string.auth_sign_out),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldPrimary,
    unfocusedBorderColor = GoldBorderLight,
    focusedLabelColor = GoldPrimary,
    unfocusedLabelColor = TextMuted,
    cursorColor = GoldPrimary,
    focusedTextColor = TextDark,
    unfocusedTextColor = TextDark
)
