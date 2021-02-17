package com.eit.brnnda.room_database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eit.brnnda.dataclass.CartData
@Database(entities = [CartData::class], version = 1)
abstract class RoomDatabaseAbstract : RoomDatabase() {


    abstract val getCartDao: DaoInterface
    companion object {
        @Volatile
        private var INSTANCE: RoomDatabaseAbstract? = null
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also {
                INSTANCE = it
            }
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext,
                RoomDatabaseAbstract::class.java,
                "essentialbrnnda.db"
        ).build()

}
}