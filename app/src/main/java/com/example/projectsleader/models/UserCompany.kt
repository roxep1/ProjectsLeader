package com.example.projectsleader.models

data class UserCompany (
    val name: String? = null,
    val creator: Boolean? = null,
    val id: String? = null,
    val projects: HashMap<String, UserProject> = hashMapOf())