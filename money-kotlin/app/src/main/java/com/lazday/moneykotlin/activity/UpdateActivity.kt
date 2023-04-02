package com.lazday.moneykotlin.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lazday.moneykotlin.BaseActivity
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.adapter.CategoryAdapter
import com.lazday.moneykotlin.databinding.ActivityCreateBinding
import com.lazday.moneykotlin.model.Category
import com.lazday.moneykotlin.model.Transaction
import com.lazday.moneykotlin.preferences.PreferencesManager

private const val TAG = "UpdateActivity"

class UpdateActivity : BaseActivity() {

    private val db by lazy { Firebase.firestore }
    val id by lazy { intent.getStringExtra("id") }
    private val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) }
    private lateinit var adapter: CategoryAdapter
    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        detailTransaction()
    }

    private fun setupView(){
        binding.buttonSave.setText( "SIMPAN PERUBAHAN" )
        adapter = CategoryAdapter(this, arrayListOf(), object : CategoryAdapter.AdapterListener {
            override fun onClick(category: Category) {
                transaction.category = category.name
            }
        })
        binding.listCategory.adapter = this.adapter
    }

    private fun setupListener(){
        binding.buttonIn.setOnClickListener {
            transaction.type = "IN"
            buttonColor(it as MaterialButton)
        }
        binding.buttonOut.setOnClickListener {
            transaction.type = "OUT"
            buttonColor(it as MaterialButton)
        }
        binding.buttonSave.setOnClickListener {
            transaction.amount = binding.editAmount.text.toString().toInt()
            transaction.note = binding.editDescription.text.toString()
            db.collection("moneytransaction").document(id!!)
                    .set(transaction)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        Toast.makeText(applicationContext, "Transaction Updated!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    private fun detailTransaction(){
        db.collection("moneytransaction")
                .document(id!!)
                .get()
                .addOnSuccessListener { document ->

                    transaction = Transaction(
                            id = document.reference.id,
                            amount = document["amount"].toString().toInt(),
                            category = document["category"].toString().toUpperCase(),
                            note = document["note"].toString(),
                            type = document["type"].toString().toUpperCase(),
                            username = document["username"].toString(),
                            created = document["created"] as Timestamp
                    )

                    binding.editAmount.setText( transaction.amount.toString() )
                    binding.editDescription.setText( transaction.note )
                    when (transaction.type) {
                        "IN" -> buttonColor(binding.buttonIn)
                        "OUT" -> buttonColor(binding.buttonOut)
                    }
                    listCategory()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents.", exception)
                }
    }

    private fun buttonColor(view: View){
        listOf<MaterialButton>(binding.buttonIn, binding.buttonOut).forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_700))
    }

    private fun listCategory(){
        val categories: ArrayList<Category> = arrayListOf()
        db.collection("moneycategory")
                .get()
                .addOnSuccessListener { result ->
                    result.forEach { document ->
                        categories.add(
                                Category(
                                        name = document.data["name"].toString()
                                )
                        )
                    }
                    adapter.setData( categories )
                    Handler(Looper.myLooper()!!).postDelayed(kotlinx.coroutines.Runnable {
                        adapter.setButton ( transaction.category )
                    }, 200)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents.", exception)
                }
    }
}