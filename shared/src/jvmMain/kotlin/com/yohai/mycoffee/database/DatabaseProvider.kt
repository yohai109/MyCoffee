package com.yohai.mycoffee.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

class JvmDatabaseFactory : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        val dbFile = File(System.getProperty("user.home"), "coffee.db")
        return Room.databaseBuilder<CoffeeDatabase>(
            name = dbFile.absolutePath,
        )
    }
}