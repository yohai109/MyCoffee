package com.yohai.mycoffee.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Entity
data class CoffeeStock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val roaster: String,
    val roastDate: LocalDate,
    val openDate: LocalDate?,
    val finishDate: LocalDate?,
    val state: CoffeeState,
    val size: Double
)

enum class CoffeeState {
    NEW, OPEN, FINISHED
}

enum class BrewMethod {
    POUR_OVER, ESPRESSO, FRENCH_PRESS, AEROPRESS, MOKA_POT, COLD_BREW, DRIP, OTHER
}

@Entity
data class BrewRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coffeeStockId: Long,
    val date: LocalDate,
    val method: BrewMethod,
    val dose: Double,
    val brewTime: Int, // in seconds
    val yield: Double?, // in grams
    val notes: String?
)

@Dao
interface BrewDao {
    @Query("SELECT * FROM BrewRecord ORDER BY date DESC")
    fun getAllBrews(): Flow<List<BrewRecord>>

    @Query("SELECT * FROM BrewRecord WHERE coffeeStockId = :coffeeStockId ORDER BY date DESC")
    fun getBrewsForCoffee(coffeeStockId: Long): Flow<List<BrewRecord>>

    @Insert
    suspend fun insertBrew(brew: BrewRecord)

    @Update
    suspend fun updateBrew(brew: BrewRecord)

    @Delete
    suspend fun deleteBrew(brew: BrewRecord)
}

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM CoffeeStock")
    fun getAllStock(): Flow<List<CoffeeStock>>

    @Insert
    suspend fun insertStock(stock: CoffeeStock)

    @Update
    suspend fun updateStock(stock: CoffeeStock)

    @Delete
    suspend fun deleteStock(stock: CoffeeStock)
}

@Database(entities = [CoffeeStock::class, BrewRecord::class], version = 1)
@TypeConverters(Converters::class)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao
    abstract fun brewDao(): BrewDao
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromBrewMethod(value: BrewMethod?): String? {
        return value?.name
    }

    @TypeConverter
    fun toBrewMethod(value: String?): BrewMethod? {
        return value?.let { BrewMethod.valueOf(it) }
    }
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<CoffeeDatabase>
): CoffeeDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .build()
}