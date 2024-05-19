package com.rst2g1.northkite.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rst2g1.northkite.databinding.NotificationsObjectBinding

class NotificationsAdapter(val notifications: List<Notification>) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    class ViewHolder(val binding: NotificationsObjectBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems(notification: Notification){

            binding.notificationTitle.text = notification.title
            binding.notificationMessage.text = notification.message

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NotificationsObjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bindItems(notification)
    }

}