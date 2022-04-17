package com.example.projectsleader.models

data class User(
    val uid: String? = null,
    val login: String? = null,
    val email: String? = null,
    val companies: HashMap<String, UserCompany> = hashMapOf()
)
