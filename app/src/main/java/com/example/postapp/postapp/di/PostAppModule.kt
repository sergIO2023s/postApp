package com.example.postapp.postapp.di

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.example.postapp.data.db.MainDataBase
import com.example.postapp.data.remote.JsonPlaceholderApiClient
import com.example.postapp.data.repositories.Repository
import com.example.postapp.domain.*
import com.example.postapp.domain.interfaces.IPostsRepository
import com.example.postapp.postapp.activities.PREFERENCES_KEY
import com.example.postapp.viewmodels.PostDetailsViewModel
import com.example.postapp.viewmodels.PostListViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val DB_NAME = "dbpostapp"
//This should be depending on flavors or compilation versions
const val BASE_URL = "https://jsonplaceholder.typicode.com/"
val postAppModules = module {
    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(get()))
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .build()
            .create(JsonPlaceholderApiClient::class.java)
    }
    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setLenient().create()
    }
    single { Room.databaseBuilder(get(), MainDataBase::class.java, DB_NAME)
        .fallbackToDestructiveMigration()
        .build() }
    single { get<MainDataBase>().postDao() }
    single { get<MainDataBase>().userDao() }
    single { get<MainDataBase>().commentDao() }
    single { GetPostsUseCase(get()) }
    single { DeleteAllUseCase(get()) }
    single { SetFavoriteUseCase(get()) }
    single { GetPostDetailsUseCase(get()) }
    single { GetPostCommentsUseCase(get()) }
    single { GetLatestPostUseCase(get()) }
    single { DeletePostById(get()) }
    single { Repository(get(), get(), get(), get(), get()) as IPostsRepository }

    single{ getSharedPrefs(androidApplication()) }
    viewModel { PostListViewModel(get(), get(), get(), get(), get()) }
    viewModel { PostDetailsViewModel(get(), get()) }
}

    private fun getSharedPrefs(androidApplication: Application): SharedPreferences{
        return  androidApplication.getSharedPreferences(PREFERENCES_KEY,  android.content.Context.MODE_PRIVATE)
    }