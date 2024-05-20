package com.rst2g1.northkite.ui.smedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.rst2g1.northkite.databinding.FragmentAddPostBinding
import com.rst2g1.northkite.ui.User

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private lateinit var currentUser: User
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val currentUserID = sharedPreferences.getString("current_user", null)

        fetchCurrentUser(currentUserID)

        database = FirebaseDatabase.getInstance().reference.child("posts")
        storage = FirebaseStorage.getInstance()

        binding.buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        binding.buttonAddPost.setOnClickListener {
            val description = binding.editTextPost.text.toString().trim()

            if (description.isEmpty() || selectedImageUri == null) {
                Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageRef = storage.reference.child("posts/${System.currentTimeMillis()}.jpg")
            selectedImageUri?.let {
                imageRef.putFile(it)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            savePost(currentUser.username, currentUser.profilePictureUrl ?: "", description, uri.toString())
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun fetchCurrentUser(userID: String?) {
        if (userID != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userID)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUser = snapshot.getValue(User::class.java) ?: User()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
        }
    }

    private fun savePost(username: String, profilePicUrl: String, description: String, imageUrl: String) {
        val postId = database.push().key
        val post = Post(
            id = postId ?: "",
            username = username,
            profilePicUrl = profilePicUrl,
            postImageUrl = imageUrl,
            likes = 0,
            description = description,
            likedBy = mutableListOf()
        )
        if (postId != null) {
            database.child(postId).setValue(post)
                .addOnCompleteListener {
                    Toast.makeText(context, "Post added successfully", Toast.LENGTH_SHORT).show()
                    // Navigate back to the SmediaFragment
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to add post", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.imageViewPost.setImageURI(selectedImageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
