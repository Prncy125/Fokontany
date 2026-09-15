package com.example.fokontany.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fokontany.data.local.dao.DistributionAideDao
import com.example.fokontany.data.local.dao.FoyerDao
import com.example.fokontany.data.local.dao.HabitantDao
import com.example.fokontany.data.local.dao.ProgrammeAideDao
import com.example.fokontany.data.local.entity.DistributionAideEntity
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.data.local.entity.ProgrammeAideEntity

@Database(
    entities = [
        FoyerEntity::class,
        HabitantEntity::class,
        ProgrammeAideEntity::class,
        DistributionAideEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun foyerDao(): FoyerDao

    abstract fun habitantDao(): HabitantDao

    abstract fun programmeAideDao(): ProgrammeAideDao

    abstract fun distributionAideDao(): DistributionAideDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE habitants
                    ADD COLUMN codePaysTelephone TEXT NOT NULL DEFAULT '+230'
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fokontany_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}