package com.example.projectsleader.viewmodels

import android.content.Context
import android.widget.Toast
import com.airbnb.mvrx.MavericksViewModel
import com.example.projectsleader.models.Company
import com.example.projectsleader.models.UserCompany
import com.example.projectsleader.objects.CurrentUser
import com.example.projectsleader.objects.DataBase
import com.example.projectsleader.states.MainState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class MainViewModel(initialState: MainState) : MavericksViewModel<MainState>(initialState) {
    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val iterator = snapshot.children.iterator()
            val result: ArrayList<UserCompany> = arrayListOf()
            iterator.forEach { result.add(it.getValue(UserCompany::class.java)!!) }
            setState { copy(companies = result) }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    init {
        CurrentUser.refCompanies.addValueEventListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        CurrentUser.refCompanies.removeEventListener(listener)
    }

    fun createCompany(name: String, id: String, password: String, handler: (Boolean) -> Unit) {
        DataBase.refCompanies.get().addOnSuccessListener { companiesSnap ->
            val type: GenericTypeIndicator<HashMap<String, Company>> =
                object : GenericTypeIndicator<HashMap<String, Company>>() {}
            val isExisting: Boolean =
                companiesSnap.getValue(type)?.any { it.value.id == id } ?: false
            handler(
                if (!isExisting) {
                    DataBase.refCompanies.child(id).setValue(
                        Company(
                            hashMapOf(CurrentUser.uid to true),
                            id,
                            name,
                            password
                        )
                    )
                    CurrentUser.refCompanies.child(id).setValue(UserCompany(name, true, id))
                    true
                } else false
            )
        }
    }

    fun enterCompany(id: String, password: String, context: Context) {
        DataBase.refCompanies.get().addOnSuccessListener {
            var isEnter = false
            it.children.forEach { companySnap ->
                val company = companySnap.getValue(Company::class.java)!!
                if (company.id == id && company.password == password)
                    if (!company.users.containsKey(CurrentUser.uid)) {
                        DataBase.refCompanies.child(id).child(DataBase.COMPANY_USERS_DIR)
                            .child(CurrentUser.uid).setValue(false)
                        CurrentUser.refCompanies.child(id)
                            .setValue(
                                UserCompany(
                                    company.name,
                                    false,
                                    company.id
                                )
                            )
                        isEnter = true
                    } else Toast.makeText(
                        context,
                        "Вы уже состоите в этой компании",
                        Toast.LENGTH_SHORT
                    ).show()
            }
            if (!isEnter)
                Toast.makeText(context, "Данные введены неверно", Toast.LENGTH_SHORT).show()
        }
    }
}