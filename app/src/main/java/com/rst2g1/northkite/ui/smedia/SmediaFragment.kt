package com.rst2g1.northkite.ui.smedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.rst2g1.northkite.R
import com.rst2g1.northkite.databinding.FragmentSmediaBinding

class SmediaFragment : Fragment() {

    private var _binding: FragmentSmediaBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var adapter: PostAdapter
    private val posts = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSmediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchPostsFromFirebase()

        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_smedia_to_addPostFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(posts, object : PostAdapter.OnItemClickListener {
            override fun onItemClick(post: Post) {
                // Handle post item click
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchPostsFromFirebase() {
        database = FirebaseDatabase.getInstance().reference.child("posts")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { posts.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
