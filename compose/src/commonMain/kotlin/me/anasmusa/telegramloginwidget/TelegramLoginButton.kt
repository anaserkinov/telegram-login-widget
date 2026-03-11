package me.anasmusa.telegramloginwidget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import me.anasmusa.shared.TelegramLoginResult

object TelegramDefaults {
    val icon: ImageVector get() = TelegramIcon
    val primaryColor = Color(0xFF54A9EB)
    val disabledPrimaryColor = Color(0xFFABDAFF)
    val iconSize = 24.dp
    val spacing = 8.dp
    val progressStrokeWidth = 2.dp
    val userPhotoSize = 24.dp

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

@Composable
fun TelegramButtonText(
    state: TelegramLoginState,
    text: String = state.buttonContent.text,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        autoSize = autoSize,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramButtonCircularProgress(
    modifier: Modifier =
        Modifier
            .size(TelegramDefaults.userPhotoSize),
    color: Color = LocalContentColor.current,
    strokeWidth: Dp = TelegramDefaults.progressStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        trackColor = trackColor,
        strokeCap = strokeCap,
        gapSize = gapSize,
    )
}

@Composable
fun TelegramButtonUserPhoto(
    state: TelegramLoginState,
    contentDescription: String? = null,
    modifier: Modifier =
        Modifier
            .border(width = 1.dp, color = Color.White, shape = CircleShape)
            .clip(CircleShape)
            .size(TelegramDefaults.userPhotoSize),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
) {
    state.buttonContent.userPhoto?.let { painter ->
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )
    }
}

@Composable
fun TelegramButtonUserPhotoBox(
    state: TelegramLoginState,
    modifier: Modifier =
        Modifier
            .padding(start = TelegramDefaults.spacing)
            .size(TelegramDefaults.userPhotoSize),
    contentAlignment: Alignment = Alignment.CenterStart,
    progress: (@Composable () -> Unit)? = ::TelegramButtonCircularProgress,
    userPhoto: (@Composable (state: TelegramLoginState) -> Unit)? = ::TelegramButtonUserPhoto,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        if (progress != null) {
            AnimatedVisibility(
                visible = state.isLoading,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                progress()
            }
        }

        if (!state.isLoading) {
            userPhoto?.invoke(state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramLoginButton(
    state: TelegramLoginState,
    onResult: (TelegramLoginResult) -> Unit,
    left: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonIcon()
    },
    center: @Composable RowScope.(state: TelegramLoginState) -> Unit = {
        TelegramButtonText(
            state = state,
            modifier =
                Modifier
                    .weight(1f, false),
        )
    },
    right: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonUserPhotoBox(state = state)
    },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = TelegramDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    TelegramLoginButton(
        state = state,
        onResult = onResult,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            left?.invoke(this, state)
            center.invoke(this, state)
            right?.invoke(this, state)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramLoginButton(
    state: TelegramLoginState,
    onResult: (TelegramLoginResult) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = TelegramDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.(state: TelegramLoginState) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    Button(
        onClick = {
            showBottomSheet = true
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
            content(state)
        },
    )
    if (showBottomSheet) {
        TelegramLoginBottomSheet(
            config = state.config,
            modifier = Modifier,
        ) {
            showBottomSheet = false
            state.reload()
            onResult(it)
        }
    }
}

@Composable
fun TelegramLoginButton(
    state: TelegramLoginState,
    onClick: () -> Unit,
    left: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonIcon()
    },
    center: @Composable RowScope.(state: TelegramLoginState) -> Unit = {
        TelegramButtonText(
            state = state,
            modifier =
                Modifier
                    .weight(1f, false),
        )
    },
    right: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonUserPhotoBox(state = state)
    },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = TelegramDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            left?.invoke(this, state)
            center.invoke(this, state)
            right?.invoke(this, state)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramLoginOutlinedButton(
    state: TelegramLoginState,
    onResult: (TelegramLoginResult) -> Unit,
    left: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonCircleIcon()
    },
    center: @Composable RowScope.(state: TelegramLoginState) -> Unit = {
        TelegramButtonText(
            state = state,
            modifier =
                Modifier
                    .weight(1f, false),
        )
    },
    right: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonUserPhotoBox(state = state)
    },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    TelegramLoginOutlinedButton(
        state = state,
        onResult = onResult,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            left?.invoke(this, state)
            center.invoke(this, state)
            right?.invoke(this, state)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramLoginOutlinedButton(
    state: TelegramLoginState,
    onResult: (TelegramLoginResult) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.(state: TelegramLoginState) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    OutlinedButton(
        onClick = {
            showBottomSheet = true
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
            content(state)
        },
    )
    if (showBottomSheet) {
        TelegramLoginBottomSheet(
            config = state.config,
            modifier = Modifier,
        ) {
            showBottomSheet = false
            state.reload()
            onResult(it)
        }
    }
}

@Composable
fun TelegramLoginOutlinedButton(
    state: TelegramLoginState,
    onClick: () -> Unit,
    left: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonCircleIcon()
    },
    center: @Composable RowScope.(state: TelegramLoginState) -> Unit = {
        TelegramButtonText(
            state = state,
            modifier =
                Modifier
                    .weight(1f, false),
        )
    },
    right: (@Composable RowScope.(state: TelegramLoginState) -> Unit)? = {
        TelegramButtonUserPhotoBox(state = state)
    },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            left?.invoke(this, state)
            center.invoke(this, state)
            right?.invoke(this, state)
        },
    )
}
