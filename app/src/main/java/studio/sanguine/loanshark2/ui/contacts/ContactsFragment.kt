package studio.sanguine.loanshark2.ui.contacts

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import studio.sanguine.loanshark2.*
import studio.sanguine.loanshark2.adapters.ContactRecordAdapter
import studio.sanguine.loanshark2.data.Contact
import studio.sanguine.loanshark2.data.ContactRecordFull
import studio.sanguine.loanshark2.databinding.FragmentContactsBinding
import studio.sanguine.loanshark2.ui.TransactionsFragment

class ContactsFragment : Fragment(), FragmentOperations {

    private var _binding: FragmentContactsBinding? = null

    lateinit var contactList: ArrayList<ContactRecordFull>

    lateinit var adapter : ContactRecordAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var vm: ContactsViewModel
    var searchQuery = "%%"
    var offset = 0
    var pageSize = 10
    var isLoading = false
    var sortDesc = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)

        vm = ContactsViewModel(requireContext())

        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        contactList = ArrayList()

        vm.data?.observe(viewLifecycleOwner){
            contactList -> getStudents(contactList)
        }

        val recyclerView = binding.contactsRecyclerView
        adapter = ContactRecordAdapter(contactList ,placeCall, editPerson, showTransactions)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        recyclerView.itemAnimator = DefaultItemAnimator()
        val swipeHelper = ContactSwipeHelper(vm, adapter, requireContext())
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        binding.conNextButton.setOnClickListener { nextPage() }
        binding.conPreviousButton.setOnClickListener { prevPage() }
        binding.contactsClearSearchButton.setOnClickListener { hideSearchBar() }
        binding.newItemButton.setOnClickListener { newItem() }

        binding.contactsSearchEdittext.addTextChangedListener {
            searchQuery = "%${binding.contactsSearchEdittext.text.toString()}%"

            search()
        }


        return root
    }

    val showTransactions = fun(id: Int){
        val fragment = TransactionsFragment.newInstance(id)
        fragment.show((context as MainActivity).getSupportFragmentManager(), "")
    }

    override fun newItem() {
        val intent = Intent(requireContext(), AddModifyPerson::class.java)
        startActivity(intent)
    }

    fun getStudents(list: List<ContactRecordFull>){
        contactList.clear()
        contactList.addAll(list)
        adapter.notifyDataSetChanged()
        if(adapter.itemCount > 0){
            binding.noItems.visibility = View.GONE
        } else {
            binding.noItems.visibility = View.VISIBLE
            binding.contactsProgressBar.visibility = View.GONE
            binding.newItemButton.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.removeItem(R.id.menu_newRecord)
    }

    val placeCall = fun (param: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Do you want to call $param?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + param)
                startActivity(dialIntent)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            .setNeutralButton("SMS", DialogInterface.OnClickListener { dialogInterface, i ->
                val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", param, null))
                smsIntent.putExtra("sms_body", "Remember me?")
                startActivity(smsIntent)
            })

        val dialog = dialogBuilder.create()
        dialog.setTitle("Call number")
        dialog.show()

    }

    val editPerson = fun(contact:Contact) {
        val intent = Intent(context, AddModifyPerson::class.java)
        intent.putExtra("contact", contact)
        intent.putExtra("editMode", true)
        startActivity(intent)
    }

    fun hideSearchBar() {
        binding.contactsSearchLayout.visibility = View.GONE
        binding.contactsSearchEdittext.setText("")
        adapter.notifyDataSetChanged()
    }

    override fun search() {
        isLoading = true
        vm.searchRecords(searchQuery, sortDesc, offset, pageSize)?.observe(
            viewLifecycleOwner
        ) { list ->
            list.let {
                adapter.data = it
                adapter.notifyDataSetChanged()
                if(adapter.itemCount > 0){
                    binding.noItems.visibility = View.GONE
                } else {
                    binding.noItems.visibility = View.VISIBLE
                    binding.contactsProgressBar.visibility = View.GONE
                    binding.newItemButton.visibility = View.VISIBLE
                }
                isLoading = false
            }
        }
    }

    override fun sortAsc() {
        sortDesc = false
        search()
    }

    override fun sortDesc() {
        sortDesc = true
        search()
    }

    override fun nextPage() {
        if(adapter.itemCount == 10 && !isLoading)
            offset += 10
        search()
    }

    override fun prevPage() {
        offset -= 10
        if(offset < 0) offset = 0
        search()
    }

    override fun searchBar() {
        if(binding.contactsSearchLayout.visibility == View.VISIBLE){
            binding.contactsSearchLayout.visibility = View.GONE
            binding.contactsSearchEdittext.setText("")
        }else{
            binding.contactsSearchLayout.visibility = View.VISIBLE
        }
    }


}

open class ContactSwipeHelper(val vm: ContactsViewModel,
                       val adapter: ContactRecordAdapter,
                       val context: Context
) : ItemTouchHelper.Callback(){
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = adapter.getItem(viewHolder.adapterPosition)

        val dialogBuilder = AlertDialog.Builder(context)
        val message = "Are you sure you want to delete ${item.firstName} ${item.lastName}?"
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                vm.deleteContact(item)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                adapter.notifyDataSetChanged()
            })
        val dialog = dialogBuilder.create()
        dialog.setTitle("Delete contact")
        dialog.show()
    }
}