package studio.sanguine.loanshark2.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import studio.sanguine.loanshark2.data.History
import studio.sanguine.loanshark2.data.Repo

class TransactionsViewModel(context: Context, val contactId: Int) : ViewModel() {
    private val repo : Repo
    val data : LiveData<List<History>>?
    var isDescending = false
    init {
        repo = Repo(context)
        data = repo.getHistoryById(contactId)
        //data = repo.getAllHistory()
    }

}