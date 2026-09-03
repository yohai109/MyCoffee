---
name: database-entity
description: Prescriptive workflow for adding or changing Room entities, DAOs, converters, and migrations.
---

# Database / Entity

You must follow this workflow whenever you change Room-backed data structures in MyCoffee.

## Rules You Must Follow

- You must keep database code in `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/`.
- You must add `@TypeConverter` methods for every new enum or non-primitive persisted type.
- You must increment the Room database version for every schema change.
- You must add and register a migration for every schema change unless the change is explicitly development-only.
- You must never change the schema without verification.

## 1. Inspect the Existing Schema Surface

Start by reading:

- `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/CoffeeDatabase.kt`
- `shared/src/commonMain/kotlin/com/yohai/mycoffee/database/DatabaseProvider.kt`
- `shared/src/{androidMain,iosMain,jvmMain}/kotlin/com/yohai/mycoffee/database/DatabaseProvider.kt`

### Verification

- Verify you know where the entity, DAO, converters, database annotation, and migrations live.
- Verify whether the change is a schema change or only a query/API change.

## 2. Add or Update the Entity and DAO

You must define entities with `@Entity` and DAOs with `@Dao`.

Required DAO patterns:

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

### Verification

- Verify the entity uses a primary key.
- Verify reactive reads return `Flow<...>` when the UI needs live updates.
- Verify DAO method names and types match existing conventions.

## 3. Add Required Type Converters

If you add a new enum or persisted complex type, you must update the `Converters` class in `CoffeeDatabase.kt`.

```kotlin
@TypeConverter
fun fromNewState(value: NewEntityState?): String? = value?.name

@TypeConverter
fun toNewState(value: String?): NewEntityState? = value?.let(NewEntityState::valueOf)
```

### Verification

- Verify every persisted custom type has a matching read and write converter.
- Verify converter names are consistent and unambiguous.

## 4. Update the Database Annotation and Migration

For every schema change, you must:

1. Add the entity to `@Database(...)` when needed.
2. Increment the version number.
3. Add a `Migration` object.
4. Register the migration in each platform database builder.

```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS NewEntity (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, value INTEGER NOT NULL)")
    }
}
```

### Verification

- Verify the version number changed exactly once.
- Verify the migration start and end versions match the version bump.
- Verify every platform builder registers the migration.

## 5. Wire the Database Change Into Usage Sites

If UI or server code depends on the new DAO/entity, you must update those call sites immediately. Never leave dead or unreachable data paths.

### Verification

- Verify every new DAO accessor is used or intentionally exposed for future use.
- Verify all callers handle nullable values safely.

## 6. Run Verification Commands

Run the smallest relevant commands first, then broader validation.

```bash
./gradlew :shared:allTests
./gradlew test
```

### Verification

- Verify the shared module compiles and tests pass.
- Verify `./gradlew test` is pass or documented as a pre-existing failure.

## Related Skills

- `.agent/skills/add-new-screen/SKILL.md` — use when the entity change is part of a new screen.
- `.agent/skills/write-tests/SKILL.md` — use for DAO, migration, and UI coverage expectations.


## User Request

$ARGUMENTS
