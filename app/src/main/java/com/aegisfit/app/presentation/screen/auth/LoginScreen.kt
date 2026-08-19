package com.aegisfit.app.presentation.screen.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aegisfit.app.presentation.theme.AegisDarkBackground
import com.aegisfit.app.presentation.theme.AegisDarkSurface
import com.aegisfit.app.presentation.theme.NeonCyan
import com.aegisfit.app.presentation.theme.NeonGreen

@Composable
fun LoginScreen(viewModel: AuthViewModel = hiltViewModel()) {
    var isLoginMode by rememberSaveable { mutableStateOf(true) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val authResult by viewModel.authState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val isBusy = authResult is AuthResult.Loading

    fun submit() {
        val cleanEmail = email.trim()
        when {
            cleanEmail.isBlank() || password.isBlank() ->
                viewModel.setError("Enter your email and password.")
            !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() ->
                viewModel.setError("Enter a valid email address.")
            !isLoginMode && password.length < 8 ->
                viewModel.setError("Use at least 8 characters for your password.")
            !isLoginMode && (!password.any(Char::isLetter) || !password.any(Char::isDigit)) ->
                viewModel.setError("Your password needs at least one letter and one number.")
            password.length > 128 ->
                viewModel.setError("Password must be 128 characters or fewer.")
            else -> {
                focusManager.clearFocus()
                if (isLoginMode) viewModel.signIn(cleanEmail, password)
                else viewModel.signUp(cleanEmail, password)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisDarkBackground)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Text("N", color = AegisDarkBackground, fontWeight = FontWeight.Black, fontSize = 24.sp)
                }
                Spacer(Modifier.size(14.dp))
                Column {
                    Text(
                        text = "NHT FITNESS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Train with clarity.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            Text(
                text = if (isLoginMode) "Welcome back" else "Build your routine",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Training, nutrition, recovery, and progress in one private workspace.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(28.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = AegisDarkSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    TabRow(
                        selectedTabIndex = if (isLoginMode) 0 else 1,
                        containerColor = Color.Transparent,
                        divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) },
                        indicator = { positions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(positions[if (isLoginMode) 0 else 1]),
                                color = NeonCyan
                            )
                        }
                    ) {
                        listOf("Sign in", "Create account").forEachIndexed { index, label ->
                            Tab(
                                selected = (isLoginMode && index == 0) || (!isLoginMode && index == 1),
                                onClick = {
                                    isLoginMode = index == 0
                                    viewModel.resetState()
                                },
                                text = { Text(label, fontWeight = FontWeight.SemiBold) }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it.take(254)
                            if (authResult is AuthResult.Error) viewModel.resetState()
                        },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            focusedLeadingIconColor = NeonCyan
                        )
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it.take(128)
                            if (authResult is AuthResult.Error) viewModel.resetState()
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            focusedLeadingIconColor = NeonCyan
                        )
                    )

                    if (isLoginMode) {
                        TextButton(
                            onClick = {
                                val cleanEmail = email.trim()
                                if (Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                                    viewModel.sendPasswordReset(cleanEmail)
                                } else {
                                    viewModel.setError("Enter your email first, then request a reset link.")
                                }
                            },
                            enabled = !isBusy,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Forgot password?")
                        }
                    } else {
                        Spacer(Modifier.height(12.dp))
                    }

                    when (val result = authResult) {
                        is AuthResult.Error -> Text(
                            text = result.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        is AuthResult.Notice -> Text(
                            text = result.message,
                            color = NeonGreen,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        else -> Unit
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = ::submit,
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = AegisDarkBackground
                        )
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = AegisDarkBackground
                            )
                            Spacer(Modifier.size(10.dp))
                            Text("Please wait")
                        } else {
                            Text(
                                text = if (isLoginMode) "Sign in" else "Create account",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "Your activity stays tied to your authenticated account and protected cloud record.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
