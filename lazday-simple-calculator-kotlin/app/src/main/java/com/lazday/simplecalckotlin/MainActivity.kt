package com.lazday.simplecalckotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var OPERATOR: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
    }

    private fun setupListener(){

        button_calc.setOnClickListener {

            if (validate()) {

                val value1: Int = edit_value1.text.toString().toInt()
                val value2: Int = edit_value2.text.toString().toInt()

                text_result.text = value(value1, value2)

            } else {
                Toast.makeText(applicationContext, "Masukkan data dengan benar",
                    Toast.LENGTH_SHORT).show()
            }

        }

        radio_operators.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(group.checkedRadioButtonId)
            OPERATOR = radioButton.text.toString()
            text_result.text = "HASIL"
        }
    }

    private fun value(value1: Int, value2: Int): String {

        var value: Int = 0

        when(OPERATOR) {
            "+" -> value = value1 + value2
            "-" -> value = value1 - value2
            "x" -> value = value1 * value2
            ":" -> value = value1 / value2
        }

        return value.toString()
    }

    private fun validate() : Boolean {

        if (edit_value1.text.isNullOrEmpty() || edit_value2.text.isNullOrEmpty()) {
            return false
        } else if (OPERATOR.isNullOrEmpty()) {
            return false
        }

        return true
    }
}
