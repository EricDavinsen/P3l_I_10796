package com.example.p3l_i_10796.api

class LoginApi {
    companion object{
        val BASE_URL = "https://gogofit.my.id/api/"
        val LOGIN_URL = BASE_URL + "LoginUser"
        val RESET_PASSWORD_URL = BASE_URL + "GantiPassword"
        val LOGOUT_URL = BASE_URL + "logout"
        val tampilanJadwal = BASE_URL + "dataJadwal"
    }
}