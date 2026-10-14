package dev.kadyko.mycurrency.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.kadyko.mycurrency.R

@Composable
fun AuthScreen(
    viewModel: AuthViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.user != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val userEmail = state.user?.email ?: stringResource(R.string.guest_user)
                    Text(
                        text = stringResource(R.string.logged_in_as, userEmail),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "UID: ${state.user?.uid}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = viewModel::signOut,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.sign_out))
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = state.emailInput,
                onValueChange = viewModel::onEmailChanged,
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.passwordInput,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = viewModel::signInWithEmail,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.sign_in))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = viewModel::signUpWithEmail,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.sign_up))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = viewModel::signInAnonymously,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.sign_in_guest))
                }
            }
        }

        if (state.message != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.message ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
