package com.example.projectsleader.fragments.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.projectsleader.R
import com.example.projectsleader.user
import com.example.projectsleader.viewmodels.CompanyViewModel
import kotlinx.android.synthetic.main.fragment_active_tasks.*

class ActiveTasksFragment : Fragment(), MavericksView {
    private val viewModel: CompanyViewModel by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active_tasks, container, false)
    }

    override fun invalidate() = withState(viewModel) { state ->
        activeTasksRecycler.withModels {
            state.userCompany?.projects?.values?.forEach { project ->
                project.tasks.forEach { task ->
                    user {
                        id(task.key)
                        email(task.value)
                        isLeader(false)
                        onCardClick(View.OnClickListener { showDialogTask(task.key) })
                    }
                }
            }
        }
    }

    private fun showDialogTask(id: String) =
        viewModel.getTask(id) { task ->
            AlertDialog.Builder(requireContext())
                .setTitle(task.title)
                .setMessage("${task.desc}\nСрок выполнения: ${task.date}")
                .setCancelable(true)
                .setPositiveButton("Выполнено") { _, _ ->
                    viewModel.deleteTask(id) {
                        Toast.makeText(
                            requireContext(),
                            "Задание выполнено!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNeutralButton("Ok") { _, _ ->
                }
                .show()
        }
}