package com.example.simplecounter
import com.example.simplecounter.databinding.ActivityBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Activity : AppCompatActivity() {
    private lateinit var binding: ActivityBinding
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.plus.setOnClickListener {
            count++
            binding.textView.text = count.toString()
        }
        binding.minus.setOnClickListener {
            if (count > 0) {
                count--
                binding.textView.text = count.toString()
            }
        }
        binding.reset.setOnClickListener {
            count = 0
            binding.textView.text = count.toString()
        }
    }
}