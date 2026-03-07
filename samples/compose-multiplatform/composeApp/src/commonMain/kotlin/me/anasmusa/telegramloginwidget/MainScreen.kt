package me.anasmusa.telegramloginwidget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.anasmusa.shared.TelegramLoginResult

@Composable
fun MainScreen(
    user: TelegramLoginResult.Success,
    backToLoginScreen: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            user.photoUrl,
            modifier =
                Modifier
                    .size(120.dp)
                    .clip(CircleShape),
            contentDescription = null,
        )
        Spacer(
            modifier = Modifier.height(16.dp),
        )
        Text(
            text = user.firstName + " " + (user.lastName ?: ""),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        )

        user.username?.let {
            Text(text = it)
        }

        Spacer(
            modifier =
                Modifier
                    .weight(1f),
        )

        Button(
            onClick = backToLoginScreen,
        ) {
            Text(
                text = "Back to Login screen",
            )
        }
        Button(
            onClick = onLogout,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                ),
        ) {
            Text(
                text = "Log out",
            )
        }
    }
}
