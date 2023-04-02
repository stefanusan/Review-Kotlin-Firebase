package com.lazday.moneykotlin.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.databinding.FragmentDateBinding
import java.util.*

class DateFragment(var listener: DateListener) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDateBinding
    private var clickDateStart: Boolean = false
    private var date: String = ""
    private var dateStart: String = ""
    private var dateEnd: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView("Tanggal mulai", "Pilih")
        setupListener()
    }

    private fun setupListener(){
        binding.calenderView.setOnDateChangeListener { _, year, month, day ->
            date = "$day/${month + 1}/$year"
            Log.e("DateFragment", date)
        }
        binding.textApply.setOnClickListener {
            when (clickDateStart) {
                false -> {
                    clickDateStart = true
                    dateStart = date
                    binding.calenderView.date = Date().time
                    setView("Tanggal akhir", "Terapkan")
                }
                true -> {
                    dateEnd = date
                    listener.onSuccess(dateStart, dateEnd)
                    this.dismiss()
                }
            }
        }
    }

    private fun setView(title: String, apply: String){
        binding.textTitle.text = title
        binding.textApply.text = apply
    }

    interface DateListener{
        fun onSuccess(dateStart: String, dateEnd: String)
    }
}