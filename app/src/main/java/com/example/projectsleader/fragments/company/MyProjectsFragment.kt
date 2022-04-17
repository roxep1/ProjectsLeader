package com.example.projectsleader.fragments.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.projectsleader.R
import com.example.projectsleader.models.Project
import com.example.projectsleader.models.UserProject
import com.example.projectsleader.objects.DataBase
import com.example.projectsleader.project
import com.example.projectsleader.viewmodels.CompanyViewModel
import kotlinx.android.synthetic.main.fragment_my_projects.*


class MyProjectsFragment : Fragment(), MavericksView {
    private val viewModel: CompanyViewModel by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_projects, container, false)
    }

    override fun invalidate() = withState(viewModel) { state ->
        myProjectsRecycler.withModels {
            if (state.userCompany?.creator == true)
                state.projects.forEach { project ->
                    project {
                        id(project.id)
                        title(project.title)
                        visibility(View.GONE)
                        onCardClick(View.OnClickListener { onClick(project) })
                    }
                }
            else
                state.userCompany?.projects?.values?.forEach { project ->
                    project {
                        id(project.id)
                        title(project.title)
                        visibility(View.GONE)
                        onCardClick(View.OnClickListener { onClick(project) })
                    }
                }

        }
    }

    private fun onClick(project: Project) =
        findNavController().navigate(
            MyProjectsFragmentDirections.actionMyProjectsToDetailProjectFragment(project.id!!)
        )


    private fun onClick(project: UserProject) = DataBase.getProject(project.id!!) { onClick(it) }

}