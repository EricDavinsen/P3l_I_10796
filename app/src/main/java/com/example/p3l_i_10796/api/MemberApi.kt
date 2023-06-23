package com.example.p3l_i_10796.api

class MemberApi {
    companion object {
        val BASE_URL = "https://gogofit.my.id/api/"
        val GET_ALL_URL = BASE_URL + "indexGym/"
        val BATALGYM = BASE_URL + "batalGym/"
        val STORE_GYM = BASE_URL + "storeGym"

        val GET_HISTORI_URL = BASE_URL + "histori/"

        val GET_KELAS = BASE_URL + "getKelas/"
        val BATAL_KELAS= BASE_URL + "batalKelas/"
        val STORE_KELAS = BASE_URL + "storeKelas"

        val GETDATAMEMBER = BASE_URL + "dataMember/"
    }
}