package dev.kadyko.mycurrency.presentation.ui

import android.os.SystemClock
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // --- Отображаем индикатор загрузки ---
        CircularProgressIndicator(
            strokeWidth = 4.dp
        )
        // ---

        // --- Запускаем таймер на 4 секунды ---
        var isTimerFinished by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            SystemClock.sleep(4000L) // 4 секунды
            isTimerFinished = true
        }

        // --- При завершении таймера вызываем onSplashFinished ---
        if (isTimerFinished) {
            onSplashFinished()
        }
        // ---
    }
}