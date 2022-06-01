package studio.sanguine.loanshark2.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repo (context: Context){
    var db: MainDao? = AppDatabase.getInstance(context)?.mainDao()!!

    fun getAllContacts() : LiveData<List<ContactRecordFull>>?{
        return db?.selectAllContacts()
    }

    fun searchContactRecords(param: String, descending: Boolean, offset: Int, pageSize: Int) : LiveData<List<ContactRecordFull>>?{
        if(!descending)
            return db?.searchContactRecordsAsc(param, offset, pageSize)
        else
            return db?.searchContactRecordsDesc(param, offset, pageSize)
    }

    fun getDbContacts() : LiveData<List<Contact>>?{
        return db?.selectDbContacts()
    }

    fun insertContact(contact: Contact){
        CoroutineScope(Dispatchers.IO).launch {
            db?.insertContact(contact)
        }
    }

    fun updateContact(contact: Contact){
        CoroutineScope(Dispatchers.IO).launch {
            db?.updateContact(contact)
        }
    }

    fun deleteContact(contact: Contact){
        CoroutineScope(Dispatchers.IO).launch {
            db?.deleteContact(contact)
        }
    }

    //debt records
    fun getAllDebtRecords() : LiveData<List<DebtRecordFull>>?{
        return db?.selectAllDebtRecords()
    }

    fun deleteDebtRecord(debtRecordDb: DebtRecordDb) {
        CoroutineScope(Dispatchers.IO).launch {
            db?.deleteDebtRecord(debtRecordDb)
        }
    }

    fun insertDebtRecord(debtRecordDb: DebtRecordDb){
        CoroutineScope(Dispatchers.IO).launch {
            db?.insertDebtRecord(debtRecordDb)
        }
    }

    fun updateDebtRecord(debtRecordDb: DebtRecordDb){
        CoroutineScope(Dispatchers.IO).launch {
            db?.updateDebtRecord(debtRecordDb)
        }
    }

    fun searchDebtRecords(param: String, descending: Boolean, offset: Int, pageSize: Int, isCreditor: Boolean) : LiveData<List<DebtRecordFull>>?{
        if(!descending)
            return db?.searchDebtRecordsAsc(param, offset, pageSize, isCreditor)
        else
            return db?.searchDebtRecordsDesc(param, offset, pageSize, isCreditor)
    }

    fun insertHistoryRecord(history: History){
        CoroutineScope(Dispatchers.IO).launch {
            db?.insertHistoryRecord(history)
        }
    }

    fun deleteHistoryRecord(history: History){
        CoroutineScope(Dispatchers.IO).launch {
            db?.deleteHistoryRecord(history)
        }
    }

    fun searchHistory(param: String, descending: Boolean, offset: Int, pageSize: Int) : LiveData<List<History>>?{
        if(!descending)
            return db?.searchHistoryAsc(param, offset, pageSize)
        else
            return db?.searchHistoryDesc(param, offset, pageSize)
    }

    fun getHistoryById(contactId: Int) : LiveData<List<History>>?{
        return db?.getHistoryById(contactId)
    }

    fun getAllHistory() : LiveData<List<History>>?{
        return db?.getAllHistory()
    }
}