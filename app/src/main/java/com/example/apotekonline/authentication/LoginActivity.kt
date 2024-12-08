package com.example.apotekonline.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.DashboardActivity
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

class LoginActivity : AppCompatActivity() {

    private lateinit var emailForm: EditText
    private lateinit var passwordForm: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordText: TextView
    private lateinit var signUpText: TextView
    private val client by lazy { createUnsafeOkHttpClient() }

    // Create an OkHttpClient that trusts all certificates (FOR DEVELOPMENT ONLY)
    private fun createUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        emailForm = findViewById(R.id.emailForm)
        passwordForm = findViewById(R.id.passwordForm)
        loginButton = findViewById(R.id.loginButton)
        forgotPasswordText = findViewById(R.id.forgotPasswordForm)
        signUpText = findViewById(R.id.registerAccountForm)

        loginButton.setOnClickListener {
            val email = emailForm.text.toString()
            val password = passwordForm.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        val jsonBody = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaType(), jsonBody)

        val request = Request.Builder()
            .url("http://10.0.2.2/register.php") // Change to your server URL if not local
            .post(requestBody)
            .build()

        Log.d("LoginRequest", request.toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkError", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string()
                    Log.d("LoginResponse", responseBody ?: "Empty response")

                    runOnUiThread {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val status = jsonResponse.getString("status")
                            if (status == "success") {
                                Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val message = jsonResponse.getString("message")
                                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("LoginError", "Error parsing response: ${e.message}", e)
                            Toast.makeText(this@LoginActivity, "Error processing response", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}