package com.example.projectsleader.objects

import android.widget.EditText
import com.example.projectsleader.models.Project
import com.example.projectsleader.models.Task
import com.example.projectsleader.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


object DataBase {
    private const val USERS_DIR = "Users"
    private const val COMPANIES_DIR = "Companies"
    const val COMPANY_USERS_DIR = "users"
    private const val COMPANY_PROJECTS_DIR = "projects"

    internal inline fun <reified T> get(
        id: String,
        ref: DatabaseReference,
        crossinline onSuccess: (T) -> Unit
    ) =
        ref.child(id).get()
            .addOnSuccessListener { onSuccess(it.getValue(T::class.java)!!) }

    fun deleteProject(project: Project, onSuccess: () -> Unit) {
        project.users.keys.forEach { uid ->
            refUsers.child(uid).child(CurrentUser.COMPANIES_DIR)
                .child(CurrentUser.currentCompanyId!!).child(CurrentUser.PROJECTS_DIR)
                .child(project.id!!).removeValue()
        }
        if (project.leader != null)
            refUsers.child(project.leader).child(CurrentUser.COMPANIES_DIR)
                .child(CurrentUser.currentCompanyId!!).child(CurrentUser.PROJECTS_DIR)
                .child(project.id!!).removeValue()
        refCurrentCompanyProjects.child(project.id!!).removeValue()
            .addOnSuccessListener { onSuccess() }
    }

    fun getTask(id: String, projectId: String, onSuccess: (Task) -> Unit) =
        get(id, refCurrentCompanyProjects.child(projectId).child("tasks"), onSuccess)

    fun getProject(id: String, onSuccess: (Project) -> Unit) =
        get(id, refCurrentCompanyProjects, onSuccess)

    fun getUser(uid: String, onSuccess: (User) -> Unit) = get(uid, refUsers, onSuccess)

    private val refDatabase: DatabaseReference
        get() = FirebaseDatabase.getInstance().reference

    val refCurrentCompanyProjects: DatabaseReference
        get() = refCompanies.child(CurrentUser.currentCompanyId!!).child(COMPANY_PROJECTS_DIR)

    val refCurrentCompanyUsers: DatabaseReference
        get() = refCompanies.child(CurrentUser.currentCompanyId!!).child(COMPANY_USERS_DIR)

    val refUsers: DatabaseReference
        get() = refDatabase.child(USERS_DIR)

    val refCompanies: DatabaseReference
        get() = refDatabase.child(COMPANIES_DIR)

    fun EditText.isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this.text.toString()).matches()
    }

    fun EditText.isPasswordOrLoginValid(): Boolean {
        return this.text.length >= 6 && this.text.isNotEmpty() && this.text.isNotBlank()
    }
}