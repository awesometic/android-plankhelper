package kr.kro.awesometic.plankhelper.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import java.util.UUID

@Entity(tableName = "lap", foreignKeys =
[ForeignKey(entity = Plank::class, parentColumns = ["entry_id"], childColumns = ["plank_id"], onDelete = CASCADE)])
data class Lap @JvmOverloads constructor(
        @PrimaryKey @ColumnInfo(name = "entry_id") var entryId: String = UUID.randomUUID().toString(),
        @ColumnInfo(name = "plank_id") var plankId: String,
        @ColumnInfo(name = "order_number") var orderNum: Int,
        @ColumnInfo(name = "passed_time") var passedTime: Int
)