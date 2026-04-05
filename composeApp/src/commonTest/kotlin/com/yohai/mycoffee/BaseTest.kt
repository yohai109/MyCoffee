package com.yohai.mycoffee

import androidx.room.Room
import androidx.room.RoomDatabase
import com.yohai.mycoffee.database.CoffeeDatabase
import com.yohai.mycoffee.database.DatabaseFactory
import com.yohai.mycoffee.database.initDatabase
import kotlin.test.BeforeTest

class TestDatabaseFactory : DatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
        return Room.inMemoryDatabaseBuilder<CoffeeDatabase>()
    }
}

open class BaseTest {
    @BeforeTest
    fun setup() {
        initDatabase(TestDatabaseFactory())
    }
}
