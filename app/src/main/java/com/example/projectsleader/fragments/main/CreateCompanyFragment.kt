package com.example.projectsleader.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.example.projectsleader.R
import com.example.projectsleader.objects.DataBase.isPasswordOrLoginValid
import com.example.projectsleader.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_create_company.*

class CreateCompanyFragment : Fragment(), MavericksView {
    private val viewModel: MainViewModel by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_company, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createCompanyBtn.setOnClickListener {
            val name = companyNameEdt.text.toString()
            if (name.isNotBlank() && uniqueIdEdt.isPasswordOrLoginValid() && companyPasswordEdt.isPasswordOrLoginValid()) {
                val password = companyPasswordEdt.text.toString()
                val id = uniqueIdEdt.text.toString()
                viewModel.createCompany(name, id, password) {
                    if (it) {
                        findNavController().navigate(R.id.action_createCompanyFragment_to_mainFragment)
                        Toast.makeText(
                            requireContext(),
                            "Компания $name успешно создана!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else Toast.makeText(
                        requireContext(),
                        "Компания с идентификатором $id уже существует или имеются проблемы с соединением.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else Toast.makeText(
                requireContext(),
                "Все поля должны быть заполнены, идентификатор и пароль должны содержать не менее 6 символов.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun invalidate() {

    }

}