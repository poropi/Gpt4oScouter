package dev.poropi.gpt4oscouter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * アプリケーションのメインアプリケーションクラス。
 *
 * このクラスは、アプリケーションのライフサイクルを管理します。
 * Hiltのアノテーションが付けられており、依存関係の注入を行います。
 * デバッグビルド時には、Timberのデバッグツリーをプラントします。
 */
@HiltAndroidApp
class MainApplication: Application() {

    /**
     * アプリケーションの作成時に呼び出されます。
     *
     * このメソッドでは、デバッグビルド時にTimberのデバッグツリーをプラントします。
     */
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}