package com.example.projectsleader.fragments.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.projectsleader.R
import com.example.projectsleader.models.Project
import com.example.projectsleader.objects.CurrentUser
import com.example.projectsleader.states.CompanyState
import com.example.projectsleader.user
import com.example.projectsleader.viewmodels.CompanyViewModel
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.set_to_project_dialog.view.*

class UsersFragment : Fragment(), MavericksView {
    private val viewModel: CompanyViewModel by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun invalidate() = withState(viewModel) { state ->
        usersRecycler.withModels {
            state.users.forEach { user ->
                if (user.uid != CurrentUser.uid)
                    user {
                        id(user.uid)
                        email(user.email)
                        login(user.login)
                        isLeader(false)
                        if (state.userCompany?.creator!!)
                            onCardClick(View.OnClickListener {
                                showSetProjectDialog(
                                    user.uid!!,
                                    user.email!!,
                                    state
                                )
                            })
                    }
            }
        }
    }

    fun showSetProjectDialog(uid: String, email: String, state: CompanyState) {
        val setProjectView =
            layoutInflater.inflate(R.layout.set_to_project_dialog, null)
        setProjectView.projectsSpn.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            state.projects
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Назначить на проект")
            .setView(setProjectView)
            .setCancelable(false)
            .setNegativeButton("Отмена") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Назначение на проект отменено",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setPositiveButton("Назначить") { _, _ ->
                val project: Project? = setProjectView.projectsSpn.selectedItem as Project?
                if (project != null)
                    viewModel.setToProject(uid, email, project) {
                        Toast.makeText(
                            requireContext(),
                            "$email успешно назначен на проект ${project.title}!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                else Toast.makeText(
                    requireContext(),
                    "Необходимо создать хотя бы один проект!",
                    Toast.LENGTH_LONG
                ).show()
            }.show()
    }
}