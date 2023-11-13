package com.example.sospolice.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sospolice.repository.SignupRepository

@Suppress("UNCHECKED_CAST")
class BaseViewModelFactory constructor(private val application: Application, private val repo: SignupRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(this.repo, application) as T
            }
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}