package ipca.am1.sergio.stopwatchv3.helpers

import android.content.Context
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.extensions.isBlackAndWhiteTheme
import com.simplemobiletools.commons.extensions.isWhiteTheme

val Context.config : Config get() = Config.newInstance(applicationContext)

fun Context.getAdjustedPrimaryColor() = when {
    isWhiteTheme() || isBlackAndWhiteTheme() -> baseConfig.accentColor
    else -> baseConfig.primaryColor
}