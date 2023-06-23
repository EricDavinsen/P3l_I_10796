package com.example.p3l_i_10796

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.api.LoginApi
import com.example.p3l_i_10796.databinding.ActivityForgotPasswordBinding
import com.example.p3l_i_10796.models.Auth
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ForgotPassword : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        queue = Volley.newRequestQueue(this)
//        setContentView(R.layout.activity_ganti_password)
        supportActionBar?.hide()

        binding.btnReset.setOnClickListener(View.OnClickListener  {
            FancyToast.makeText(this,"Kembali ke Login",
                FancyToast.LENGTH_LONG,
                FancyToast.INFO,false).show()
            val moveDaftar = Intent(this@ForgotPassword, MainActivity::class.java)
            startActivity(moveDaftar)
        })

        binding.btnDaftar.setOnClickListener(View.OnClickListener {
            changePassword()
        })
    }


    private fun changePassword(){

        val auth = Auth(
            binding.inputLayoutEmail.getEditText()?.getText().toString(),
            binding.inputLayoutPassword.getEditText()?.getText().toString())

        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, LoginApi.RESET_PASSWORD_URL, Response.Listener { response ->
                val gson = Gson()

                var user= gson.fromJson(response, Auth::class.java)

                var resJO = JSONObject(response.toString())
                val userobj = resJO.getJSONObject("user")

                if(user!=null) {
                    FancyToast.makeText(this,"Berhasil Ganti Passsword",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
                    val intent = Intent(this@ForgotPassword, MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    FancyToast.makeText(this,"Ganti Password Gagal",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
                }
                return@Listener
            }, Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    FancyToast.makeText(this, errors.getString("message"), FancyToast.LENGTH_LONG, FancyToast.INFO, false).show()
                }catch (e: Exception) {
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
            }
        queue!!.add(stringRequest)
    }
}