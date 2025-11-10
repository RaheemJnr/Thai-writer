package com.rjnr.thaiwrter.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rjnr.thaiwrter.data.local.entities.ThaiCharacterEntity
import com.rjnr.thaiwrter.data.local.entities.UserProgressEntity
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import com.rjnr.thaiwrter.data.models.UserProgress

//@Database(
//    entities = [ThaiCharacterEntity::class, UserProgressEntity::class],
//    version = 1
//)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun thaiCharacterDao(): ThaiCharacterDao
//    abstract fun userProgressDao(): UserProgressDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "thai_language_db"
//                )
//                    .createFromAsset("database/initial_characters.db")  // We'll create this later
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

@Database(
    entities = [ThaiCharacterEntity::class, UserProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun thaiCharacterDao(): ThaiCharacterDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "thai_language_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}