package top.yukonga.miuix.kmp

import android.annotation.SuppressLint
import android.os.Build
import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun getRoundedCorner(): Dp = getSystemCornerRadius()

// NewApi. Represents a rounded corner of the display.
@Composable
private fun getSystemCornerRadius(): Dp {
    val insets = LocalView.current.rootWindowInsets
    val density = LocalDensity.current.density
    val roundedCornerRadius = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        insets?.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: getCornerRadiusBottom()
    } else {
        getCornerRadiusBottom()
    }
    return (roundedCornerRadius / density).dp
}

// from https://dev.mi.com/distribute/doc/details?pId=1631
@Composable
@SuppressLint("DiscouragedApi")
fun getCornerRadiusBottom(): Int {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier("rounded_corner_radius_bottom", "dimen", "android")
    return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
}