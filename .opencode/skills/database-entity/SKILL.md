---
name: database-entity
description: Guide for working with Room database entities, DAOs, type converters, and database usage in composables.
---

# Database/Entity

This skill guides you through working with Room database entities and DAOs in the shared module.

## Entity Structure

### Basic Entity Pattern

```kotlin
@Entity
data class EntityName(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val field1: String,
    val field2: Int,
    val field3: Double
)
```

### Entity with Enums

```kotlin
enum class EntityState {
    STATE_ONE, STATE_TWO, STATE_THREE
}

@Entity
data class EntityWithState(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val state: EntityState
)
```

## DAO Pattern

```kotlin
@Dao
interface EntityDao {
    @Query("SELECT * FROM EntityName")
    fun getAllEntities(): Flow<List<EntityName>>
    
    @Query("SELECT * FROM EntityName WHERE id = :id")
    suspend fun getEntityById(id: Long): EntityName?
    
    @Query("SELECT * FROM EntityName WHERE state = :state")
    fun getEntitiesByState(state: EntityState): Flow<List<EntityName>>
    
    @Insert
    suspend fun insertEntity(entity: EntityName)
    
    @Update
    suspend fun updateEntity(entity: EntityName)
    
    @Delete
    suspend fun deleteEntity(entity: EntityName)
}
```

## Type Converters for kotlinx.datetime

The project uses `kotlinx.datetime.LocalDate`. The `Converters` class handles conversion:

```kotlin
class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}
```

## Database Class

```kotlin
@Database(entities = [EntityName::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entityDao(): EntityDao
}
```

## Usage in Composables

```kotlin
val database = remember { getDatabase() }
val dao = database.entityDao()

// Reactive data
val entities: List<EntityName> by dao.getAllEntities().collectAsState(initial = emptyList())

// Write operations
scope.launch {
    dao.insertEntity(EntityName(name = "Test"))
}
```

## Adding a New Entity

1. Add entity class to `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/`
2. Add DAO interface
3. Update `@Database` annotation with new entity
4. Increment database version if schema changed
5. Add migration if needed

## Key Files

- Entity definitions: `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/CoffeeDatabase.kt`
- Database provider: `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/DatabaseProvider.kt`
