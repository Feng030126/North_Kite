package com.rst2g1.northkite.ui.smedia

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.rst2g1.northkite.databinding.FragmentAddPostBinding
import com.squareup.picasso.Picasso

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val database = FirebaseDatabase.getInstance().reference.child("posts")
    private var selectedImageUri: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Picasso.get().load(it).into(binding.imageViewPost)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSelectImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.buttonAddPost.setOnClickListener {
            val postText = binding.editTextPost.text.toString()
            if (selectedImageUri != null) {
                uploadImageAndSavePost(postText, selectedImageUri!!)
            } else {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageAndSavePost(postText: String, imageUri: Uri) {
        val postID = database.push().key ?: ""
        val storageReference = FirebaseStorage.getInstance().reference.child("post_images/$postID")

        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    savePostToDatabase(postText, downloadUri.toString(), postID)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePostToDatabase(postText: String, imageUrl: String, postID: String) {
        val currentUserID = "test_user_id" // Replace with method to get current user ID
        val post = Post(
            id = postID,
            username = "Test User", // Replace with actual username
            profilePicUrl = "https://example.com/profile.jpg", // Replace with actual profile picture URL
            postImageUrl = imageUrl,
            description = postText
        )

        database.child(postID).setValue(post).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Post added successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to add post: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
