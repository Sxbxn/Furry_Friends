package com.k_bootcamp.furry_friends.model.animal

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "routine")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val routineId: Int = 0,
    val session: String,
    val animal_id: Int,
    val routineName: String,
    val isOn: Boolean,
    val mon: Boolean = false,
    val tue: Boolean = false,
    val wed: Boolean = false,
    val thu: Boolean = false,
    val fri: Boolean = false,
    val sat: Boolean = false,
    val sun: Boolean = false,
    val time: String = "00:00"
) :Parcelable
