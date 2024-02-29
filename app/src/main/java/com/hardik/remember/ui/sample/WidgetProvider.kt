package com.hardik.remember.ui.sample


import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.hardik.remember.R
import com.hardik.remember.ui.spelling_widget.WidgetService

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application).create(
            AppWidgetViewModel::class.java)

        for (appWidgetId in appWidgetIds) {

            // adapter on the stackView intent
            val serviceIntent = Intent(context, WidgetService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

            // stackView template clicking intent
            val clickIntent = Intent(context, WidgetProvider::class.java)
            clickIntent.action = ACTION_REFRESH
            val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE)

            val views = RemoteViews(context.packageName, R.layout.spelling_app_widget)
            views.setRemoteAdapter(R.id.appWidget_list_view, serviceIntent)
//            views.setEmptyView(R.id.appWidget_list_view, R.id.appwidget_empty_view)
            views.setPendingIntentTemplate(R.id.appWidget_list_view, clickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetId,
                R.id.appWidget_list_view
            )
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        // Handle configuration changes, e.g., resize
        // Update your RemoteViews here
        updateWidget(context, appWidgetManager, appWidgetId, newOptions)
        val views = RemoteViews(context.packageName, R.layout.spelling_app_widget)
        appWidgetManager.updateAppWidget(appWidgetId, views)
        appWidgetManager.notifyAppWidgetViewDataChanged(
            appWidgetId,
            R.id.appWidget_list_view
        )
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        // Retrieve new size information from newOptions
        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        // Update your widget layout based on new size
        // You may need to reload data or adjust views accordingly


        // Update the widget with the new RemoteViews
        val views = RemoteViews(context.packageName, appWidgetId)

        appWidgetManager.updateAppWidget(appWidgetId, views)
        appWidgetManager.notifyAppWidgetViewDataChanged(
            appWidgetId,
            R.id.appWidget_list_view
        )
    }

    override fun onEnabled(context: Context) {
        Toast.makeText(context, "onEnabled", Toast.LENGTH_SHORT).show()
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Toast.makeText(context, "onDisabled", Toast.LENGTH_SHORT).show()
        super.onDisabled(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Toast.makeText(context, "onDeleted", Toast.LENGTH_SHORT).show()
        super.onDeleted(context, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_REFRESH == intent.action) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            Toast.makeText(context, "Clicked position: $appWidgetId", Toast.LENGTH_SHORT).show()
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetId,
                R.id.appWidget_list_view
            )
        // update our collection view
        }
        super.onReceive(context, intent)
    }

    companion object {
        const val ACTION_REFRESH = "actionRefresh"
    }
}