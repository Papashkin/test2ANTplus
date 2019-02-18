package com.example.test2antplus.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import android.support.annotation.NonNull
import com.example.test2antplus.data.ProfilesDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(@NonNull private val context: Context) {

    @Provides
    @Singleton
    fun getContext() = context

    @Provides
    @Singleton
    fun getDatabase() = Room.databaseBuilder(
        context,
        ProfilesDatabase::class.java,
        "profiles"
    )
        .build()
}