package ru.fintech.devlife.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.fintech.devlife.App

@Database(entities = [PosterLocal::class], version = 1, exportSchema = false)
abstract class PosterDataBase: RoomDatabase() {

    abstract val posterDataDao: PosterDataDao

    companion object {
        val instance: PosterDataBase  by lazy {
            Room.inMemoryDatabaseBuilder(App.appContext, PosterDataBase::class.java)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}