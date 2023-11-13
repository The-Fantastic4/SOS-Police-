package com.example.sospolice.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sospolice.data.SignUpData
import com.example.sospolice.data.SignupResponse
import com.example.sospolice.repository.SignupRepository
import com.example.sospolice.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val signupRepository: SignupRepository,application: Application) : AndroidViewModel(application) {
    private val _signupResult = MutableLiveData<Resource<SignupResponse>>()
    val signupResult: LiveData<Resource<SignupResponse>> get() = _signupResult

    fun signup(station_name: String, longitude: Double, latitude: Double, city: String) {
        _signupResult.value = Resource.loading()
        viewModelScope.launch {
            try {
                val regData = SignUpData(station_name, longitude, latitude, city)
                signupRepository.signUp(regData).enqueue(object : Callback<SignupResponse> {
                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>
                    ) {
                        if (response.isSuccessful) {
                            val user = response.body()!!
                            Log.i("RESPONSE", user.toString())
                            _signupResult.value = Resource.success(user)
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: response.message()
                            _signupResult.value = Resource.error(errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        _signupResult.value = t.message?.let { Resource.error(it) }
                    }
                })
            } catch (e: Exception) {
                Log.i("LoginViewModel", "Signup,something went wrong!")
                print(e)
            }
        }
    }


    fun updateFCM(userId: String, token: String) {
        viewModelScope.launch {
            try {
                signupRepository.sendFCM(userId, token).enqueue(object : Callback<SignupResponse> {
                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>
                    ) {
                        if (response.isSuccessful) {
                            val station = response.body()!!
                            Log.i("RESPONSE", station.toString())
                            _signupResult.value = Resource.success(station)
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: response.message()
                            _signupResult.value = Resource.error(errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        _signupResult.value = t.message?.let { Resource.error(it) }
                    }

                })
            } catch (e: Exception) {
                Log.i("LoginViewModel", " FCM,something went wrong!")
                print(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("LoginViewModel", "LoginViewModel destroyed!")
    }
}