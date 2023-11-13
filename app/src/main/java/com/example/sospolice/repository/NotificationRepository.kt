package com.example.sospolice.repository

import com.example.sospolice.data.ApiList
import com.example.sospolice.data.NotificationResponse
import retrofit2.Call

class NotificationRepository(private val apilist: ApiList) {
    fun sendNotification(id:String, firstName:String, phone_number:String, latt: Double, long: Double): Call<NotificationResponse> {
        return apilist.sendNotification(id,firstName,phone_number,latt,long)
    }
}