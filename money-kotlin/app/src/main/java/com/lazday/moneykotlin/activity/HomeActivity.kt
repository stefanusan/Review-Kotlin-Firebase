package com.lazday.moneykotlin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lazday.moneykotlin.BaseActivity
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.adapter.TransactionAdapter
import com.lazday.moneykotlin.databinding.ActivityHomeBinding
import com.lazday.moneykotlin.model.Category
import com.lazday.moneykotlin.model.Transaction
import com.lazday.moneykotlin.preferences.PreferencesManager
import com.lazday.moneykotlin.util.amountFormat
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeActivity"

class HomeActivity : BaseActivity() {

    public fun close(){
        this.finish()
    }

    class HomeClass{
        companion object{
            var activity: Activity? = null
        }
    }

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val bindingAvatar by lazy { binding.layoutAvatar }
    private val bindingBalance by lazy { binding.layoutBalance }
    private val db by lazy { Firebase.firestore }
    private lateinit var adapter: TransactionAdapter
    private val preference by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        HomeClass.activity = this
        setupList()
        setupListener()

//        val currentTimestamp = System.currentTimeMillis()

//        val stamp = Timestamp(System.currentTimeMillis())
//        val currentTimestamp = Date(stamp.time)

        val currentTimestamp = com.google.firebase.Timestamp.now()
        Log.e(TAG, "currentTimestamp $currentTimestamp")

        val dateFormat = SimpleDateFormat("DD/MM/yyyy", Locale.getDefault())
        Log.e(TAG, "toDate ${dateFormat.format(currentTimestamp.toDate())}")
        Log.e(TAG, "fromDate ${currentTimestamp.toDate()}")

        // GET CATEGORY
        val categories: ArrayList<Category> = arrayListOf()
        db.collection("moneycategory")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    categories.add(
                            Category(
                                    name = document.data["name"].toString()
                            )
                    )
                }
                Log.e(TAG, "categories $categories")

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {

            startActivity(Intent(this, CreateActivity::class.java))

            // INSERT TRANSACTION
//            val transaction = hashMapOf(
//                "amount" to 500000,
//                "category" to "BONUS",
//                "note" to "Dividen",
//                "type" to "in",
//                "username" to "aris"
//            )
//
//            db.collection("moneytransaction")
//                .add(transaction)
//                .addOnSuccessListener { documentReference ->
//                    Log.e(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                }
//                .addOnFailureListener { e ->
//                    Log.e(TAG, "Error adding document", e)
//                }

            // UPDATE TRANSACTION
//            val transaction = hashMapOf(
//                "amount" to 500000,
//                "category" to "BONUS",
//                "note" to "Dividen",
//                "type" to "in",
//                "username" to "aris"
//            )
//
//            db.collection("moneytransaction").whe
//                .add(transaction)
//                .addOnSuccessListener { documentReference ->
//                    Log.e(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                }
//                .addOnFailureListener { e ->
//                    Log.e(TAG, "Error adding document", e)
//                }

        }
    }

    override fun onStart() {
        super.onStart()
        getProfile()
        getBalance()
        listLimitTransaction()
    }

    private fun setupList(){
        adapter = TransactionAdapter(arrayListOf(), null)
        binding.listTransaction.adapter = this.adapter
    }

    private fun setupListener(){
        binding.avatar.setOnClickListener {
            startActivity(
                    Intent(this, ProfileActivity::class.java)
                            .putExtra("balance", bindingBalance.textBalance.text.toString())
            )
        }
        binding.textTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }
    }

    private fun getProfile(){
        bindingAvatar.imageAvatar.setImageResource(preference.getInt("pref_avatar"))
        bindingAvatar.textAvatar.text = preference.getString("pref_name")
    }

    private fun listLimitTransaction(){
        binding.progress.visibility = View.VISIBLE
        val transactions: ArrayList<Transaction> = arrayListOf()
        db.collection("moneytransaction")
            .orderBy("created", Query.Direction.DESCENDING)
            .whereEqualTo("username", preference.getString("pref_username"))
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                binding.progress.visibility = View.GONE
                for (document in result) {
                    Log.e(TAG, "moneytransaction ${document.data}")
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
            .addOnFailureListener { exception ->
                binding.progress.visibility = View.GONE
                Log.e(TAG, "Error getting documents.", exception)
            }
    }

    private fun getBalance(){
        var totalBalance: Int = 0
        var totalIn: Int = 0
        var totalOut: Int = 0
        db.collection("moneytransaction")
            .whereEqualTo("username", preference.getString("pref_username"))
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    totalBalance += document.data["amount"].toString().toInt()
                    when (document.data["type"].toString()) {
                        "IN" -> totalIn += document.data["amount"].toString().toInt()
                        "OUT" -> totalOut += document.data["amount"].toString().toInt()
                    }
                }
                bindingBalance.textBalance.text = "Rp ${amountFormat(totalBalance)}"
                bindingBalance.textIn.text = "Rp ${amountFormat(totalIn)}"
                bindingBalance.textOut.text = "Rp ${amountFormat(totalOut)}"
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }
    }
}