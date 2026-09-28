package com.aitaskorganizer.app.di

import android.content.Context
import android.content.SharedPreferences
import com.aitaskorganizer.app.AITaskOrganizerApplication
import com.aitaskorganizer.app.data.remote.api.SourceApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val PREFS_NAME = "ai_task_organizer_prefs"
    private const val KEY_BACKEND_URL = "backend_url"
    private const val DEFAULT_BACKEND_URL = "http://10.0.2.2:8000"

    private val prefs: SharedPreferences
        get() = AITaskOrganizerApplication.appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBackendUrl(): String {
        val saved = prefs.getString(KEY_BACKEND_URL, DEFAULT_BACKEND_URL)?.trim().orEmpty()
        return if (saved.isBlank()) DEFAULT_BACKEND_URL else saved.trimEnd('/')
    }

    fun setBackendUrl(url: String) {
        val sanitized = url.trim().ifBlank { DEFAULT_BACKEND_URL }
        prefs.edit().putString(KEY_BACKEND_URL, sanitized.trimEnd('/')).apply()
        refreshRetrofit()
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private var retrofit: Retrofit = buildRetrofit()
    var sourceApi: SourceApi = retrofit.create(SourceApi::class.java)
        private set

    private fun buildRetrofit(): Retrofit {
        val backendUrl = getBackendUrl()
        val baseUrl = if (backendUrl.endsWith("/")) backendUrl else "$backendUrl/"
        return Retrofit.Builder()
            .baseUrl("${baseUrl}api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun refreshRetrofit() {
        retrofit = buildRetrofit()
        sourceApi = retrofit.create(SourceApi::class.java)
    }
}
