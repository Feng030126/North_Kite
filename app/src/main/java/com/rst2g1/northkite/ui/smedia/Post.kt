package com.rst2g1.northkite.ui.smedia

data class Post(
    var id: String = "",
    val username: String = "",
    val profilePicUrl: String = "",
    val postImageUrl: String = "",
    var likes: Int = 0,
    val description: String = "",
    val likedBy: MutableList<String> = mutableListOf()
)
