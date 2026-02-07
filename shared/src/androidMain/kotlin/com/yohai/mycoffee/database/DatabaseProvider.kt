package com.yohai.mycoffee.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class AndroidDatabaseFactory(private val context: Context) : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        val dbFile = context.getDatabasePath("coffee.db")
        return Room.databaseBuilder<CoffeeDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
    }
}