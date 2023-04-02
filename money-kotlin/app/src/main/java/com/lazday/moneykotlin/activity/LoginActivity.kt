package com.lazday.moneykotlin.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lazday.moneykotlin.BaseActivity
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.databinding.ActivityLoginBinding
import com.lazday.moneykotlin.model.User
import com.lazday.moneykotlin.preferences.PreferencesManager
import com.lazday.moneykotlin.util.timestampToString

private const val TAG = "LoginActivity"

class LoginActivity : BaseActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val firebase by lazy { Firebase.firestore }
    private val preference by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupListener()
//        setupTest()
    }

    private fun setupView(){
        binding.textAlert.visibility = View.GONE
        showLoading(false)
    }

    private fun setupListener(){
        binding.buttonLogin.setOnClickListener {
            if (
                    binding.editUsername.text.toString().isNotEmpty() &&
                    binding.editPassword.text.toString().isNotEmpty()
            ) {
                showLoading(true)
                firebase.collection("moneyuser")
                        .whereEqualTo("username",  binding.editUsername.text.toString())
                        .whereEqualTo("password",  binding.editPassword.text.toString())
                        .get()
                        .addOnSuccessListener { result ->
                            showLoading(false)
                            if (result.isEmpty) {
                                binding.textAlert.visibility = View.VISIBLE
                            } else {
                                for (document in result) {
                                    Log.e(TAG, "moneytransaction ${ document.data}")
                                    saveSession (
                                            User (
                                                    name = document.data["name"].toString(),
                                                    username = document.data["username"].toString(),
                                                    password = document.data["password"].toString(),
                                                    created = document.data["created"] as Timestamp
                                            )
                                    )
                                }
                                Toast.makeText(applicationContext, "Login berhasil!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                        }
                        .addOnFailureListener { exception ->
                            showLoading(false)
                            Log.e(TAG, "Error getting documents.", exception)
                        }
            }
        }
        binding.textRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupTest(){
        binding.editUsername.setText("irsyad")
        binding.editPassword.setText("password")
    }

    private fun showLoading(loading: Boolean) {
        if (loading) {
            binding.progress.visibility = View.VISIBLE
            binding.buttonLogin.visibility = View.GONE
            binding.textAlert.visibility = View.GONE
        } else {
            binding.buttonLogin.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
        }
    }

    private fun saveSession(user: User){
        preference.put("pref_is_login", true)
        preference.put("pref_name", user.name)
        preference.put("pref_username", user.username)
        preference.put("pref_created", timestampToString(user.created) )
        if (preference.getInt("pref_avatar") == 0) preference.put("pref_avatar", R.drawable.avatar1)
    }
}