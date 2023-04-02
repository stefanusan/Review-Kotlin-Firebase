package com.lazday.moneykotlin.activity

import android.os.Bundle
import com.lazday.moneykotlin.BaseActivity
import com.lazday.moneykotlin.databinding.ActivityProfileBinding


class ProfileActivity : BaseActivity() {

    val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val homeActivity = HomeActivity()
        homeActivity.close()
    }
}