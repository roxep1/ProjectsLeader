package com.example.projectsleader.models

data class Company(
    val users: HashMap<String, Boolean> = hashMapOf(),
    val id: String? = null,
    val name: String? = null,
    val password: String? = null,
    val projects: HashMap<String, Project> = hashMapOf()
)
