package com.caixaapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.caixaapp.R
import com.caixaapp.databinding.ActivityLoginBinding
import com.caixaapp.model.SettingsRepository

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Habilita o modo ponta a ponta

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository
        settingsRepository = SettingsRepository(this)

        setupLogin()
    }

    @SuppressLint("SetTextI18n")
    private fun setupLogin() {
        val secureLoginStatus = settingsRepository.getSecureLoginStatus()

        if (secureLoginStatus == 1) {
            // Secure Login
            binding.loginTypeLabel.text = getString(R.string.seclogin_enb)
            binding.loginButton.setOnClickListener {
                authenticateUser()
            }
        } else {
            // Simple Login (bypass)
            binding.loginTypeLabel.text = getString(R.string.seclogin_dis)
            binding.loginButton.setOnClickListener {
                navigateToNextScreen()
            }
        }
    }

    private fun navigateToNextScreen() {
        // startActivity(Intent(this, TransactionActivity::class.java))
        // Change the destination from TransactionActivity to MainMenu
        startActivity(Intent(this, MainMenu::class.java))
        finish()
    }

    private fun authenticateUser() {
        val executor = ContextCompat.getMainExecutor(this)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.dialog_auth_title))
            .setSubtitle(getString(R.string.dialog_auth_subtitle))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                navigateToNextScreen()
            }
        })

        biometricPrompt.authenticate(promptInfo)
    }
}
