package studio.sanguine.loanshark2.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MainDao {
    //contacts operations

    @Query("SELECT * FROM ContactRecordFull")
    fun selectAllContacts() : LiveData<List<ContactRecordFull>>

    @Query("SELECT * FROM Contact")
    fun selectDbContacts() : LiveData<List<Contact>>

    @Insert
    fun insertContact(contact: Contact)

    @Delete
    fun deleteContact(contact: Contact)

    @Update
    fun updateContact(contact: Contact)

    //DebtRecordDb operations

    @Query("SELECT * FROM DebtRecordFull")
    fun selectAllDebtRecords() : LiveData<List<DebtRecordFull>>

    @Query("SELECT * FROM DebtRecordFull WHERE firstName LIKE :param OR lastName LIKE :param")
    fun searchDebtRecords(param: String) : LiveData<List<DebtRecordFull>>

    @Query("SELECT * FROM DebtRecordFull WHERE debtIsCreditor = :isCreditor AND (firstName LIKE :param OR lastName LIKE :param) ORDER BY firstName LIMIT :pageSize OFFSET :offset")
    fun searchDebtRecordsAsc(param: String, offset: Int, pageSize: Int, isCreditor: Boolean) : LiveData<List<DebtRecordFull>>

    @Query("SELECT * FROM DebtRecordFull WHERE debtIsCreditor = :isCreditor AND (firstName LIKE :param OR lastName LIKE :param) ORDER BY firstName DESC LIMIT :pageSize OFFSET :offset")
    fun searchDebtRecordsDesc(param: String, offset: Int, pageSize: Int, isCreditor: Boolean) : LiveData<List<DebtRecordFull>>

    @Query("SELECT * FROM ContactRecordFull WHERE firstName LIKE :param OR lastName LIKE :param ORDER BY firstName LIMIT :pageSize OFFSET :offset")
    fun searchContactRecordsAsc(param: String, offset: Int, pageSize: Int) : LiveData<List<ContactRecordFull>>

    @Query("SELECT * FROM ContactRecordFull WHERE firstName LIKE :param OR lastName LIKE :param ORDER BY firstName DESC LIMIT :pageSize OFFSET :offset")
    fun searchContactRecordsDesc(param: String, offset: Int, pageSize: Int) : LiveData<List<ContactRecordFull>>

    @Delete
    fun deleteDebtRecord(debtRecordDb: DebtRecordDb)

    @Update
    fun updateDebtRecord(debtRecordDb: DebtRecordDb)

    @Insert
    fun insertDebtRecord(debtRecordDb: DebtRecordDb)


    @Insert
    fun insertHistoryRecord(history: History)

    @Delete
    fun deleteHistoryRecord(history: History)

    @Query("SELECT * FROM History")
    fun getAllHistory(): LiveData<List<History>>

    @Query("SELECT * FROM History WHERE contactId = :contactId")
    fun getHistoryById(contactId: Int): LiveData<List<History>>

    @Query("SELECT * FROM History WHERE name LIKE :param ORDER BY name LIMIT :pageSize OFFSET :offset")
    fun searchHistoryAsc(param: String, offset: Int, pageSize: Int) : LiveData<List<History>>

    @Query("SELECT * FROM History WHERE name LIKE :param ORDER BY name DESC LIMIT :pageSize OFFSET :offset")
    fun searchHistoryDesc(param: String, offset: Int, pageSize: Int) : LiveData<List<History>>
}