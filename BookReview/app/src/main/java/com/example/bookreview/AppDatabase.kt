package com.example.bookreview

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bookreview.dao.HistoryDao
import com.example.bookreview.dao.ReviewDao
import com.example.bookreview.model.History
import com.example.bookreview.model.Review

//Room data 배이스  - 데이터베이스는 앱에 저장되어 있는 로컬 데이터에 대한 액세스 포인트를 제공해주는 역할
@Database(entities = [History::class, Review::class], version = 2)
abstract class AppDatabase:RoomDatabase() {
    abstract fun historyDao() : HistoryDao
    abstract fun reviewDao(): ReviewDao
}


//fun getAppDatabase(context: Context): AppDatabase {
//
//        val migration_1_2 = object : Migration(1,2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
//            }
//
//        }
//
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "BookSearchDB"
//        )
//            .build()
//    }