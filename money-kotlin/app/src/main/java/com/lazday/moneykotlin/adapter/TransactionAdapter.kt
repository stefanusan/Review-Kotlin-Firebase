package com.lazday.moneykotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lazday.moneykotlin.R
import com.lazday.moneykotlin.databinding.AdapterTransactionBinding
import com.lazday.moneykotlin.model.Transaction
import com.lazday.moneykotlin.util.timestampToString

class TransactionAdapter (
        var transactions: ArrayList<Transaction>,
        var listener: AdapterListener?
): RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        if (transaction.type.toUpperCase() == "IN") holder.binding.imageType.setImageResource(R.drawable.ic_in)
        else holder.binding.imageType.setImageResource(R.drawable.ic_out)

        holder.binding.textNote.text = transaction.note
        holder.binding.textCategory.text = transaction.category.toLowerCase()
        holder.binding.textAmount.text = transaction.amount.toString()
        holder.binding.textDate.text = timestampToString( transaction.created )

        holder.binding.container.setOnClickListener {
            listener?.onClick( transaction )
        }
        holder.binding.container.setOnLongClickListener {
            listener?.onLongClick( transaction )
            true
        }
    }

    class ViewHolder(val binding: AdapterTransactionBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(data: List<Transaction>) {
        transactions.clear()
        transactions.addAll(data)
        notifyDataSetChanged()
    }

    interface AdapterListener{
        fun onClick(transaction: Transaction)
        fun onLongClick(transaction: Transaction)
    }

}