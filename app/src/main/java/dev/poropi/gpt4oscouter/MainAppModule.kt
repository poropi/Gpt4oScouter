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
 *
 * このモジュールは、アプリケーション全体で使用する依存関係を提供します。
 * Hiltのアノテーションが付けられており、依存関係の注入を行います。
 */
@InstallIn(SingletonComponent::class)
@Module
class MainAppModule {

    /**
     * OkHttpClientの提供
     *
     * この関数は、アプリケーション全体で使用するOkHttpClientを提供します。
     * ネットワークインターセプター、HTTPログインターセプター、タイムアウト設定が含まれています。
     *
     * @return OkHttpClientのインスタンス。
     */
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

    /**
     * OpenAiServiceの提供
     *
     * この関数は、アプリケーション全体で使用するOpenAiServiceを提供します。
     * Retrofitを使用してAPIクライアントを作成します。
     *
     * @param okHttpClient OkHttpClientのインスタンス。
     * @return OpenAiServiceのインスタンス。
     */
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

    /**
     * OpenAiRepositoryの提供
     *
     * この関数は、アプリケーション全体で使用するOpenAiRepositoryを提供します。
     *
     * @param openAiService OpenAiServiceのインスタンス。
     * @return OpenAiRepositoryのインスタンス。
     */
    @Provides
    fun provideApiOpenAiRepository(openAiService: OpenAiService): OpenAiRepository {
        return OpenAiRepositoryImpl(openAiService)
    }

    /**
     * HttpLoggingInterceptorの作成
     *
     * この関数は、HTTPログインターセプターを作成します。
     * デバッグビルド時には、ログレベルをBODYに設定します。
     *
     * @return HttpLoggingInterceptorのインスタンス。
     */
    private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message -> Timber.d("Retrofit: %s", message) }
        logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return logging
    }
}