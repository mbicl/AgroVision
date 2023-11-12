package com.example.agrovision
//https://b9da-84-54-75-42.ngrok-free.app/api/v1/login/


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServes {
    @POST("login/") // Replace "your_endpoint" with the actual endpoint
    fun postData(@Body requestModel: YourRequestModel): Call<YourResponseModel>?

    @POST("upload/") // Replace "your_endpoint" with the actual endpoint
    fun postDataUpload(@Body requestModel: uploadPost): Call<YourResponseModel>?

}
