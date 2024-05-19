package com.rst2g1.northkite.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rst2g1.northkite.MainActivity
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.FragmentNotificationBinding
import com.rst2g1.northkite.databinding.NotificationsObjectBinding

class NotificationsFragment : Fragment() {

    private lateinit var _binding: FragmentNotificationBinding
    private val binding get() = _binding

    private lateinit var notificationReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)

        database =
            FirebaseDatabase.getInstance("https://northkite-1120-default-rtdb.asia-southeast1.firebasedatabase.app")

        notificationReference = database.reference.child("notification")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationList = mutableListOf<Notification>()
        val currentUserID = sharedPreferences.getString("current_user", null)

        val adapter = NotificationsAdapter(notificationList)
        binding

        notificationReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (data in snapshot.children) {
                    val notification = data.getValue(Notification::class.java)
                    if(notification?.userID == currentUserID)
                    {
                        notification?.let {
                            notificationList.add(it)
                        }
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

}