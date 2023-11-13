package com.example.sospolice

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.sospolice.data.ApiList
import com.example.sospolice.databinding.ActivityMainBinding
import com.example.sospolice.repository.SignupRepository
import com.example.sospolice.viewModel.BaseViewModelFactory
import com.example.sospolice.viewModel.LoginViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelFactory: BaseViewModelFactory
    private var apilist =  ApiList.create()
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signupRepo = SignupRepository(apilist)
        viewModelFactory = BaseViewModelFactory(application,signupRepo)
        loginViewModel = ViewModelProvider(this,viewModelFactory)[LoginViewModel::class.java]

        loginViewModel.signupResult.observe(this) { Resource ->
            if(Resource.isLoading()){
                binding.loading.visibility = View.VISIBLE
            }
            else if (Resource.isSuccess()) {
                val userId = Resource.data?.station?._id
                if (userId != null) {
                    Log.d("USERID",userId)
                }
                binding.loading.visibility = View.GONE

                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(ContentValues.TAG, "get token failed", task.exception)
                            return@addOnCompleteListener
                        }
                        // Get new Instance ID token
                        val token = task.result
                        Log.d(ContentValues.TAG, token)
                        if (userId != null) {
                            loginViewModel.updateFCM(userId,token)
                        }
                    }
                val o = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(o)
                finish()
            }else if (Resource.isError()){
                binding.loading.visibility = View.GONE
                Toast.makeText(applicationContext,"Registering Failed", Toast.LENGTH_LONG).show()
            }
        }

        binding.button.setOnClickListener {
            launch {
                signup()
            }
        }

    }
    private fun signup (){
        if (validation()) {
            val stationName = binding.stationName.text.toString().trim()
            val long = binding.longitude.text.toString().toDouble()
            val latt = binding.latitude.text.toString().toDouble()
            val city = binding.city.text.toString().trim()
            val json = JSONObject()
            json.put("station_Name", stationName)
            json.put("longitude", long)
            json.put("lattitude", latt)
            json.put("city", city)
            loginViewModel.signup(stationName,long,latt,city)

        }
    }

    private fun validation(): Boolean {
        var value = true

        val stationName = binding.stationName.text.toString().trim()
        val latt = binding.latitude.text.toString().trim()
        val long = binding.longitude.text.toString().trim()
        val city = binding.city.text.toString().trim()
        if (stationName.isEmpty()) {
            binding.stationName.error = "Station Name required"
            binding.stationName.requestFocus()
            value = false
        }

        if (latt.isEmpty()) {
            binding.latitude.error = "Latitude required"
            binding.latitude.requestFocus()
            value = false
        }

        if (long.isEmpty()) {
            binding.longitude.error = "Longitude required"
            binding.longitude.requestFocus()
            value = false
        }
        if (city.isEmpty()) {
            binding.city.error = "City required"
            binding.city.requestFocus()
            value = false
        }
        return value
    }

}