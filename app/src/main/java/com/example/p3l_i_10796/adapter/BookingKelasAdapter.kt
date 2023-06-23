package com.example.p3l_i_10796.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.p3l_i_10796.BookingKelasActivity
import com.example.p3l_i_10796.R
import com.example.p3l_i_10796.models.BookingKelas
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BookingKelasAdapter(private var BookingKelasList: List<BookingKelas>,context: Context): RecyclerView.Adapter<BookingKelasAdapter.ViewHolder>() {
    private val context: Context

    init {
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_booking_kelas_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = BookingKelasList[position]
        holder.tvKodeBooking.text = "${data.KODE_BOOKING_KELAS} - ${data.NAMA_KELAS}"
        holder.tvTanggalBook.text = "Tanggal Kelas: ${data.TANGGAL_JADWAL_HARIAN}"
        holder.tvTanggalMelakukan.text = "Tanggal Booking: ${data.TANGGAL_MELAKUKAN_BOOKING}"
        holder.tvStatusBooking.text = "${data.STATUS_PRESENSI_KELAS} - ${data.WAKTU_PRESENSI_KELAS}"
        if (holder.tvStatusBooking.text == "null - null") {
            holder.tvStatusBooking.text = "Belum dikonfirmasi"
        }

        holder.iconDel.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin membatalkan booking kelas ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Iya") { _, _ ->
                    if (context is BookingKelasActivity) {
                        context.cancelBooking(data.KODE_BOOKING_KELAS)
                    }
                }
                .show()
        }
    }

    override fun getItemCount(): Int {
        return BookingKelasList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvKodeBooking: TextView
        var tvTanggalBook: TextView
        var tvTanggalMelakukan: TextView
        var tvStatusBooking: TextView
        var iconDel: ImageButton
        var cvBook: CardView

        init {
            tvKodeBooking = view.findViewById(R.id.text_kode)
            tvTanggalBook = view.findViewById(R.id.text_tanggal)
            tvTanggalMelakukan = view.findViewById(R.id.text_tanggal_melakukan)
            tvStatusBooking = view.findViewById(R.id.text_status_konfirmasi)
            iconDel = view.findViewById(R.id.btn_delete)
            cvBook = view.findViewById(R.id.cv_book)
        }

    }
}

