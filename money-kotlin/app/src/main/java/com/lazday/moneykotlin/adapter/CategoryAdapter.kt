package com.lazday.moneykotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.databinding.AdapterCategoryBinding
import com.lazday.moneykotlin.model.Category
import java.util.logging.Handler

class CategoryAdapter (var context: Context, var categories: ArrayList<Category>, var listener: AdapterListener ):
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var listButton: ArrayList<MaterialButton> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.buttonCategory.text = category.name
        holder.binding.buttonCategory.setOnClickListener {
            listener.onClick( category )
            buttonColor(it as MaterialButton)
        }
        listButton.add(holder.binding.buttonCategory)
    }

    class ViewHolder(val binding: AdapterCategoryBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(data: List<Category>) {
        categories.addAll(data)
        notifyDataSetChanged()
    }

    fun setButton(category: String) {
        listButton.forEach { button ->
            if (button.text.toString().contains( category )) {
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_700))
            }
        }
    }

    private fun buttonColor(button: MaterialButton) {
        listButton.forEach {
            it.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }
        button.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_700))
    }

    interface AdapterListener {
        fun onClick(category: Category)
    }

}