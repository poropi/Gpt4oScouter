package dev.poropi.gpt4oscouter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.poropi.gpt4oscouter.service.web.interceptor.CurlHttpLoggingInterceptor
import dev.poropi.gpt4oscouter.repository.OpenAiRepository
import dev.poropi.gpt4oscouter.repository.OpenAiRepositoryImpl
import dev.poropi.gpt4oscouter.service.web.OpenAiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * MainAppModule
 * Module for providing dependencies
 * @see Module
 * @see InstallIn
 */
@InstallIn(SingletonComponent::class)
@Module
class MainAppModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(CurlHttpLoggingInterceptor())
            .addInterceptor(createHttpLoggingInterceptor())
            .addInterceptor { chain ->
                val original = chain.request()
                val authorization = "Bearer ${BuildConfig.API_KEY}"
                val request = original.newBuilder()
                    .addHeader("Authorization", authorization)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    fun provideApiClient(okHttpClient: OkHttpClient): OpenAiService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenAiService::class.java)
    }

    @Provides
    fun provideApiOpenAiRepository(openAiService: OpenAiService): OpenAiRepository {
        return OpenAiRepositoryImpl(openAiService)
    }

    private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message -> Timber.d("Retrofit: %s", message) }
        logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return logging
    }
}