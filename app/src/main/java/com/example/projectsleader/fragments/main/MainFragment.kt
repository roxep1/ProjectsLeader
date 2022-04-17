package com.example.projectsleader.fragments.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.projectsleader.R
import com.example.projectsleader.addButton
import com.example.projectsleader.company
import com.example.projectsleader.objects.CurrentUser
import com.example.projectsleader.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.enter_company_dialog.view.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), MavericksView {
    private val viewModel: MainViewModel by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun invalidate() =
        withState(viewModel) { state ->
            companiesRecycler.withModels {
                state.companies?.forEach { userCompany ->
                    company {
                        id(userCompany.id)
                        company(userCompany.name)
                        onCardClick(View.OnClickListener {
                            CurrentUser.currentCompanyId = userCompany.id
                            findNavController().navigate(R.id.action_mainFragment_to_companyActivity)
                        })
                    }
                }
                addButton {
                    id("addButton")
                    click(View.OnClickListener {
                        showDialogCreateOrEnter()
                    })
                }
            }
        }

    private fun showDialogCreateOrEnter() {
        AlertDialog.Builder(requireContext())
            .setMessage("Вы хотите войти в организацию или создать новую?")
            .setCancelable(false)
            .setPositiveButton("Войти") { _, _ ->
               showDialogEnter()
            }
            .setNeutralButton("Создать") { _, _ ->
                findNavController().navigate(R.id.action_mainFragment_to_createCompanyFragment)
            }
            .show()
    }

    private fun showDialogEnter(){
        val enterCompanyView =
            layoutInflater.inflate(R.layout.enter_company_dialog, null)
        AlertDialog.Builder(requireContext())
            .setTitle("Войти в компанию")
            .setView(enterCompanyView)
            .setCancelable(false)
            .setNegativeButton("Отмена") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Вход в компанию отменен",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setPositiveButton("Войти") { _, _ ->
                val id = enterCompanyView.companyId.text.toString()
                val password =
                    enterCompanyView.companyPassword.text.toString()
                if (id.isNotBlank() && password.isNotBlank()) {
                    viewModel.enterCompany(id, password, requireContext())
                } else Toast.makeText(
                    requireContext(),
                    "Все поля должны быть заполнены",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
    }
}