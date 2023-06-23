package com.example.p3l_i_10796

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

import com.google.android.material.textfield.TextInputLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.api.LoginApi
import com.example.p3l_i_10796.databinding.ActivityMainBinding
import com.example.p3l_i_10796.models.Auth
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
//import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var mainLayout: ConstraintLayout
    private var queue: RequestQueue? = null
    lateinit  var mBundle: Bundle
    var etEmail: String = ""
    var etPassword: String = ""

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        queue = Volley.newRequestQueue(this)
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)

        val isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
            .getBoolean("isFirstRun", true)
        if (isFirstRun) {
            startActivity(Intent(this@MainActivity, SplashScreen::class.java))
            finish()
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
            .putBoolean("isFirstRun", false).commit()


        supportActionBar?.hide()

        binding.btnJadwal.setOnClickListener {
            FancyToast.makeText(this,"Halaman Menu Informasi Umum",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show()
            val moveDaftar = Intent(this@MainActivity, MenuUmum::class.java)
            startActivity(moveDaftar)
        }

        binding.btnLogin.setOnClickListener(View.OnClickListener {


            etEmail = binding.inputLayoutUsername.editText?.text.toString()
            etPassword = binding.inputLayoutPassword.editText?.text.toString()

            binding.inputLayoutUsername.setError(null)
            binding.inputLayoutPassword.setError(null)

            val auth = Auth(
                etEmail, etPassword
            )

            val stringRequest : StringRequest = object:
                StringRequest(Request.Method.POST, LoginApi.LOGIN_URL, Response.Listener { response ->

                    val gson = Gson()
                    var jsonObj = JSONObject(response.toString())
                    var userObjectData = jsonObj.getJSONObject("user")


                    if(userObjectData.has("ID_MEMBER")){
                        val token = jsonObj.getString("access_token")
                        val move = Intent(this@MainActivity, HomeActivity::class.java)
                        FancyToast.makeText(this,"Berhasil Login",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
                        sharedPreferences.edit()
                            .putString("id", userObjectData.getString("ID_MEMBER"))
                            .putString("role", "member")
                            .putString("token", token)
                            .apply()
                        startActivity(move)
                    }else if(userObjectData.has("ID_PEGAWAI")){
                        val token = jsonObj.getString("access_token")
                        sharedPreferences.edit()
                            .putInt("id", userObjectData.getInt("ID_PEGAWAI"))
                            .putString("role", "Manager Operasional")
                            .putString("token", token)
                            .apply()
                        FancyToast.makeText(this,sharedPreferences.getString("role",null),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
                        val move = Intent(this@MainActivity, HomeActivity::class.java)

                        startActivity(move)
                    }else if(userObjectData.has("ID_INSTRUKTUR")){
                        val token = jsonObj.getString("access_token")
                        sharedPreferences.edit()
                            .putInt("id", userObjectData.getInt("ID_INSTRUKTUR"))
                            .putString("role", "instruktur")
                            .putString("token", token)
                            .apply()
                        FancyToast.makeText(this,"Berhasil Login",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
                        val move = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(move)
                    }


                }, Response.ErrorListener { error ->
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        if (error.networkResponse.statusCode == 400) {
                            binding.inputLayoutUsername.setError("Email salah!")
                            binding.inputLayoutPassword.setError("Password salah!")
                        }else if (error.networkResponse.statusCode == 400) {
                            val jsonObject = JSONObject(responseBody)
                            val jsonObject1 = jsonObject.getJSONObject("message")
                            for (i in jsonObject1.keys()) {
                                if (i == "Email") {
                                    binding.inputLayoutUsername.error = jsonObject1.getJSONArray(i).getString(0)
                                }
                                if (i == "password") {
                                    binding.inputLayoutPassword.error = jsonObject1.getJSONArray(i).getString(0)
                                }
                            }
                        }else {
                            val errors = JSONObject(responseBody)
                            FancyToast.makeText(this, errors.getString("message"), FancyToast.LENGTH_LONG, FancyToast.INFO, false).show()
                        }

                    } catch (e: Exception){
                        FancyToast.makeText(this, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(auth)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8;"
                }

                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["Email"] = binding.emailInput.text.toString()
                    params["password"] = binding.passInput.text.toString()
                    return params
                }
            }
            queue!!.add(stringRequest)
        })

        binding.btnForgot.setOnClickListener(View.OnClickListener  {
            FancyToast.makeText(this,"Halaman Ganti Password",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show()
            val moveDaftar = Intent(this@MainActivity, ForgotPassword::class.java)
            startActivity(moveDaftar)
        })
    }
}



