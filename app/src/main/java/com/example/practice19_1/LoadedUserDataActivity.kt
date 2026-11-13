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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loaded_user_data)

        imageView = findViewById(R.id.imageForLoaded)
        initAnimation()
        initMediaPlayer()

        // Через 3 секунды переходим на главный экран
        handler.postDelayed(goToMainRunnable, 3000)
    }

    private fun initAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_rotate_animation)
        imageView.startAnimation(animation)
    }

    private fun initMediaPlayer() {
        // Если файла noise.mp3 нет — просто не запускаем музыку, чтобы не крашилось
        try {
            mPlayer = MediaPlayer.create(this, R.raw.noise)
            mPlayer?.setVolume(0.2f, 0.2f)
            mPlayer?.isLooping = true
            mPlayer?.start()
        } catch (e: Exception) {
            android.util.Log.e("AUDIO", "Ошибка запуска звука: ${e.message}")
        }
    }

    override fun onPause() {
        super.onPause()
        if (mPlayer?.isPlaying == true) mPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (mPlayer != null && mPlayer?.isPlaying == false) mPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Убираем таймер, чтобы не запускался, если пользователь закрыл экран раньше
        handler.removeCallbacks(goToMainRunnable)
        if (mPlayer != null) {
            if (mPlayer!!.isPlaying) mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
        }
    }
}