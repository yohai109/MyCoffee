package com.yohai.mycoffee.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class AndroidDatabaseFactory(
    private val context: Context,
    private val databaseName: String = "coffee.db"
) : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        val dbFile = context.getDatabasePath(databaseName)
        return Room.databaseBuilder<CoffeeDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        )
    }
}
