package com.example.apotekonline.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var updatePasswordButton: Button
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences
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
        setContentView(R.layout.activity_change_password)

        // Initialize views
        updatePasswordButton = findViewById(R.id.updatePasswordButton)
        newPasswordEditText = findViewById(R.id.newPasswordForm)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordForm)
        sharedPreferences = getSharedPreferences("PharmEasyPrefs", MODE_PRIVATE)

        updatePasswordButton.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val email = sharedPreferences.getString("requested_email", null)

        if (email == null) {
            Toast.makeText(
                this,
                "Email not found. Please request a password change first.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validate input
        when {
            newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return
            }
            newPassword != confirmPassword -> {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return
            }
            newPassword.length < 8 -> {
                Toast.makeText(this, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Create a JSON body to send data
        val jsonBody = """
            {
                "email": "$email",
                "new_password": "$newPassword",
                "confirm_password": "$confirmPassword"
            }
        """.trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody)

        // Create a request to your PHP script
        val request = Request.Builder()
            .url("${Config.API_ADDRESS}change_password.php")
            .post(requestBody)
            .build()

        Log.d("ChangePasswordRequest", "Sending request: $request")

        // Execute the request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkError", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "Network Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    val responseBody = resp.body?.string()
                    Log.d("ChangePasswordResponse", "Response: $responseBody")

                    runOnUiThread {
                        try {
                            val jsonResponse = JSONObject(responseBody ?: "{}")
                            when {
                                resp.isSuccessful && jsonResponse.optString("status") == "success" -> {
                                    val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        jsonResponse.optString("message", "Password updated successfully"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sharedPreferences.edit().remove("requested_email").apply()


                                }
                                else -> {
                                    Toast.makeText(
                                        this@ChangePasswordActivity,
                                        jsonResponse.optString("message", "Failed to update password"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("ChangePasswordError", "Error parsing response: ${e.message}", e)
                            Toast.makeText(
                                this@ChangePasswordActivity,
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