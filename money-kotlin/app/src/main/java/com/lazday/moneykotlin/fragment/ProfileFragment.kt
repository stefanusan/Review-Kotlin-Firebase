package com.lazday.moneykotlin.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.activity.HomeActivity
import com.lazday.moneykotlin.activity.LoginActivity
import com.lazday.moneykotlin.databinding.FragmentProfileBinding
import com.lazday.moneykotlin.preferences.PreferencesManager

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val pref by lazy { PreferencesManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getProfile()
    }

    private fun getProfile(){
        binding.imageAvatar.setImageResource( pref.getInt("pref_avatar") )
        binding.textName.text = pref.getString("pref_name")
        binding.textBalance.text = requireActivity().intent.getStringExtra("balance")
        binding.textEmail.text = pref.getString("pref_username")
        binding.textDate.text = pref.getString("pref_created")
    }

    private fun setupListener(){
        binding.imageAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_avatarFragment)
        }
        binding.cardLogout.setOnClickListener {
            pref.clear()
            Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
            startActivity(
                    Intent(requireActivity(), LoginActivity::class.java)
                            .addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
            )
            requireActivity().finish()
        }
    }
}