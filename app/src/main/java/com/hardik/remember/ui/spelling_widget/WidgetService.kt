package com.hardik.remember.ui.spelling_widget

import android.annotation.SuppressLint
import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.hardik.remember.R
import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.SpellingResponseItem
import com.hardik.remember.repository.SpellingRepositoryInstance
import com.hardik.remember.ui.spelling_widget.SpellingAppWidget.Companion.MyOnClick


class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return AppWidgetItemFactory(applicationContext, intent)
    }

    internal class AppWidgetItemFactory(private val context: Context, intent: Intent) :
        RemoteViewsFactory {

        private val appWidgetId: Int
        private val repositoryInstance = SpellingRepositoryInstance(DBInstance(context))
//        private val spellingAppWidgetViewModel: SpellingAppWidgetViewModel = ViewModelProvider(context as ViewModelStoreOwner, SpellingAppWidgetViewModelProviderFactory(context.applicationContext as Application, SpellingRepositoryInstance(DBInstance(context)))).get(SpellingAppWidgetViewModel::class.java)

        private lateinit var allSpellingItems: LiveData<List<SpellingResponseItem>>
        private var spellingItemList: List<SpellingResponseItem> = emptyList()

        init {
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        override fun onCreate() {
            //Connect to datasource
            allSpellingItems = repositoryInstance.getAllSpellings(isLike = true)
//            allSpellingItems = spellingAppWidgetViewModel.spellingDataList
            allSpellingItems.observeForever { list ->
                Log.e("TAG", "onCreate: listSize: ${list.size}")
                updateList(list)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, SpellingAppWidget::class.java))
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appWidget_list_view)
            }
//            SystemClock.sleep(3000)
        }

        override fun onDataSetChanged() {
            // Refresh data source || Refresh the widget when the data changes
             Log.e("TAG", "onDataSetChanged: ")
//            val date = Date()
//            val timeFormatted = DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
//            exampleData = arrayOf("one\n$timeFormatted", "two\n$timeFormatted", "three\n$timeFormatted")
        }

        override fun onDestroy() {
            //close data source connection
        }

        override fun getCount(): Int {
            return spellingItemList.size
        }

        @SuppressLint("ObsoleteSdkInt")
        override fun getViewAt(position: Int): RemoteViews {
            Log.e("TAG", "getViewAt: $position")
            return RemoteViews(context.packageName, R.layout.spelling_item).apply {
                val pronunciation = if (spellingItemList[position].pronounce.isNotEmpty()){" <font color=\"grey\"><small>(${spellingItemList[position].pronounce})</small></font><br>"}else{""}
                val meaning = if (spellingItemList[position].meaning.isNotEmpty()){"<font color=\"grey\"><small>${spellingItemList[position].meaning}</small></font>"}else{""}
                val spellingWordHtmlString = spellingItemList[position].word + pronunciation + meaning
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setTextViewText(R.id.tv_spelling, Html.fromHtml(spellingWordHtmlString, Html.FROM_HTML_MODE_LEGACY))
                } else {
                    @Suppress("DEPRECATION")
                    setTextViewText(R.id.tv_spelling, HtmlCompat.fromHtml(spellingWordHtmlString, HtmlCompat.FROM_HTML_MODE_LEGACY))
                }

                // Set click intent for the item
                val fillIntent = Intent()
                fillIntent.action = MyOnClick
                fillIntent.putExtra("word", spellingItemList[position].word)
                fillIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

                // Use setOnClickFillInIntent to associate the intent with the root view of your item layout
                setOnClickFillInIntent(R.id.spelling_item, fillIntent) // items root view id
            }
//                Log.e("TAG1", "getViewAt: ${spellingItemList[position]}", )

            /*val views = RemoteViews(context.packageName, R.layout.spelling_item)
//            views.setTextViewText(R.id.tv_spelling, spellingItemList[position].word)

            val pronunciation = if (spellingItemList[position].pronounce.isNotEmpty()){" <font color=\"grey\"><small>(${spellingItemList[position].pronounce})</small></font><br>"}else{""}
            val meaning = if (spellingItemList[position].meaning.isNotEmpty()){"<font color=\"grey\"><small>${spellingItemList[position].meaning}</small></font>"}else{""}

            val spellingWordHtmlString = spellingItemList[position].word + pronunciation + meaning

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                views.setTextViewText(R.id.tv_spelling, Html.fromHtml(spellingWordHtmlString, Html.FROM_HTML_MODE_LEGACY))
            } else {
                @Suppress("DEPRECATION")
                views.setTextViewText(R.id.tv_spelling, HtmlCompat.fromHtml(spellingWordHtmlString, HtmlCompat.FROM_HTML_MODE_LEGACY))
            }


            // Set click intent for the item
            val fillIntent = Intent()
            fillIntent.action = MyOnClick
            fillIntent.putExtra("word", spellingItemList[position].word)
            fillIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // Use setOnClickFillInIntent to associate the intent with the root view of your item layout
            views.setOnClickFillInIntent(R.id.spelling_item, fillIntent) // items root view id

            return views*/
        }

        override fun getLoadingView(): RemoteViews {
            // Create a loading view with a TextView displaying "Loading..."
            val loadingView = RemoteViews(context.packageName, R.layout.spelling_item)
            loadingView.setTextViewText(R.id.tv_spelling, "Loading...")

            return loadingView
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        private fun updateList(list: List<SpellingResponseItem>) {
            spellingItemList = list
        }
    }
}