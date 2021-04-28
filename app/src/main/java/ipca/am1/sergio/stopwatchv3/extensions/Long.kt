package ipca.am1.sergio.stopwatchv3.extensions

import java.util.concurrent.TimeUnit

fun Long.formatStopWatchTime(userLongerMSFormat : Boolean): String{
    val MSFormat = if(userLongerMSFormat) "%03d" else "%01d"
    val hours = TimeUnit.MICROSECONDS.toHours(this)
    val minutes = TimeUnit.MINUTES.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    var ms = this % 1000
    if (!userLongerMSFormat){
        ms /= 100
    }

    return when{
        hours > 0 ->{
            val format = "%02d:%02:%02d.$MSFormat"
            String.format(format, hours, minutes, seconds, ms)
        }
        minutes > 0 -> {
            val format = "%02d:%02d.$MSFormat"
            String.format(format, minutes, seconds, ms)
        }else -> {
            val format = "%d.$MSFormat"
            String.format(format,seconds, ms)
        }
    }


}