package com.example.p3l_i_10796.api

class PresensiBookingApi {
    companion object{
        val BASE_URL ="https://gogofit.my.id/api/"

        val GET_JADWAL = BASE_URL + "instrukturjadwal/"
        val GET_HISTORI = BASE_URL + "historipresensi/"
        val UPDATE_TRANSAKSI = BASE_URL + "updatetransaksi"
    }
}