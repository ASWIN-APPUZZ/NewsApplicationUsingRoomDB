package com.android.news1application.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.news1application.model.Article

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
 )

@TypeConverters(Converters::class)

abstract class NewsDatabase : RoomDatabase() {
    abstract fun getArticlesDAO(): ArticlesDAO

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: getDatabase(context).also { INSTANCE = it }
        }

        private fun getDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NewsDatabase::class.java,
            "article_database.db"
        ).build()
    }
}