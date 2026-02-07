package com.yohai.mycoffee.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

class IosDatabaseFactory : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        val dbFilePath = NSHomeDirectory() + "/coffee.db"
        return Room.databaseBuilder<CoffeeDatabase>(
            name = dbFilePath,
            factory =  { CoffeeDatabase::class.instantiateImpl() }
        )
    }
}