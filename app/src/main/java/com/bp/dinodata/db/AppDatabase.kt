package com.bp.dinodata.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.bp.dinodata.db.dao.ColorDao
import com.bp.dinodata.db.dao.GenusDao
import com.bp.dinodata.db.entities.ColorEntity
import com.bp.dinodata.db.entities.GenusEntity

@Database(
    entities = [GenusEntity::class, ColorEntity::class],
    version = 5,
    autoMigrations = [AutoMigration(4,5)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun genusDao(): GenusDao
    abstract fun colorDao(): ColorDao
}
