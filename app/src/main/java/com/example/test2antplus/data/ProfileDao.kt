package com.example.test2antplus.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.test2antplus.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * from profile")
    fun getAll(): List<Profile>

    @Query("Select * from profile where id = :profileId")
    fun getProfile(profileId: Int): Profile

    @Insert
    fun addProfile(profile: Profile)

    @Delete
    fun deleteProfile(profile: Profile)
}