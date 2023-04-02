package com.lazday.moneykotlin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.lazday.moneykotlin.BaseActivity
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.preferences.PreferencesManager

class SplashActivity : BaseActivity() {

    private val pref by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.myLooper()!!)
                .postDelayed( {
                    if (pref.getBoolean("pref_is_login"))
                        startActivity(Intent(this, HomeActivity::class.java))
                    else
                        startActivity(Intent(this, LoginActivity::class.java))
                    finish()

                }, 2000 )

    }
}