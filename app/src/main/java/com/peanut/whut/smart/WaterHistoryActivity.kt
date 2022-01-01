package com.peanut.whut.smart

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.peanut.sdk.miuidialog.MIUIDialog
import com.peanut.whut.smart.Charts.applyData
import com.peanut.whut.smart.Charts.applyStyle
import com.peanut.whut.smart.Tools.buildPost
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class WaterHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_history)
        val fullscreenContent = findViewById<LinearLayout>(R.id.layout)
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        val dialog = MIUIDialog(this).show {
            progress(text = "正在加载数据...")
            cancelOnTouchOutside=false
            cancelable=false
        }
        thread {
            downloadAllOrder{
                val auth = WaterWidgetProvider.loadWaterPref(
                    this,
                    0
                )[1]
                val map = mutableMapOf<String, Int>()
                var i = 1
                it.forEach { order: JSONObject ->
                    runOnUiThread {
                        dialog.setProgressText("正在加载数据(${i++}/${it.length()})...")
                    }
                    val orderId = order.getInt("id")
                    val orderStatus = getOrderStatus(orderId, auth!!)
                    val hotWaterML = orderStatus.getJSONObject("data").getInt("hotWaterML")
                    val warmWaterML = orderStatus.getJSONObject("data").getInt("warmWaterML")
                    val domesticHotML = orderStatus.getJSONObject("data").getInt("domesticHotML")
                    val paidAt = orderStatus.getJSONObject("data").getString("paidAt")
                    val ml = hotWaterML+warmWaterML+domesticHotML
                    val dateKey = paidAt.split(" ")[0]
                    map[dateKey] = if (map.containsKey(dateKey)) (map[dateKey]?:0) + ml else ml
                }
                val sortedMap = map.toList().sortedBy {s-> s.first }.reversed()
                val selected = mutableListOf<Pair<String, Int>>()
                for ((j, v) in sortedMap.withIndex()){
                    if (j < 30)
                        selected.add(v)
                    else
                        break
                }
                runOnUiThread {
                    findViewById<LineChart>(R.id.top_days_line).apply {
                        this.applyStyle()
                            .applyData(selected)
                    }
                    dialog.cancel()
                }
            }
        }
    }

    private fun getOrderStatus(order: Int, auth: String): JSONObject{
        val s = SettingManager.getValue("water_${order}_status", "")
        return if (s != "")
            JSONObject(s)
        else{
            val r = buildPost(
                "https://phoenix.ujing.online/api/v1/water/waterOrderDetail",
                JSONObject("{}").put("orderId", order), auth
            )
            r.run()
            SettingManager["water_${order}_status"] = r.body?:""
            JSONObject(r.body?:"{}")
        }
    }

    private fun JSONArray.forEach(func: (JSONObject)->Unit){
        for (i in 0 until this.length())
            func.invoke(this.getJSONObject(i))
    }

    private fun downloadAllOrder(func: (jsonArray: JSONArray) -> Unit) {
        try {
            val auth = WaterWidgetProvider.loadWaterPref(
                this,
                0
            )[1]
            val r = buildPost("https://phoenix.ujing.online/api/v1/water/waterOrderList",
                JSONObject("{}").put("limit", 999).put("skip", 0),auth!!)
            r.run()
            LogService.log("请求订单数据成功：${r.body}")
            func.invoke(JSONObject(r.body?:"{}").getJSONArray("data"))
        }catch (e:Exception){
            runOnUiThread {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}