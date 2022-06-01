package studio.sanguine.loanshark2.ui.contacts

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.sanguine.loanshark2.data.Contact
import studio.sanguine.loanshark2.data.ContactRecordFull
import studio.sanguine.loanshark2.data.DebtRecordFull
import studio.sanguine.loanshark2.data.Repo

class ContactsViewModel(context: Context) : ViewModel() {
    private val repo : Repo
    val data : LiveData<List<ContactRecordFull>>?
    var isDescending = false
    init {
        repo = Repo(context)
        data = repo.searchContactRecords("%%", isDescending, 0, 10)
    }

    fun getAllContacts() : LiveData<List<ContactRecordFull>>?{
        return repo.getAllContacts()
    }

    fun deleteContact(contact: Contact){
        repo.deleteContact(contact)
    }

    fun searchRecords(param: String, descending: Boolean, offset: Int, pageSize: Int): LiveData<List<ContactRecordFull>>? {
        return repo.searchContactRecords(param, descending, offset, pageSize)
    }
}