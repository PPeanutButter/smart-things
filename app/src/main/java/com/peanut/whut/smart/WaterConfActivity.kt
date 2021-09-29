package com.peanut.whut.smart

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceScreen
import com.peanut.whut.smart.Tools.editPreference
import com.peanut.whut.smart.Tools.preference
import com.peanut.whut.smart.WaterWidgetProvider.Companion.loadWaterPref

class WaterConfActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.putExtra("ActivityName", "水卡配置")
        /**
         * 2. Perform your App Widget configuration.
         */
        onPreferenceLoad = {
            it.findPreference<PreferenceScreen>("panel")!!.apply {
                editPreference(
                    key = "${mAppWidgetId}_device_type",
                    title = "出水口名称",
                    summary = "如开水口"
                )
                editPreference(
                    key = "${mAppWidgetId}_device_location",
                    title = "设备位置标识",
                    summary = "如实验室"
                )
                editPreference(
                    key = "${mAppWidgetId}_device_id",
                    title = "设备的唯一编码",
                    summary = "扫描二维码获取"
                )
                editPreference(
                    key = "water_user_auth",
                    title = "用户唯一标示",
                    summary = "抓包Header获取Auth"
                )
                preference(key = "", title = "点我完成") {
                    /**
                     * 3. When the configuration is complete, get an instance of the AppWidgetManager by
                     * calling getInstance(Context):
                     */
                    val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(this@WaterConfActivity)
                    /**
                     * 4. Update the App Widget with a RemoteViews layout by calling
                     * updateAppWidget(int, RemoteViews)
                     */
                    val data = loadWaterPref(this@WaterConfActivity, mAppWidgetId)
                    WaterWidgetProvider.updateAppWidget(
                        this@WaterConfActivity, appWidgetManager,
                        mAppWidgetId, data
                    )
                    /**
                     * 5. Finally, create the return Intent, set it with the Activity result,
                     * and finish the Activity:
                     */
                    val resultValue = Intent()
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                    setResult(RESULT_OK, resultValue)
                    finish()
                }
            }
        }
        super.onCreate(savedInstanceState)
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED)
        /**
         * # Updating the App Widget from the configuration Activity
         * When an App Widget uses a configuration Activity, it is the responsibility of the
         * Activity to update the App Widget when configuration is complete. You can do so by
         * requesting an update directly from the 'AppWidgetManager'.
         *
         * Here's a summary of the procedure to properly update the App Widget and close the
         * configuration Activity:
         *
         * 1. First, get the App Widget ID from the Intent that launched the Activity
         */
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            // Find the widget id from the intent.
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            // If they gave us an intent without the widget id, just bail.
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish()
            }
        }
    }

    companion object {
        private var mAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    }
}