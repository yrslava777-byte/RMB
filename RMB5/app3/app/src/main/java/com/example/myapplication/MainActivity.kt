package com.example.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.slider.Slider
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var chipGroupModes: ChipGroup
    private lateinit var textViewTimer: TextView
    private lateinit var sliderMinutes: Slider
    private lateinit var textViewDuration: TextView
    private lateinit var progressCircular: CircularProgressIndicator
    private lateinit var buttonStart: MaterialButton
    private lateinit var buttonPause: MaterialButton
    private lateinit var buttonReset: MaterialButton

    private var countDownTimer: CountDownTimer? = null
    private var initialDurationMillis: Long = TimeUnit.MINUTES.toMillis(20)
    private var timeLeftMillis: Long = initialDurationMillis
    private var isRunning: Boolean = false
    private var isPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        applyWindowInsets()
        bindViews()
        setupListeners()
        renderInitialState()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun bindViews() {

        chipGroupModes = findViewById(R.id.chip_group_modes)
        textViewTimer = findViewById(R.id.text_view_timer)
        sliderMinutes = findViewById(R.id.slider_duration)
        textViewDuration = findViewById(R.id.text_view_duration)
        buttonStart = findViewById(R.id.button_start)
        buttonReset = findViewById(R.id.button_reset)
        buttonPause = findViewById(R.id.button_pause)
        progressCircular = findViewById(R.id.progress_circular)
    }

    private fun setupListeners() {
        setupChipGroupListener()
        setupSliderListener()
        setupButtonListeners()
    }

    private fun renderInitialState() {
        updateTimerDisplay()
        updateDurationText(sliderMinutes.value.toInt())
        updateProgressBar(reset = true)
        updateButtons()
    }

    private fun setupChipGroupListener() {
        chipGroupModes.setOnCheckedStateChangeListener { _, checkedIds ->
            if (isRunning) return@setOnCheckedStateChangeListener

            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener

            val durationMinutes = when (checkedId) {
                R.id.chip_work -> 20
                R.id.chip_break -> 5
                R.id.chip_recharge -> 15
                else -> 25
            }

            sliderMinutes.value = durationMinutes.toFloat()
            setNewDuration(durationMinutes)
        }
    }


    private fun setupSliderListener() {
        sliderMinutes.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener

            if (isRunning) {
                sliderMinutes.value = TimeUnit.MILLISECONDS.toMinutes(initialDurationMillis).toFloat()
                return@addOnChangeListener
            }


            setNewDuration(value.toInt())
        }
    }


    private fun setupButtonListeners() {
        buttonStart.setOnClickListener {
            if (!isRunning) startTimer()
        }


        buttonPause.setOnClickListener {
            if (!isRunning) return@setOnClickListener


            if (isPaused) {
                resumeTimer()
            } else {
                pauseTimer()
            }
        }

        buttonReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun setNewDuration(minutes: Int) {
        initialDurationMillis = TimeUnit.MINUTES.toMillis(minutes.toLong())
        timeLeftMillis = initialDurationMillis


        updateTimerDisplay()
        updateDurationText(minutes)
        updateProgressBar(reset = true)
        updateButtons()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateTimerDisplay()
                updateProgressBar()
            }

            override fun onFinish() {
                resetTimer()
            }
        }.start()

        isRunning = true
        isPaused = false
        updateButtons()
    }


    private fun pauseTimer() {
        countDownTimer?.cancel()
        isPaused = true
        updateButtons()
    }


    private fun resumeTimer() {
        startTimer()
    }


    private fun resetTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isPaused = false
        timeLeftMillis = initialDurationMillis


        updateTimerDisplay()
        updateProgressBar(reset = true)
        updateButtons()
    }

    private fun updateTimerDisplay() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis) -
                TimeUnit.MINUTES.toSeconds(minutes)

        textViewTimer.text = String.format("%02d:%02d", minutes, seconds)
    }


    private fun updateDurationText(minutes: Int) {
        textViewDuration.text = minutes.toString()
    }


    private fun updateProgressBar(reset: Boolean = false) {
        progressCircular.progress = if (reset) {
            100
        } else {
            ((timeLeftMillis.toFloat() / initialDurationMillis.toFloat()) * 100).toInt()
        }
    }

    private fun updateButtons() {
        buttonStart.isEnabled = !isRunning
        buttonPause.isEnabled = isRunning
        buttonReset.isEnabled = isRunning || isPaused

        buttonPause.text = if (isPaused) "Продолжить" else "Пауза"
    }
}
