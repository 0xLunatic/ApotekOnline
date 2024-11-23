package com.example.apotekonline.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var sendButton: Button

    // Create a custom OkHttpClient with SSL support
    private fun createOkHttpClient(): OkHttpClient {
        val trustManager = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), java.security.SecureRandom())

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true }) // Accept all hostnames (not recommended for production)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        emailEditText = findViewById(R.id.forgotEmailForm)
        sendButton = findViewById(R.id.requestOtpButton)

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (isValidEmail(email)) {
                checkEmailInDatabase(email)
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkEmailInDatabase(email: String) {
        val client = createOkHttpClient()
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            """{"email": "$email"}"""
        )

        val request = Request.Builder()
            .url("http://pharmeasy.infinityfreeapp.com/check_email.php") // Ensure this is HTTPS
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ForgotPasswordActivity, "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@ForgotPasswordActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody)

                        runOnUiThread {
                            if (jsonResponse.has("error")) {
                                val errorMessage = jsonResponse.getString("error")
                                Toast.makeText(this@ForgotPasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            } else if (jsonResponse.has("message")) {
                                val successMessage = jsonResponse.getString("message")
                                Toast.makeText(this@ForgotPasswordActivity, successMessage, Toast.LENGTH_SHORT).show()

                                // Proceed to OTP verification if email exists
                                sendPasswordResetRequest(email)
                                val intent = Intent(this@ForgotPasswordActivity, VerifyOtpActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun sendPasswordResetRequest(email: String) {
        val client = createOkHttpClient()
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            """{"email": "$email"}"""
        )

        val request = Request.Builder()
            .url("http://pharmeasy.infinityfreeapp.com/reset_password.php") // Ensure this is HTTPS
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ForgotPasswordActivity, "Request failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@ForgotPasswordActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ForgotPasswordActivity, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                            val sharedPreferences = getSharedPreferences("PharmEasyPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("requested_email", email)
                            editor.apply()
                        }
                    }
                }
            }
        })
    }
}
