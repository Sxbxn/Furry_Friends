package com.k_bootcamp.furry_friends.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.k_bootcamp.furry_friends.data.db.dao.RoutineDao
import com.k_bootcamp.furry_friends.model.animal.Routine


@Database(
    entities = [Routine::class],
    version = 1,
    exportSchema = false
)
abstract class RoutineDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
}