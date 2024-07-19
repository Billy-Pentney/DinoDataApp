package com.bp.dinodata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bp.dinodata.db.dao.ColorDao
import com.bp.dinodata.db.dao.GenusDao
import com.bp.dinodata.db.entities.ColorEntity
import com.bp.dinodata.db.entities.GenusEntity

@Database(entities = [GenusEntity::class, ColorEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun genusDao(): GenusDao
    abstract fun colorDao(): ColorDao


    companion object {
        fun prepopulateDatabase(appDatabase: AppDatabase) {

        }
    }


}
