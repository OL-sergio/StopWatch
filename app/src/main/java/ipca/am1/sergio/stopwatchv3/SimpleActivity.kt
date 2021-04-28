package ipca.am1.sergio.stopwatchv3

import com.simplemobiletools.commons.activities.BaseSimpleActivity


open class SimpleActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = arrayListOf(
            R.mipmap.ic_launcher_icon
    )
    override fun getAppLauncherName() = getString(R.string.app_launcher_name)
}

