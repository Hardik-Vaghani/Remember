package com.hardik.remember.db

import com.hardik.remember.models.SpellingResponseItem
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hardik.remember.models.BlogResponseItem

@Database(
    entities = [SpellingResponseItem::class,BlogResponseItem::class],
    version = 1,
    exportSchema = false
)
abstract class DBInstance : RoomDatabase() {

    abstract fun getDBDaoSpelling(): DaoSpelling
    abstract fun getDBDaoContent(): DaoContent

    companion object {
        @Volatile
        private var instance: DBInstance? = null
        private val LOCK = Any()//single instance

        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                instance ?: createDatabase(context).also {
                    instance = it
                }
            }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DBInstance::class.java,
                "database.db"
            ).build()
    }
}
//this is also singleton database