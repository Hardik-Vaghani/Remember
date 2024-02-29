package com.hardik.remember.ui.spelling_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.hardik.remember.R


open class SpellingAppWidget : AppWidgetProvider() {
    companion object {
        const val MyOnClick = "myOnClickTag"
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            Log.e("TAG", "onUpdate: ${context.packageName}")
            val pendingIntent = getPendingSelfIntent(context, MyOnClick)
            updateAppWidget(context, appWidgetManager, appWidgetId,pendingIntent,appWidgetIds)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    override fun onReceive(context: Context, intent: Intent) {
//        Log.e("TAG", "onReceive:")

        if (MyOnClick == intent.action) {
            // Retrieve data from the intent
            val getIntentValue = intent.getStringExtra("word")
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

//            Log.e("TAG", "onReceive: MyOnClick WidgetId:$appWidgetId, Value:$getIntentValue")
//            Toast.makeText(context, "Clicked position: $appWidgetId", Toast.LENGTH_SHORT).show()

            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appWidget_list_view)
//            val cn = ComponentName(context, SpellingAppWidget::class.java)
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.appWidget_list_view)
        }

        super.onReceive(context, intent)
    }
    private fun getPendingSelfIntent(context: Context?, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    pendingIntent: PendingIntent,
    appWidgetIds:IntArray
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.spelling_app_widget)
    // Set background for the root view
//    views.setInt(R.id.spelling_app_widget, "setBackgroundResource", R.drawable.widget_preview)
//    views.setInt(R.id.spelling_app_widget, "setBackgroundResource", 0)
    views.setInt(R.id.spelling_app_widget, "setBackgroundResource", android.R.color.background_light)

    // adapter on the stackView intent
    val serviceIntent = Intent(context, WidgetService::class.java)
    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
    views.setRemoteAdapter(R.id.appWidget_list_view, serviceIntent)
    views.setEmptyView(R.id.appWidget_list_view,R.id.appwidget_empty_view)

    // stackView template clicking intent
//    val clickIntent = Intent(context, SpellingAppWidget::class.java)
//    clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//    clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//    val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//    views.setPendingIntentTemplate(R.id.appWidget_list_view, clickPendingIntent)
    views.setPendingIntentTemplate(R.id.appWidget_list_view, pendingIntent)


    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appWidget_list_view)
}
