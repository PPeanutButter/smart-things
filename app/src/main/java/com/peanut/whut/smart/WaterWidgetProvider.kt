package com.peanut.whut.smart

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.RemoteViews
import android.util.Log
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception
import kotlin.concurrent.thread
import android.app.Notification
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.content.Context.NOTIFICATION_SERVICE

class WaterWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds.forEach { appWidgetId ->
            val data = loadWaterPref(context, appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, data = data)
        }
    }

    private fun listenOrderStatus(context: Context, order: Int, auth: String){
        thread {
            while (true){
                try {
                    val r = Http()
                        .setPost(
                            "https://phoenix.ujing.online/api/v1/water/waterOrderDetail",
                            JSONObject("{}").put("orderId", order).toString()
                                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                        )
                        .setHeader("authorization", auth)
                        .setHeader("x-mobile-id", "ce6d36d2-7d19-3a6e-82de-12c3be83ddb9")
                        .setHeader(
                            "user-agent",
                            "MIX 2(Android/9) (com.midea.vm.washer/2.1.32) Weex/0.28.0.1 1080x2030"
                        )
                        .setHeader("x-app-version", "2.1.32")
                        .setHeader("x-app-code", "CA")
                    r.run()
                    println(r.body)
                    val res = JSONObject(r.body?:"{}")
                    if (res.getJSONObject("data").getInt("orderStatus") != 0){
                        val hotWaterML = res.getJSONObject("data").getInt("hotWaterML")
                        val warmWaterML = res.getJSONObject("data").getInt("warmWaterML")
                        val domesticHotML = res.getJSONObject("data").getInt("domesticHotML")
                        val payment = res.getJSONObject("data").getDouble("payment")
                        val desc = "本次取水: ${hotWaterML+warmWaterML+domesticHotML}ml, 消费${payment}元。"
                        notification(context, desc)
                        break
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
                println("listenOrderStatus=${order}")
                Thread.sleep(10000)
            }
        }
    }

    private fun notification(context: Context, desc: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channelId = "ChannelId"
                val notification: Notification = Notification.Builder(context, channelId)
                    .setChannelId(channelId)
                    .setContentTitle("取水完成")
                    .setSmallIcon(R.drawable.water_icon)
                    .setContentText(desc)
                    .build()
                val notificationManager = context
                    .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    channelId,
                    "取水完成通知",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(1, notification)
            }else {
                val notification: Notification = Notification.Builder(context)
                    .setContentTitle("取水完成")
                    .setSmallIcon(R.drawable.water_icon)
                    .setContentText(desc)
                    .build()
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .notify(1, notification)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            val id = intent.extras?.getString("id")
            val auth = intent.extras?.getString("auth")
            thread {
                try {
                    val r = Http()
                        .setPost(
                            "https://phoenix.ujing.online/api/v1/water/createWaterOrder",
                            JSONObject("{}").put("deviceId", id).toString()
                                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                        )
                        .setHeader("authorization", auth!!)
                        .setHeader("x-mobile-id", "ce6d36d2-7d19-3a6e-82de-12c3be83ddb9")
                        .setHeader(
                            "user-agent",
                            "MIX 2(Android/9) (com.midea.vm.washer/2.1.32) Weex/0.28.0.1 1080x2030"
                        )
                        .setHeader("x-app-version", "2.1.32")
                        .setHeader("x-app-code", "CA")
                    r.run()
                    Handler(context.mainLooper).post {
                        println(r.body)
                        listenOrderStatus(context, JSONObject(r.body?:"{\"data\":{\"orderId\":44366141}}")
                                .getJSONObject("data")
                                .getInt("orderId"), auth)
                        Toast.makeText(context,r.res?.code.toString(),Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    Handler(context.mainLooper).post {
                        e.printStackTrace()
                        Toast.makeText(context,e.localizedMessage?:"error",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        super.onReceive(context, intent)
    }

    companion object {
        // log tag
        private const val TAG = "ExampleAppWidgetProvider"
        private const val ACTION = "CREATE_WATER_ORDER"

        @SuppressLint("UnspecifiedImmutableFlag")
        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int, data: List<String?>
        ) {
            Log.d(TAG, "updateAppWidget appWidgetId=$appWidgetId")
            // Construct the RemoteViews object.  It takes the package name (in our case, it's our
            // package, but it needs this because on the other side it's the widget host inflating
            // the layout from our package).
            val views = RemoteViews(context.packageName, R.layout.water_widget)
            views.setTextViewText(R.id.device, data[2])
            views.setTextViewText(R.id.location, data[3])
            val intent = Intent(context, WaterWidgetProvider::class.java)
            intent.action = ACTION
            intent.putExtra("id", data[0])
            intent.putExtra("auth", data[1])
            val p = PendingIntent.getBroadcast(
                context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.water_layout, p)
            // Tell the widget manager
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun loadWaterPref(context: Context, appWidgetId: Int): List<String?> {
            val prefs = context.getSharedPreferences(Tools.prefsNAME, 0)
            val deviceId = prefs.getString("${appWidgetId}_device_id", null)
            val userAuth = prefs.getString("water_user_auth", null)
            val deviceType = prefs.getString("${appWidgetId}_device_type", null)
            val deviceLocation = prefs.getString("${appWidgetId}_device_location", null)
            return listOf(deviceId, userAuth, deviceType, deviceLocation).also { println(it.toString()) }
        }
    }
}