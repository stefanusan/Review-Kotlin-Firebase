package com.lazday.moneykotlin.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lazday.moneykotlin.BaseActivity
import com.lazday.moneykotlin.adapter.TransactionAdapter
import com.lazday.moneykotlin.databinding.ActivityTransactionBinding
import com.lazday.moneykotlin.fragment.DateFragment
import com.lazday.moneykotlin.model.Transaction
import com.lazday.moneykotlin.preferences.PreferencesManager
import com.lazday.moneykotlin.util.stringToDate
import java.util.*


private const val TAG = "TransactionActivity"

class TransactionActivity : BaseActivity() {

    private val binding by lazy { ActivityTransactionBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }
    private lateinit var adapter: TransactionAdapter
    private val preference by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupList()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        listTransaction()
    }

    private fun setupList(){
        adapter = TransactionAdapter(arrayListOf(), object : TransactionAdapter.AdapterListener {
            override fun onClick(transaction: Transaction) {
                startActivity(
                    Intent(this@TransactionActivity, UpdateActivity::class.java)
                        .putExtra("id", transaction.id)
                )
            }

            override fun onLongClick(transaction: Transaction) {
                val alertDialog = AlertDialog.Builder(this@TransactionActivity)
                alertDialog.apply {
                    setTitle("Hapus")
                    setMessage("Hapus ${transaction.note} dari histori transaksi?")
                    setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Hapus") { dialog, _ ->
                        deleteTransaction(transaction.id!!)
                        dialog.dismiss()
                    }
                }
                alertDialog.show()
            }
        })
        binding.listTransaction.adapter = this.adapter
    }

    private fun setupListener(){
        binding.swipe.setOnRefreshListener {
            listTransaction()
            binding.textDate.text = "Menampilkan 50 transaksi terbaru"
        }
        binding.imageDate.setOnClickListener {
            DateFragment(object : DateFragment.DateListener {
                override fun onSuccess(dateStart: String, dateEnd: String) {
                    binding.textDate.text = "$dateStart - $dateEnd"
                    binding.swipe.isRefreshing = true
                    db.collection("moneytransaction")
                        .orderBy("created", Query.Direction.DESCENDING)
                        .whereEqualTo("username", preference.getString("pref_username"))
                        .whereGreaterThanOrEqualTo("created", stringToDate("$dateStart 00:00")!!)
                        .whereLessThanOrEqualTo("created", stringToDate("$dateEnd 23:59")!!)
                        .get()
                        .addOnSuccessListener { result ->
                            binding.swipe.isRefreshing = false
                            setTransaction( result )
                        }
                        .addOnFailureListener { exception ->
                            binding.swipe.isRefreshing = false
                            Log.e(TAG, "Error getting documents.", exception)
                        }
                }
            }).apply {
                show(supportFragmentManager, "dateFragment")
            }
        }
    }

    private fun listTransaction(){
        binding.swipe.isRefreshing = true
        db.collection("moneytransaction")
            .orderBy("created", Query.Direction.DESCENDING)
            .whereEqualTo("username", preference.getString("pref_username"))
            .limit(50)
            .get()
            .addOnSuccessListener { result ->
                binding.swipe.isRefreshing = false
                setTransaction( result )
            }
            .addOnFailureListener { exception ->
                binding.swipe.isRefreshing = false
                Log.e(TAG, "Error getting documents.", exception)
            }
    }

    private fun setTransaction(result: QuerySnapshot){
        val transactions: ArrayList<Transaction> = arrayListOf()
        for (document in result) {
            transactions.add(
                Transaction(
                    id = document.reference.id,
                    amount = document.data["amount"].toString().toInt(),
                    category = document.data["category"].toString(),
                    note = document.data["note"].toString(),
                    type = document.data["type"].toString(),
                    username = document.data["username"].toString(),
                    created = document.data["created"] as Timestamp
                )
            )
        }
        this.adapter.setData(transactions)
    }

    private fun deleteTransaction(id: String){
        db.collection("moneytransaction").document(id)
                .delete()
                .addOnSuccessListener { listTransaction() }
                .addOnFailureListener { }
    }
}