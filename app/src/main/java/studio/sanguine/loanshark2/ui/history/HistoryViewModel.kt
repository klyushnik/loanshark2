package studio.sanguine.loanshark2.ui.history

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.sanguine.loanshark2.data.Contact
import studio.sanguine.loanshark2.data.ContactRecordFull
import studio.sanguine.loanshark2.data.History
import studio.sanguine.loanshark2.data.Repo

class HistoryViewModel(context: Context) : ViewModel() {

    private val repo : Repo
    val data : LiveData<List<History>>?
    var isDescending = false
    init {
        repo = Repo(context)
        data = repo.searchHistory("%%", isDescending, 0, 10)
        //data = repo.getAllHistory()
    }

    fun deleteHistory(history: History){
        repo.deleteHistoryRecord(history)
    }

    fun searchHistory(param: String, descending: Boolean, offset: Int, pageSize: Int): LiveData<List<History>>? {
        return repo.searchHistory(param, descending, offset, pageSize)
    }
}