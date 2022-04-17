package com.example.projectsleader.fragments.company

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.projectsleader.R
import com.example.projectsleader.addButton
import com.example.projectsleader.models.Project
import com.example.projectsleader.objects.DataBase
import com.example.projectsleader.project
import com.example.projectsleader.user
import com.example.projectsleader.viewmodels.CompanyViewModel
import kotlinx.android.synthetic.main.add_project_dialog.view.*
import kotlinx.android.synthetic.main.fragment_projects.*
import kotlinx.android.synthetic.main.recycler_dialog.view.*

class ProjectsFragment : Fragment(), MavericksView {
    private val viewModel: CompanyViewModel by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun invalidate() = withState(viewModel) { state ->
        projectsRecycler.withModels {
            state.projects.forEach { project ->
                carousel{
                    models()
                }
                project {
                    id(project.id)
                    if (state.userCompany?.creator == true) {
                        visibility(View.VISIBLE)
                        onDeleteBtnClick(View.OnClickListener {
                            viewModel.deleteProject(project) {
                                Toast.makeText(
                                    requireContext(),
                                    "Проект ${project.title} успешно ликвидирован!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    } else visibility(View.GONE)
                    title(project.title)
                    onCardClick(View.OnClickListener { onCardClickListener(project) })
                }
            }
            if (state.userCompany?.creator == true)
                addButton {
                    id("addProjectBtn")
                    click(View.OnClickListener { onAddProjectBtnClick() })
                }
        }
    }

    private fun onAddProjectBtnClick() {
        val addProjectView =
            layoutInflater.inflate(R.layout.add_project_dialog, null)
        AlertDialog.Builder(requireContext())
            .setTitle("Создать проект")
            .setView(addProjectView)
            .setCancelable(false)
            .setNegativeButton("Отмена") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Создание проекта отменено",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setPositiveButton("Создать") { _, _ ->
                val title = addProjectView.addProjectTitle.text.toString()
                if (title.isNotBlank())
                    viewModel.createProject(title) {
                        Toast.makeText(
                            requireContext(),
                            "Проект $title успешно создан!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                else Toast.makeText(
                    requireContext(),
                    "Поле должно быть заполнено",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
    }

    private fun onCardClickListener(project: Project) {
        val usersView =
            layoutInflater.inflate(R.layout.recycler_dialog, null)
        if (project.leader != null)
            DataBase.getUser(project.leader) { leader ->
                usersView.recycler.withModels {
                    user {
                        id(leader.uid)
                        email(leader.email)
                        login(leader.login)
                        isLeader(true)
                    }
                    project.users.forEach {
                        user {
                            id(it.key)
                            email(it.value)
                            isLeader(false)
                        }
                    }
                }
            }
        else usersView.recycler.withModels {
            project.users.forEach {
                user {
                    id(it.key)
                    email(it.value)
                    isLeader(false)
                }
            }
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Участники проекта")
            .setView(usersView)
            .setCancelable(true)
            .setNeutralButton("Ок") { _, _ -> }
            .show()
    }
}