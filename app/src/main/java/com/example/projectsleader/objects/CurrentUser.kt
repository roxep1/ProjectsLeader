package com.example.projectsleader.objects

import com.example.projectsleader.models.UserCompany
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

object CurrentUser {
    internal const val COMPANIES_DIR = "companies"
    internal const val PROJECTS_DIR = "projects"

    val auth
        get() = FirebaseAuth.getInstance()

    val currentUser
        get() = auth.currentUser

    val uid: String
        get() = currentUser!!.uid

    val refUser: DatabaseReference
        get() = DataBase.refUsers.child(uid)

    val refCompanies: DatabaseReference
        get() = refUser.child(COMPANIES_DIR)

    var currentCompanyId: String? = null

    private val refCurrentCompany: DatabaseReference
        get() = refCompanies.child(currentCompanyId!!).ref

    val refCurrentCompanyProjects
        get() = refCurrentCompany.child(PROJECTS_DIR)

    fun getCurrentUserCompany(onSuccess: (UserCompany) -> Unit) =
        DataBase.get(currentCompanyId!!, refCompanies, onSuccess)
}