package com.example.projectsleader.states

import com.airbnb.mvrx.MavericksState
import com.example.projectsleader.models.Project
import com.example.projectsleader.models.User
import com.example.projectsleader.models.UserCompany

data class CompanyState(
    val projects: List<Project> = listOf(),
    val users: List<User> = listOf(),
    val userCompany: UserCompany? = null,
    val currentProject: Project? = null
) : MavericksState