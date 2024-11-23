package com.example.apotekonline.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var otpEditText: EditText
    private lateinit var verifyButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val client by lazy { createUnsafeOkHttpClient() } // Create the unsafe OkHttpClient

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
        setContentView(R.layout.activity_verify_otp)

        otpEditText = findViewById(R.id.otpForm)
        verifyButton = findViewById(R.id.verifyButton)
        sharedPreferences = getSharedPreferences("PharmEasyPrefs", MODE_PRIVATE)

        verifyButton.setOnClickListener {
            val enteredOtp = otpEditText.text.toString()
            val email = sharedPreferences.getString("requested_email", null)

            if (email != null && enteredOtp.isNotEmpty()) {
                verifyOtp(email, enteredOtp)
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyOtp(email: String, otp: String) {
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            """{"email": "$email", "otp": "$otp"}"""
        )

        val request = Request.Builder()
            .url("http://pharmeasy.infinityfreeapp.com/verify_otp.php") // Update with your server URL
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@VerifyOtpActivity, "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@VerifyOtpActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody)

                        runOnUiThread {
                            if (jsonResponse.has("error")) {
                                val errorMessage = jsonResponse.getString("error")
                                Toast.makeText(this@VerifyOtpActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            } else if (jsonResponse.has("message")) {
                                // Handle success (e.g., navigate to the next activity)
                                val successMessage = jsonResponse.getString("message")
                                Toast.makeText(this@VerifyOtpActivity, successMessage, Toast.LENGTH_SHORT).show()
                                // Navigate to reset password activity or another relevant activity
                                startActivity(Intent(this@VerifyOtpActivity, ChangePasswordActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        })
    }
}
