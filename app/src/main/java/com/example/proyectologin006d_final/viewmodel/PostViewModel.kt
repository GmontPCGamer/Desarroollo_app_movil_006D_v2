package com.example.proyectologin006d_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectologin006d_final.data.model.Post
import com.example.proyectologin006d_final.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    // Flujo interno mutable
    internal val _postList = MutableStateFlow<List<Post>>(emptyList())

    // Flujo público inmutable
    val postList: StateFlow<List<Post>> = _postList

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                println("error al obtener datos ${e.localizedMessage}")
            }
        }
    }
}