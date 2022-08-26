package com.example.wsamad3

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import com.example.wsamad3.core.Constants
import com.example.wsamad3.core.networkInfo
import com.example.wsamad3.data.post
import com.example.wsamad3.data.signIn
import com.example.wsamad3.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        animateIn()

        writing()
        clicks()


    }

    private fun writing() {
        emailWriting()
        passwordWriting()

    }

    private fun passwordWriting() {
        binding.edtPassword.addTextChangedListener {
            val regex = Pattern.compile("^(1234)")
            if (!regex.matcher(it!!).matches()) {
                binding.edtPassword.error = "The password must have the correct format"
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.light_blue)
            } else {
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.white)
                binding.btnSignIn.isEnabled = true
                binding.edtPassword.error = null
            }
        }
    }

    private fun emailWriting() {
        binding.edtEmail.addTextChangedListener {
            val regex = Pattern.compile("^([a-zA-Z]{1,}@wsa[.]com)")
            if (!regex.matcher(it!!).matches()) {
                binding.edtEmail.error = "The email must have the correct format"
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.light_blue)
            } else {
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.white)
                binding.btnSignIn.isEnabled = true
                binding.edtEmail.error = null
            }
        }
    }

    private fun clicks() {
        binding.btnSignIn.setOnClickListener { validate() }
    }

    private fun validate() {
        val result = arrayOf(validateEmail(), validatePassword())
        if (false in result)
            return

        if (!networkInfo(applicationContext)) {
            alertMessage("No internet connection")
            return
        }
        sendVisible(true)
        sendSignIn()
    }

    private fun sendVisible(b: Boolean) {
        if (b) {
            binding.btnSignIn.visibility = View.GONE
            binding.progress.visibility = View.VISIBLE
        } else {
            binding.btnSignIn.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
        }
    }

    private fun sendSignIn() {
        Constants.okHttp.newCall(
            post(
                "signin/",
                signIn(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
            )
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                runOnUiThread {
                    sendVisible(false)
                    alertMessage("Server Error!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")) {
                    val data = json.getJSONObject("data")
                    val sharedPreferences =
                        getSharedPreferences(Constants.USER, Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("id", data.getString("id"))
                        putString("name", data.getString("name"))
                        putString("token", data.getString("token"))
                        apply()
                    }
                    runOnUiThread {
                        sendVisible(false)
                        animateOut()
                    }
                    Handler.createAsync(Looper.getMainLooper()).postDelayed({
                        val i = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(i)
                    },200)
                } else {
                    runOnUiThread {
                        sendVisible(false)
                        alertMessage("Wrong Credential")
                    }
                }
            }
        })
    }

    private fun validatePassword(): Boolean {
        return if (binding.edtEmail.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else {
            true
        }
    }

    private fun validateEmail(): Boolean {
        val regex = Pattern.compile("^([a-zA-Z]{1,}@wsa[.]com)")
        return if (binding.edtEmail.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else if (!regex.matcher(binding.edtEmail.text.toString()).matches()) {
            alertMessage("The Email field must have the correct format")
            false
        } else {
            true
        }
    }

    private fun alertMessage(s: String) {
        binding.txtAlertMessage.text = s
        binding.btnSignIn.animate().translationY(300f).setDuration(200).withEndAction {
            binding.llAlert.animate().alpha(1f).setDuration(200).withEndAction {
                binding.llAlert.animate().alpha(1f).setDuration(800).withEndAction {
                    binding.llAlert.animate().alpha(0f).setDuration(200)
                    binding.btnSignIn.animate().translationY(0f).setDuration(200)
                }
            }
        }
    }

    private fun animateIn() {
        binding.txtSignIn.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sttb
            )
        )
        binding.imgCircles.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.strtb
            )
        )
        binding.txtWelcome.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.stltob
            )
        )
        binding.txtIntroduction.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.stltob
            )
        )
        binding.txtLogin.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste
            )
        )
        binding.edtEmail.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste
            )
        )
        binding.txtPassword.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste
            )
        )
        binding.edtPassword.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste
            )
        )
        binding.btnSignIn.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sbtt
            )
        )
    }

    private fun animateOut() {
        binding.txtSignIn.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sbtt2
            )
        )
        binding.imgCircles.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sbtt2
            )
        )
        binding.txtWelcome.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste2
            )
        )
        binding.txtIntroduction.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste2
            )
        )
        binding.txtLogin.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste2
            )
        )
        binding.edtEmail.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste2
            )
        )
        binding.txtPassword.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste2
            )
        )
        binding.edtPassword.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sste2
            )
        )
        binding.btnSignIn.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.sttb2
            )
        )
    }
}
