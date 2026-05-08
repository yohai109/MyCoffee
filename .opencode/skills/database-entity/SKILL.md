---
name: database-entity
description: Guide for working with Room database entities, DAOs, type converters, and database usage in composables.
---
# Database/Entity

This skill guides you through working with Room database entities and DAOs in the shared module.

## Entity Structure

All entities and DAOs live in `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/CoffeeDatabase.kt`. You can either add new entities to this file or create separate files in the same package.

### Existing Entities

```kotlin
@Entity
data class CoffeeStock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val roaster: String,
    val roastDate: LocalDate,
    val openDate: LocalDate?,
    val finishDate: LocalDate?,
    val state: CoffeeState,
    val size: Double,
    val remainingWeight: Double? = null,
    val rating: Int? = null,
    val origin: String? = null,
    val process: ProcessMethod? = null,
    val tastingNotes: String? = null
)

@Entity
data class BrewRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coffeeStockId: Long,
    val date: LocalDate,
    val method: BrewMethod,
    val dose: Double,
    val brewTime: Int,
    val yield: Double?,
    val notes: String?
)
```

### New Entity Pattern

```kotlin
@Entity
data class NewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val value: Int,
)
```

### Entity with Enums

Enums used by entities must have a `@TypeConverter` in the `Converters` class:

```kotlin
enum class NewEntityState {
    STATE_A, STATE_B, STATE_C
}

@Entity
data class NewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val state: NewEntityState,
)
```

## DAO Pattern

Existing DAOs use the following patterns. Follow these for new DAOs:

```kotlin
@Dao
interface NewEntityDao {
    @Query("SELECT * FROM NewEntity")
    fun getAll(): Flow<List<NewEntity>>

    @Query("SELECT * FROM NewEntity WHERE id = :id")
    suspend fun getById(id: Long): NewEntity?

    @Insert
    suspend fun insert(entity: NewEntity)

    @Update
    suspend fun update(entity: NewEntity)

    @Delete
    suspend fun delete(entity: NewEntity)
}
```

## Type Converters

The `Converters` class in `CoffeeDatabase.kt` handles both `kotlinx.datetime.LocalDate` and enum serialization. When adding a new enum entity field, add the corresponding converters:

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

    // Add for each enum used in entities:
    @TypeConverter
    fun fromNewState(value: NewEntityState?): String? {
        return value?.name
    }

    @TypeConverter
    fun toNewState(value: String?): NewEntityState? {
        return value?.let { NewEntityState.valueOf(it) }
    }
}
```

## Database Class

Update the `@Database` annotation when adding entities:

```kotlin
@Database(entities = [CoffeeStock::class, BrewRecord::class, NewEntity::class], version = 4)
@TypeConverters(Converters::class)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao
    abstract fun brewDao(): BrewDao
    abstract fun newEntityDao(): NewEntityDao
}
```

### ⚠️ Version Bumps & Migrations

Each schema change requires:
1. **Incrementing** the `version` number in `@Database`
2. **Adding a migration** — without one, the app will crash on startup with a `MigrationNotFoundException`

Simple migration example (adds a table):

```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS NewEntity (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, value INTEGER NOT NULL)")
    }
}
```

Pass migrations to the builder in each platform's `DatabaseFactory`:

```kotlin
// In DatabaseFactory.createBuilder():
.addMigrations(MIGRATION_3_4)
```

**Fallback for development**: Use `fallbackToDestructiveMigration()` to recreate the database (all data is lost).

## Usage in Composables

```kotlin
val database = remember { getDatabase() }
val dao = database.newEntityDao()

// Reactive data
val entities: List<NewEntity> by dao.getAll().collectAsState(initial = emptyList())

// Write operations
scope.launch {
    dao.insert(NewEntity(name = "Test", value = 42))
}
```

## Adding a New Entity — Checklist

1. Add entity class and enum (if needed) to `CoffeeDatabase.kt` or a new file in the same package
2. Add DAO interface
3. Add `@TypeConverter` methods to `Converters` for any new enums
4. Update `@Database` annotation with new entity and increment version
5. Write a `Migration` class and register it in platform `DatabaseFactory` builders
6. Run `./gradlew :shared:allTests` to verify the build compiles

**Note**: Room uses KSP for annotation processing. If KSP fails, check that the entity and DAO are properly annotated. The build config already has KSP configured in the shared module.

## Key Files

- Entity definitions and DAOs: `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/CoffeeDatabase.kt`
- Database provider: `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/DatabaseProvider.kt`
- Platform-specific database builders: `shared/src/{androidMain,iosMain,jvmMain}/kotlin/com/yohai/mycoffee/database/DatabaseProvider.kt`

## Related Skills

- [Add New Screen](../add-new-screen/SKILL.md) — for integrating database entities into new screens
- [Write Tests](../write-tests/SKILL.md) — for testing database operations with in-memory Room database
