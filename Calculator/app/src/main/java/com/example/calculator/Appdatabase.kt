package com.example.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.calculator.Dao.HistoryDao
import com.example.calculator.model.History

@Database(entities = [History::class], version = 1)
abstract class Appdatabase : RoomDatabase() {
    abstract fun historydao(): HistoryDao

}