package com.example.projectsleader.models

data class UserProject(
    val tasks: HashMap<String, String> = hashMapOf(),
    val title: String? = null,
    val id: String? = null
)
