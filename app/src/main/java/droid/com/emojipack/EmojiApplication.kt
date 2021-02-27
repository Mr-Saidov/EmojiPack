package droid.com.emojipack

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatDelegate
import droid.com.emoji.EmojiManager
import droid.com.emoji.GoogleEmojiProvider


/**
 *  Created by Dilshodbek on 20:17 25-February 2021
 *  DataMicron
 */

class EmojiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EmojiManager.install(GoogleEmojiProvider())
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectAll().build()
            )
            StrictMode.setVmPolicy(VmPolicy.Builder().detectAll().build())
        }
    }
}
