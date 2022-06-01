package studio.sanguine.loanshark2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [DebtRecordDb::class,Contact::class,History::class],
    views = [DebtRecordFull::class,ContactRecordFull::class],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mainDao() : MainDao

    companion object{
        var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase? {
            if(INSTANCE == null){
                //6
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "shark.db").build()
                }
            }

            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }

    }
}