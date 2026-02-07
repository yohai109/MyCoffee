package com.yohai.mycoffee.database

import androidx.room.Room
import androidx.room.RoomDatabase

class TestDatabaseFactory : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        return Room.databaseBuilder<CoffeeDatabase>(
            name = ":memory:"
        )
    }
}

fun initTestDatabase() {
    initDatabase(TestDatabaseFactory())
}
