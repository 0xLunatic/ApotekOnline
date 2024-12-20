package com.example.apotekonline.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginText: TextView
    private val client by lazy { createUnsafeOkHttpClient() }

    // Create an OkHttpClient that trusts all certificates (FOR DEVELOPMENT ONLY)
    private fun createUnsafeOkHttpClient(): OkHttpClient {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        registerButton = findViewById(R.id.registerButton)
        usernameEditText = findViewById(R.id.usernameForm)
        emailEditText = findViewById(R.id.emailForm)
        passwordEditText = findViewById(R.id.passwordForm)
        loginText = findViewById(R.id.loginLink)

        registerButton.setOnClickListener {
            if (validateInputs()) {
                registerButton.isEnabled = false // Prevent multiple clicks
                registerUser()
            }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        when {
            username.isEmpty() -> {
                usernameEditText.error = "Username is required"
                usernameEditText.requestFocus()
                return false
            }
            username.length < 3 -> {
                usernameEditText.error = "Username must be at least 3 characters long"
                usernameEditText.requestFocus()
                return false
            }
            email.isEmpty() -> {
                emailEditText.error = "Email is required"
                emailEditText.requestFocus()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailEditText.error = "Please enter a valid email address"
                emailEditText.requestFocus()
                return false
            }
            password.isEmpty() -> {
                passwordEditText.error = "Password is required"
                passwordEditText.requestFocus()
                return false
            }
            password.length < 8 -> {
                passwordEditText.error = "Password must be at least 8 characters long"
                passwordEditText.requestFocus()
                return false
            }
        }
        return true
    }

    private fun registerUser() {
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        // Create a JSON body to send data
        val jsonBody = """
            {
                "username": "$username",
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaType(), jsonBody)

        val request = Request.Builder()
            .url("${Config.API_ADDRESS}register.php") // Fixed URL path
            .post(requestBody)
            .build()

        Log.d("RegisterRequest", "Sending registration request for email: $email")

        // Execute the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkError", "Error: ${e.message}", e)
                runOnUiThread {
                    registerButton.isEnabled = true
                    Toast.makeText(
                        this@RegisterActivity,
                        "Network Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    val responseBody = resp.body?.string()
                    Log.d("RegisterResponse", "Response: $responseBody")

                    runOnUiThread {
                        try {
                            val jsonResponse = JSONObject(responseBody ?: "{}")
                            when {
                                resp.isSuccessful && jsonResponse.optString("status") == "success" -> {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Registration successful! Please login.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Navigate to login screen
                                    startActivity(
                                        Intent(
                                            this@RegisterActivity,
                                            LoginActivity::class.java
                                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    )
                                    finish()
                                }
                                else -> {
                                    registerButton.isEnabled = true
                                    val errorMessage = jsonResponse.optString("message", "Registration failed")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            registerButton.isEnabled = true
                            Log.e("JsonError", "Error parsing response: ${e.message}", e)
                            Toast.makeText(
                                this@RegisterActivity,
                                "Error processing server response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }
}