package me.anasmusa.telegramloginwidget

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TelegramIcon: ImageVector
    get() {
        if (_TelegramIcon != null) {
            return _TelegramIcon!!
        }
        _TelegramIcon =
            ImageVector
                .Builder(
                    name = "TelegramIcon",
                    defaultWidth = 64.dp,
                    defaultHeight = 64.dp,
                    viewportWidth = 100f,
                    viewportHeight = 100f,
                ).apply {
                    path(fill = SolidColor(Color.White)) {
                        moveTo(87.72f, 12.14f)
                        curveTo(76.42f, 17.24f, 23.66f, 39.09f, 9.08f, 45.05f)
                        curveToRelative(-9.78f, 3.82f, -4.05f, 7.39f, -4.05f, 7.39f)
                        reflectiveCurveToRelative(8.35f, 2.86f, 15.5f, 5.01f)
                        curveToRelative(7.15f, 2.15f, 10.97f, -0.24f, 10.97f, -0.24f)
                        lineToRelative(33.62f, -22.65f)
                        curveToRelative(11.92f, -8.11f, 9.06f, -1.43f, 6.2f, 1.43f)
                        curveToRelative(-6.2f, 6.2f, -16.45f, 15.98f, -25.04f, 23.84f)
                        curveToRelative(-3.82f, 3.34f, -1.91f, 6.2f, -0.24f, 7.63f)
                        curveToRelative(6.2f, 5.25f, 23.13f, 15.98f, 24.08f, 16.69f)
                        curveToRelative(5.04f, 3.57f, 14.94f, 8.7f, 16.45f, -2.15f)
                        curveToRelative(0f, 0f, 5.96f, -37.44f, 5.96f, -37.44f)
                        curveToRelative(1.91f, -12.64f, 3.82f, -24.32f, 4.05f, -27.66f)
                        curveTo(96.31f, 8.8f, 87.72f, 12.14f, 87.72f, 12.14f)
                        close()
                    }
                }.build()

        return _TelegramIcon!!
    }

@Suppress("ObjectPropertyName")
private var _TelegramIcon: ImageVector? = null
