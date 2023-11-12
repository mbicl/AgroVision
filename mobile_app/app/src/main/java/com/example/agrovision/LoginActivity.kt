package com.example.agrovision

import android.content.Context
import android.content.Intent
import android.content.RestrictionsManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.agrovision.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val BASE_URL = "https://b9da-84-54-75-42.ngrok-free.app/api/v1/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.btnSubmit.setOnClickListener {

            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
//            getToken("binding.etLogin.text.toString()")

        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        postDataUsingRetrofit(this,"9875412")


    }


    private fun postDataUsingRetrofit(
        ctx: Context,
        number: String
    ) {


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of your ApiService interface
        val apiService = retrofit.create(ApiServes::class.java)

        // Create an instance of YourRequestModel with the data you want to send
        val requestModel = YourRequestModel("value1")

        // Make the POST request
        val call = apiService.postData(requestModel)
        call!!.enqueue(object : Callback<YourResponseModel> {
            override fun onResponse(
                call: Call<YourResponseModel>,
                response: Response<YourResponseModel>
            ) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val responseBody = response.body()

//                    Toast.makeText(this@LoginActivity,"${responseBody!!.token}",Toast.LENGTH_LONG).show()
                    // Do something with the response data
                } else {
                    // Handle error response
                    // You can parse the error response from the server here
                }
            }

            override fun onFailure(call: Call<YourResponseModel>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
            }
        })

    }
    }



