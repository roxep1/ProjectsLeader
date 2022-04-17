package com.example.projectsleader.viewmodels

import com.airbnb.mvrx.MavericksViewModel
import com.example.projectsleader.models.Project
import com.example.projectsleader.models.Task
import com.example.projectsleader.models.UserProject
import com.example.projectsleader.objects.CurrentUser
import com.example.projectsleader.objects.DataBase
import com.example.projectsleader.states.CompanyState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class CompanyViewModel(initialState: CompanyState) :
    MavericksViewModel<CompanyState>(initialState) {
    private var isProjectListener = false
    private var currentProjectId: String? = null
    private val projectListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            DataBase.getProject(currentProjectId!!) {
                setState { copy(currentProject = it) }
            }
        }

        override fun onCancelled(error: DatabaseError) {}

    }
    private val projectsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val type = object : GenericTypeIndicator<HashMap<String, Project>>() {}
            setState { copy(projects = snapshot.getValue(type)?.values?.toList() ?: listOf()) }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    private val usersListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            setState { copy(users = listOf()) }
            val type = object : GenericTypeIndicator<HashMap<String, Boolean>>() {}
            val ids = snapshot.getValue(type)!!.keys.toList()
            ids.forEach {
                DataBase.getUser(it) {
                    setState { copy(users = users.plus(it)) }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    private val userCompanyListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            CurrentUser.getCurrentUserCompany {
                setState { copy(userCompany = it) }
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    init {
        CurrentUser.refCompanies.addValueEventListener(userCompanyListener)
        DataBase.refCurrentCompanyProjects.addValueEventListener(projectsListener)
        DataBase.refCurrentCompanyUsers.addValueEventListener(usersListener)
    }

    override fun onCleared() {
        super.onCleared()
        DataBase.refCurrentCompanyProjects.removeEventListener(projectsListener)
        DataBase.refCurrentCompanyUsers.removeEventListener(usersListener)
        CurrentUser.refCompanies.removeEventListener(userCompanyListener)
        if (isProjectListener)
            DataBase.refCurrentCompanyProjects.child(currentProjectId!!)
                .removeEventListener(projectListener)
    }

    fun createProject(title: String, onSuccess: () -> Unit) {
        val ref = DataBase.refCurrentCompanyProjects.push()
        ref.setValue(Project(ref.key, title)).addOnSuccessListener { onSuccess() }
    }

    fun deleteProject(project: Project, onSuccess: () -> Unit) =
        DataBase.deleteProject(project, onSuccess)

    fun setProjectListener(id: String) {
        currentProjectId = id
        DataBase.refCurrentCompanyProjects.child(currentProjectId!!)
            .addValueEventListener(projectListener)
        isProjectListener = true
    }

    fun addTask(uid: String, title: String, desc: String, date: String, onSuccess: () -> Unit) {
        val ref = DataBase.refCurrentCompanyProjects.child(currentProjectId!!).child("tasks").push()
        ref.setValue(Task(title, desc, date, uid)).addOnSuccessListener { onSuccess() }
        withState {
            DataBase.refUsers.child(uid).child(CurrentUser.COMPANIES_DIR)
                .child(it.userCompany!!.id!!).child(CurrentUser.PROJECTS_DIR)
                .child(currentProjectId!!).child("tasks").child(ref.key!!).setValue(title)
        }
    }

    fun setNewLeader(uid: String, onSuccess: () -> Unit) {
        val refProject = DataBase.refCurrentCompanyProjects.child(currentProjectId!!)

        fun forUser(){
            refProject.child("users").child(uid).removeValue()
            refProject.child("leader").setValue(uid).addOnSuccessListener { onSuccess() }
        }

        DataBase.getProject(currentProjectId!!) { project ->
            if (project.leader != null)
                DataBase.getUser(project.leader) {
                    refProject.child("users").child(project.leader).setValue(it.email)
                    forUser()
                }
            else forUser()
        }
    }

    fun getTask(id: String, onSuccess: (Task) -> Unit) = withState { state ->
        state.projects.forEach { project ->
            if (project.tasks.keys.contains(id))
                DataBase.getTask(id, project.id!!, onSuccess)
        }
    }

    fun deleteTask(id: String, onSuccess: () -> Unit) = withState { state ->
        state.userCompany?.projects?.values?.forEach { project ->
            if (project.tasks.keys.contains(id)) {
                DataBase.refCurrentCompanyProjects.child(project.id!!).child("tasks").child(id)
                    .removeValue().addOnSuccessListener { onSuccess() }
                CurrentUser.refCurrentCompanyProjects.child(project.id).child("tasks").child(id)
                    .removeValue()
            }
        }
    }

    fun setToProject(uid: String, email: String, project: Project, onSuccess: () -> Unit) {
        DataBase.refCurrentCompanyProjects.child(project.id!!).child("users").child(uid)
            .setValue(email).addOnSuccessListener { onSuccess() }
        DataBase.refUsers.child(uid).child(CurrentUser.COMPANIES_DIR)
            .child(CurrentUser.currentCompanyId!!)
            .child(CurrentUser.PROJECTS_DIR).child(project.id)
            .setValue(UserProject(title = project.title, id = project.id))
    }
}