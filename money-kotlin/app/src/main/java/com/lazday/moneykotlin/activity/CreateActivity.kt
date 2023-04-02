package com.lazday.moneykotlin.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
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

private const val TAG = "CreateActivity"

class CreateActivity : BaseActivity() {

    private val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }
    private val preference by lazy { PreferencesManager(this) }
    private lateinit var adapter: CategoryAdapter

    private var type: String = ""
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupList()
        setupListener()
        listCategory()
    }

    private fun setupList(){
        adapter = CategoryAdapter(this, arrayListOf(), object : CategoryAdapter.AdapterListener {
            override fun onClick(category: Category) {
                this@CreateActivity.category = category.name
            }
        })
        binding.listCategory.adapter = this.adapter
    }

    private fun buttonColor(button: MaterialButton) {
        listOf<MaterialButton>(binding.buttonIn, binding.buttonOut).forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_700))
    }

    private fun setupListener() {
        binding.buttonIn.setOnClickListener {
            type = "IN"
            buttonColor(it as MaterialButton)
        }
        binding.buttonOut.setOnClickListener {
            type = "OUT"
            buttonColor(it as MaterialButton)
        }
        binding.buttonSave.setOnClickListener {

            val transaction = Transaction(
                    null,
                    amount = binding.editAmount.text.toString().toInt(),
                    category = category,
                    note = binding.editDescription.text.toString(),
                    type = type,
                    username = "irsyad",
                    created = Timestamp.now()
            )

            db.collection("moneytransaction")
                .add(transaction)
                .addOnSuccessListener { documentReference ->
                    Log.e(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(applicationContext, "Transaction Added!", Toast.LENGTH_SHORT).show()
                    finish()

                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                }

        }
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
                Log.e(TAG, "categories $categories")
                adapter.setData( categories )
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }
    }
}