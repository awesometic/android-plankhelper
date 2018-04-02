package kr.kro.awesometic.plankhelper.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "plank")
data class Plank @JvmOverloads constructor(
        @PrimaryKey @ColumnInfo(name = "entry_id") var entryId: String = UUID.randomUUID().toString(),
        @ColumnInfo(name = "datetime") var datetime: Int,
        @ColumnInfo(name = "duration") var duration: Int,
        @ColumnInfo(name = "type") var type: Int,
        var laps: List<Lap>?
)