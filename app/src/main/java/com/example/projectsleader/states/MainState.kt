package com.example.projectsleader.states

import com.airbnb.mvrx.MavericksState
import com.example.projectsleader.models.UserCompany

data class MainState(val companies: List<UserCompany>? = null) : MavericksState