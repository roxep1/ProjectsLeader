package com.example.projectsleader.models

import java.io.Serializable

data class Project(
    val id: String? = null,
    val title: String? = null,
    val users: HashMap<String, String> = hashMapOf(),
    val leader: String? = null,
    val tasks: HashMap<String, Task> = hashMapOf()
) : Serializable {
    override fun toString(): String {
        return title!!
    }
}
