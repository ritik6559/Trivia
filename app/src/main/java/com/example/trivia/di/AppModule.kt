package com.example.trivia.di

import com.example.trivia.network.QuestionApi
import com.example.trivia.repository.QuestionRepository
import com.example.trivia.util.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQuestionApi(): QuestionApi{
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            //TO CONVERT THE CONTENT INTO OBJECT THAT WE CAN USE HERE WE USE .addConverterFactory(
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionApi::class.java)
    }


    @Singleton
    @Provides
    suspend fun provideQuestionRepository(api: QuestionApi) = QuestionRepository(api)
}