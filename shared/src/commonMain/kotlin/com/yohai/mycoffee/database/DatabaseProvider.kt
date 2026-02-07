package com.yohai.mycoffee.database

import androidx.room.RoomDatabase

interface DatabaseFactory {
    fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase>
}

private lateinit var factory: DatabaseFactory

fun initDatabase(databaseFactory: DatabaseFactory) {
    factory = databaseFactory
}

fun getDatabase(): CoffeeDatabase {
    return getRoomDatabase(factory.createBuilder())
}