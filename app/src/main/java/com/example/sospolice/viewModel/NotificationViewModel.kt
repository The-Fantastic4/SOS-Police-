package com.example.sospolice.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sospolice.data.NotificationResponse
import com.example.sospolice.repository.NotificationRepository
import com.example.sospolice.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationViewModel (private val userRepo: NotificationRepository) : ViewModel() {
    private val _notify = MutableLiveData<Resource<NotificationResponse>>()
    val notify: LiveData<Resource<NotificationResponse>> get()  = _notify

    fun notify(id:String, firstName:String, phone_number:String, latt:Double,long:Double){
        viewModelScope.launch {
            try {
                userRepo.sendNotification(id,firstName,phone_number,latt,long).enqueue(object :
                    Callback<NotificationResponse> {
                    override fun onResponse(
                        call: Call<NotificationResponse>,
                        response: Response<NotificationResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()!!
                            _notify.value = Resource.success(data)
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: response.message()
                            _notify.value = Resource.error( errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                        _notify.value = t.message?.let { Resource.error(it) }
                        t.printStackTrace()
                    }

                })
            }catch (e:Exception){
                print(e)
                Log.i("NotificationViewModel", "Something went wrong!")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("NotificationViewModel", "NotificationViewModel destroyed!")
    }

}