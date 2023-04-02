package com.lazday.moneykotlin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lazday.moneykotlin.BaseActivity
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.databinding.ActivityRegisterBinding
import com.lazday.moneykotlin.model.Transaction
import com.lazday.moneykotlin.model.User

private const val TAG = "RegisterActivity"

class RegisterActivity : BaseActivity() {

    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        showProgress(false)
    }

    private fun setupListener(){
        binding.buttonRegister.setOnClickListener {
            if (isRequired()) checkUsername()
            else Toast.makeText(applicationContext, "Isi data kamu dengan lengkap!", Toast.LENGTH_SHORT).show()
        }
        binding.textLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showProgress(show: Boolean) {
        when(show) {
            true -> {
                binding.progress.visibility = View.VISIBLE
                binding.buttonRegister.visibility = View.GONE
                binding.textAlert.visibility = View.GONE
            }
            false -> {
                binding.progress.visibility = View.GONE
                binding.buttonRegister.visibility = View.VISIBLE
            }
        }
    }

    private fun isRequired() : Boolean {
        return  (
                binding.editName.text.toString().isNotEmpty() &&
                binding.editUsername.text.toString().isNotEmpty() &&
                binding.editPassword.text.toString().isNotEmpty()
                )
    }

    private fun checkUsername(){
        showProgress(true)
        db.collection("moneyuser")
            .whereEqualTo("username", binding.editUsername.text.toString(),)
            .get()
            .addOnSuccessListener { result ->
                showProgress(false)
                if (result.isEmpty) addUser()
                else binding.textAlert.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                showProgress(false)
                Log.e(TAG, "Error getting documents.", exception)
            }
    }

    private fun addUser(){
        showProgress(true)
        val user = User(
            name = binding.editName.text.toString(),
            username = binding.editUsername.text.toString(),
            password = binding.editPassword.text.toString(),
            created = Timestamp.now()
        )
        db.collection("moneyuser")
            .add(user)
            .addOnSuccessListener { documentReference ->
                showProgress(false)
                Log.e(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(applicationContext, "User Added!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }
            .addOnFailureListener { e ->
                showProgress(false)
                Log.e(TAG, "Error adding document", e)
            }
    }
}