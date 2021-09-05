package ru.fintech.devlife.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "posters")
data class PosterLocal(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="index")
    val index: Int? = 0,

    @NotNull
    @ColumnInfo(name = "category")
    val category: String,

    @NotNull
    @ColumnInfo(name = "poster_id")
    val posterId: Int,

    @NotNull
    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name="gifURL")
    var posterUrl: String?,

    @ColumnInfo(name = "width")
    var posterWidth: Int?,

    @ColumnInfo(name = "height")
    var posterHeight: Int?,
)