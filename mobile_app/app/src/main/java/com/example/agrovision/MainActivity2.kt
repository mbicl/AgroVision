package com.example.agrovision

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.agrovision.databinding.ActivityMain2Binding
import com.example.agrovision.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Url

class MainActivity2 : AppCompatActivity() {

    private val BASE_URL = "https://b9da-84-54-75-42.ngrok-free.app/api/v1/"

    private lateinit var binding:ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)

//        "token": "g5a2D1y1j6R0C8I0q5j9"

        val profileName=intent.getStringExtra("Username")
        val name=intent.getStringExtra("name")
        postDataUsingRetrofit(this,"931311480", name!!, profileName!!)




    }

    private fun postDataUsingRetrofit(
        ctx: Context,
        number: String
        ,name:String,
        url:String
    ) {


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of your ApiService interface
        val apiService = retrofit.create(ApiServes::class.java)

        // Create an instance of YourRequestModel with the data you want to send
        val requestModel = uploadPost(name,"931311480",url,"41.3500932","69.2083192")
        // Make the POST request
        val call = apiService.postDataUpload(requestModel)
        call!!.enqueue(object : Callback<YourResponseModel> {
            override fun onResponse(
                call: Call<YourResponseModel>,
                response: Response<YourResponseModel>
            ) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val responseBody = response.body()
//                    Glide.with(this@MainActivity2).load(responseBody).into(binding.ivImage1)
//                    Glide.with(this@MainActivity2).load(responseBody).into(binding.ivImage2)

                    val imageByteArray: String = responseBody!!.img1// Your byte array containing JPEG data
                    val image2:String = responseBody!!.img2
                    // Convert byte array to Bitma



//                    binding.ivImage2.setImageResource()

                    val resourceId = resources.getIdentifier(imageByteArray, "drawable", packageName)
                    val resourceId2 = resources.getIdentifier(image2, "drawable", packageName)


                    Glide.with(this@MainActivity2).load(resourceId).into(binding.ivImage1)
                    Glide.with(this@MainActivity2).load(resourceId2).into(binding.ivImage2)


                    binding.tvKasallik.text=responseBody!!.name

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