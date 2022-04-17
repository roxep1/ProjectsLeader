package com.example.projectsleader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_company.*

class CompanyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company)

        val navController = Navigation.findNavController(this, R.id.company_nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}