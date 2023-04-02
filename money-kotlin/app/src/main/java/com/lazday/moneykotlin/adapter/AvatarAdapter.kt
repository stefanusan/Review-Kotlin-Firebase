package com.lazday.moneykotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lazday.moneykotlin.databinding.AdapterAvatarBinding

class AvatarAdapter (var avatars: ArrayList<Int>, var listener: AdapterListener ):
    RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = avatars.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val avatar: Int = avatars[position]
        holder.binding.imageAvatar.setImageResource(avatar)
        holder.binding.imageAvatar.setOnClickListener {
            listener.onClick( avatar )
        }
    }

    class ViewHolder(val binding: AdapterAvatarBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(data: List<Int>) {
        avatars.clear()
        avatars.addAll(data)
        notifyDataSetChanged()
    }

    interface AdapterListener {
        fun onClick(avatar: Int)
    }

}