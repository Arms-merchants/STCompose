package com.example.stcompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.stcompose.R

/**
 *    author : heyueyang
 *    time   : 2023/03/14
 *    desc   :
 *    version: 1.0
 */
open class STAssets private constructor(var background: Int, var illos: Int, var logo: Int) {
    object LightSTAssets : STAssets(
        background = R.drawable.ic_light_welcome_bg,
        illos = R.drawable.ic_light_welcome_illos,
        logo = R.drawable.ic_light_logo
    )

    object DartSTAssets : STAssets(
        background = R.drawable.ic_dark_welcome_bg,
        illos = R.drawable.ic_dark_welcome_illos,
        logo = R.drawable.ic_dark_logo
    )
}

internal var LocalSTAssets = staticCompositionLocalOf {
    STAssets.DartSTAssets as STAssets
}

val MaterialTheme.stAssets
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) {
        STAssets.DartSTAssets
    } else {
        STAssets.LightSTAssets
    }



