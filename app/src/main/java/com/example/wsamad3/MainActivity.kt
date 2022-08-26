package com.example.wsamad3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wsamad3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        animateIn()
    }

    private fun animateIn() {
        binding.logo.animate().translationY(-600f).alpha(0f).scaleX(0f).scaleY(0f).setDuration(0)
            .withEndAction {
                binding.logoName.animate().translationY(-100f).alpha(0f).setDuration(0)
                binding.logo.animate().translationY(0f).alpha(1f).scaleX(1f).scaleY(1f)
                    .setDuration(600).withEndAction {
                        binding.logoName.animate().translationY(-100f).alpha(0f).setDuration(0)
                            .withEndAction {
                                binding.logoName.animate().translationY(0f).alpha(1f)
                                    .setDuration(600)
                                    .withEndAction {
                                        binding.logoName.animate().translationY(0f).alpha(1f)
                                            .setDuration(600).withEndAction {
                                                binding.logo.animate().translationY(-300f).alpha(0f)
                                                    .scaleX(0f).scaleY(0f).setDuration(600)
                                                binding.logoName.animate().translationY(-100f)
                                                    .alpha(0f)
                                                    .setDuration(600).withEndAction {
                                                        val i = Intent(this@MainActivity,LoginActivity::class.java)
                                                        startActivity(i)
                                                        finish()
                                                    }
                                            }
                                    }
                            }
                    }
            }
    }
}


