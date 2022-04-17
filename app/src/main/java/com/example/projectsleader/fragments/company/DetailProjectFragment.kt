package com.example.projectsleader.fragments.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.projectsleader.R
import com.example.projectsleader.bigText
import com.example.projectsleader.models.User
import com.example.projectsleader.objects.CurrentUser
import com.example.projectsleader.objects.DataBase
import com.example.projectsleader.user
import com.example.projectsleader.viewmodels.CompanyViewModel
import kotlinx.android.synthetic.main.add_task_dialog.view.*
import kotlinx.android.synthetic.main.fragment_detail_project.*
import java.util.*


class DetailProjectFragment : Fragment(), MavericksView {
    private val viewModel: CompanyViewModel by fragmentViewModel()
    private val args: DetailProjectFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setProjectListener(args.projectId)
    }

    override fun invalidate() = withState(viewModel) { state ->
        fun addWithOrWithoutLeader(epoxy: EpoxyController, leader: User? = null) {
            state.currentProject!!.users.forEach { user ->
                epoxy.user {
                    id(user.key)
                    isLeader(false)
                    email(user.value)
                    if (leader?.uid == CurrentUser.uid)
                        onCardClick(View.OnClickListener { showAddTaskDialog(user.key) })
                    else if (state.userCompany != null && state.userCompany.creator!!)
                        onCardClick(View.OnClickListener { showSelectLeaderDialog(user.key) })
                }
            }
            if (state.userCompany != null && state.userCompany.creator != true) {
                val userProject =
                    state.userCompany.projects[state.currentProject.id!!]!!
                val tasks = userProject.tasks
                if (tasks.isNotEmpty()) {
                    epoxy.bigText {
                        id("yourTasks")
                        text("Ваши задания в этом проекте")
                    }
                    tasks.forEach { task ->
                        epoxy.user {
                            id(task.key)
                            email(task.value)
                            isLeader(false)
                        }
                    }
                }
                if (leader?.uid == CurrentUser.uid && state.currentProject.tasks.isNotEmpty()) {
                    epoxy.bigText {
                        id("allTasks")
                        text("Все задания в этом проекте")
                    }
                    state.currentProject.tasks.forEach { task ->
                        epoxy.user {
                            id(task.key)
                            email(task.value.title)
                            login(task.value.date)
                            isLeader(false)
                        }
                    }
                }
            }
        }

        if (state.currentProject != null) {
            if (state.currentProject.leader != null)
                DataBase.getUser(state.currentProject.leader) { leader ->
                    detailProjectRecycler.withModels {
                        user {
                            id(leader.uid)
                            login(leader.login)
                            email(leader.email)
                            isLeader(true)
                        }
                        addWithOrWithoutLeader(this, leader)
                    }
                }
            detailProjectRecycler.withModels {
                addWithOrWithoutLeader(this)
            }
        }
    }


    private fun showAddTaskDialog(uid: String) {
        val addTaskDialog =
            layoutInflater.inflate(R.layout.add_task_dialog, null)
        val calendar = Calendar.getInstance()
        var date = "${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH) + 1}.${calendar.get(Calendar.YEAR)}"
        addTaskDialog.addTaskDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { view, year, monthOfYear, dayOfMonth ->
            date = "$dayOfMonth.${monthOfYear + 1}.$year"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Назначить задание пользователю")
            .setView(addTaskDialog)
            .setCancelable(false)
            .setNegativeButton("Отмена") { _, _ ->
            }
            .setPositiveButton("Создать") { _, _ ->
                val title = addTaskDialog.addTaskTitle.text.toString()
                val desc = addTaskDialog.addTaskDesc.text.toString()

                if (title.isNotBlank() && desc.isNotBlank() && date.isNotBlank()) {
                    viewModel.addTask(uid, title, desc, date) {
                        Toast.makeText(
                            requireContext(),
                            "Задание успешно добавлено!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else Toast.makeText(
                    requireContext(),
                    "Все поля должны быть заполнены",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
    }

    private fun showSelectLeaderDialog(uid: String) {
        AlertDialog.Builder(requireContext())
            .setMessage("Назначить нового руководителя проекта?")
            .setCancelable(false)
            .setPositiveButton("Назначить") { _, _ ->
                viewModel.setNewLeader(uid) {
                    Toast.makeText(
                        requireContext(),
                        "Новый руководитель проекта успешно назначен!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Отмена"){_,_ -> }
            .show()
    }
}