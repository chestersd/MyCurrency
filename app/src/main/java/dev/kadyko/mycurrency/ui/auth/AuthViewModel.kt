package dev.kadyko.mycurrency.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val user: FirebaseUser? = null,
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(user = auth.currentUser)
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _uiState.value = _uiState.value.copy(
                user = firebaseAuth.currentUser,
                isLoading = false
            )
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(emailInput = email)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(passwordInput = password)
    }

    fun signInWithEmail() {
        val email = _uiState.value.emailInput.trim()
        val password = _uiState.value.passwordInput.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(message = "Please enter email and password")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, message = null)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Signed in successfully!")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = task.exception?.localizedMessage ?: "Sign in failed"
                    )
                }
            }
    }

    fun signUpWithEmail() {
        val email = _uiState.value.emailInput.trim()
        val password = _uiState.value.passwordInput.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(message = "Please enter email and password")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, message = null)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Account created successfully!")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = task.exception?.localizedMessage ?: "Sign up failed"
                    )
                }
            }
    }

    fun signInAnonymously() {
        _uiState.value = _uiState.value.copy(isLoading = true, message = null)
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Signed in as Guest")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = task.exception?.localizedMessage ?: "Guest login failed"
                    )
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _uiState.value = _uiState.value.copy(user = null, message = "Signed out")
    }
}
