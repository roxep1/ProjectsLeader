package com.example.projectsleader.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectsleader.R
import com.example.projectsleader.models.User
import com.example.projectsleader.objects.CurrentUser
import com.example.projectsleader.objects.DataBase.isEmailValid
import com.example.projectsleader.objects.DataBase.isPasswordOrLoginValid
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_auth.*


class AuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginBtn.setOnClickListener {
            loginUser()
        }
        regBtn.setOnClickListener {
            regUser()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        //TODO()
        if (CurrentUser.currentUser != null)
            findNavController().navigate(R.id.action_authFragment_to_mainFragment)

    }

    private fun loginUser() {
        if (emailEdt.isEmailValid() && passwordEdt.isPasswordOrLoginValid()) {
            val email = emailEdt.text.toString()
            val password = passwordEdt.text.toString()
            CurrentUser.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_authFragment_to_mainFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка: ${task.exception!!.message.toString()}",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Данные введены некорректно", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun regUser() {
        if (loginEdt.isPasswordOrLoginValid() && emailEdt.isEmailValid() && passwordEdt.isPasswordOrLoginValid()) {
            val email = emailEdt.text.toString()
            val password = passwordEdt.text.toString()
            val login = loginEdt.text.toString()
            CurrentUser.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CurrentUser.refUser.setValue(User(CurrentUser.uid, login, email))
                            .addOnSuccessListener {
                                findNavController().navigate(R.id.action_authFragment_to_mainFragment)
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Ошибка: ${it.message}",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка: ${task.exception!!.message.toString()}",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Данные введены некорректно", Toast.LENGTH_SHORT)
                .show()
        }
    }
}