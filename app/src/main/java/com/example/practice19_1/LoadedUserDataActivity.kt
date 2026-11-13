package com.example.practice19_1

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LoadedUserDataActivity : AppCompatActivity() {

    private var mPlayer: MediaPlayer? = null
    private lateinit var imageView: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private val goToMainRunnable = Runnable {
        android.util.Log.e("AUDIO", "Переход на MainActivity")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loaded_user_data)

        android.util.Log.e("AUDIO", "LoadedUserDataActivity.onCreate запущен")

        imageView = findViewById(R.id.imageForLoaded)
        initAnimation()
        initMediaPlayer()

        // 10 секунд вместо 3 — для диагностики (потом верните 3000)
        handler.postDelayed(goToMainRunnable, 10000)
    }

    private fun initAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_rotate_animation)
        imageView.startAnimation(animation)
        android.util.Log.e("AUDIO", "Анимация запущена")
    }

    private fun initMediaPlayer() {
        try {
            android.util.Log.e("AUDIO", "Попытка создать MediaPlayer из R.raw.noise")
            mPlayer = MediaPlayer.create(this, R.raw.noise)

            if (mPlayer == null) {
                android.util.Log.e("AUDIO", "ОШИБКА: MediaPlayer.create вернул null — файл не найден или битый")
                return
            }

            android.util.Log.e("AUDIO", "Файл найден. Длительность = ${mPlayer?.duration} мс")
            android.util.Log.e("AUDIO", "Громкость до установки: ${mPlayer?.duration}")

            mPlayer?.setVolume(1.0f, 1.0f) // 100% громкость
            mPlayer?.isLooping = true
            mPlayer?.start()

            android.util.Log.e("AUDIO", "Звук запущен. isPlaying = ${mPlayer?.isPlaying}")
            android.util.Log.e("AUDIO", "isLooping = ${mPlayer?.isLooping}")

        } catch (e: Exception) {
            android.util.Log.e("AUDIO", "ИСКЛЮЧЕНИЕ при запуске: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.e("AUDIO", "onPause вызван")
        if (mPlayer?.isPlaying == true) {
            mPlayer?.pause()
            android.util.Log.e("AUDIO", "Звук поставлен на паузу")
        }
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.e("AUDIO", "onResume вызван")
        if (mPlayer != null && mPlayer?.isPlaying == false) {
            mPlayer?.start()
            android.util.Log.e("AUDIO", "Звук возобновлён")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.e("AUDIO", "onDestroy вызван")
        handler.removeCallbacks(goToMainRunnable)
        if (mPlayer != null) {
            if (mPlayer!!.isPlaying) mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
            android.util.Log.e("AUDIO", "MediaPlayer освобождён")
        }
    }
}