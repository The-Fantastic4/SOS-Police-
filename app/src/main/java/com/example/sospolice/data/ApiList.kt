package com.example.sospolice.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiList {

    @POST("/register_police_station")
    fun signUp(@Body signupData: SignUpData): retrofit2.Call<SignupResponse>

    @POST("/notifyPolice/{id}/{firstName}/{phone_number}/{latt}/{long}")
    fun sendNotification(@Query("id") Id: String,
                         @Path("firstName") firstName: String,
                         @Path("phone_number") phone_number: String,
                         @Path("latt") latt: Double,
                         @Path("long") long: Double
    )
    : retrofit2.Call<NotificationResponse>

    @POST("/update_police_station/{id}/{device_token}")
    fun sendFCM(
        @Path("id") userId: String,
        @Path("device_token") token: String
    ): retrofit2.Call<SignupResponse>


    companion object {

        private const val BASE_URL = "https://sos-service.onrender.com"

        fun create(): ApiList {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(ApiWorker.client)
                .build()
            return retrofit.create(ApiList::class.java)
        }
    }
}