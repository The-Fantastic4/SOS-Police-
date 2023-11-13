package com.example.sospolice.repository

import com.example.sospolice.data.ApiList
import com.example.sospolice.data.SignUpData
import com.example.sospolice.data.SignupResponse
import retrofit2.Call

class SignupRepository(private val apiList: ApiList) {

    fun signUp(signupData: SignUpData) : Call<SignupResponse> {
        return apiList.signUp(signupData)
    }

    fun sendFCM(userId:String,token:String):Call<SignupResponse>{
        return apiList.sendFCM(userId,token)
    }
}