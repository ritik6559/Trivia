package com.example.trivia.network

import com.example.trivia.model.Question
import retrofit2.http.GET

interface QuestionApi {
    //we can consider it as a dao.
    @GET("world.json")//we removed world.json from the baseurl because this get function will append this to the end of the baseurl.
    suspend fun getAllQuestions(): Question
    // since it is an interface we don't need to create the function body.
}