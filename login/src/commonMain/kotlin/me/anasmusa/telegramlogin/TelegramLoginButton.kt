package me.anasmusa.telegramlogin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

object TelegramDefaults {
    val icon: ImageVector get() = TelegramIcon
    val primaryColor = Color(0xFF54A9EB)
    val disabledPrimaryColor = Color(0xFFABDAFF)
    val iconSize = 24.dp
    val spacing = 8.dp

    @Composable
    fun buttonColors(
        containerColor: Color = this.primaryColor,
        contentColor: Color = Color.White,
        disabledContainerColor: Color = this.disabledPrimaryColor,
        disabledContentColor: Color = Color.White,
    ): ButtonColors =
        ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )
}

@Composable
fun TelegramButtonIcon(
    imageVector: ImageVector = TelegramDefaults.icon,
    contentDescription: String? = null,
    modifier: Modifier =
        Modifier
            .padding(end = TelegramDefaults.spacing)
            .size(TelegramDefaults.iconSize),
    tint: Color = Color.White,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun TelegramButtonCircleIcon(
    imageVector: ImageVector = TelegramDefaults.icon,
    contentDescription: String? = null,
    modifier: Modifier =
        Modifier
            .padding(end = TelegramDefaults.spacing)
            .size(TelegramDefaults.iconSize)
            .background(color = TelegramDefaults.primaryColor, CircleShape)
            .padding(6.dp)
            .offset(x = (-1).dp),
    tint: Color = Color.White,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramLoginButton(
    config: TelegramLoginConfig,
    onResult: (TelegramLoginResult) -> Unit,
    icon: (@Composable RowScope.() -> Unit)? = {
        TelegramButtonIcon()
    },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = TelegramDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Button(
        onClick = {
            showDialog = true
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            icon?.invoke(this)
            content()
        },
    )

    if (showDialog) {
        TelegramLoginDialog(
            config = config,
        ) {
            showDialog = false
            onResult(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramLoginOutlinedButton(
    config: TelegramLoginConfig,
    onResult: (TelegramLoginResult) -> Unit,
    icon: (@Composable RowScope.() -> Unit)? = {
        TelegramButtonCircleIcon()
    },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    OutlinedButton(
        onClick = {
            showDialog = true
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            icon?.invoke(this)
            content()
        },
    )
    if (showDialog) {
        TelegramLoginDialog(
            config = config,
        ) {
            showDialog = false
            onResult(it)
        }
    }
}
