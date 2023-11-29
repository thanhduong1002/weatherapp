package com.example.weatherapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ProvinceItemBinding
import com.example.weatherapp.model.ProvinceData
import com.example.weatherapp.utils.StringUtils

class ProvincesAdapter(private var listProvince: List<ProvinceData>) :
    RecyclerView.Adapter<ProvincesAdapter.ProvincesViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setListProvince(listProvince: List<ProvinceData>) {
        this.listProvince = listProvince
        notifyDataSetChanged()
    }
    inner class ProvincesViewHolder(val provinceItemBinding: ProvinceItemBinding) :
        RecyclerView.ViewHolder(provinceItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvincesViewHolder {
        return ProvincesViewHolder(
            ProvinceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listProvince.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProvincesViewHolder, position: Int) {
        val item = listProvince[position]

        holder.provinceItemBinding.apply {
            nameProvince.text = item.name?.let { StringUtils.removeTinh(it) }
            numberVehicle.text = item.name?.let { StringUtils.removeTinh(it) }
                ?.let { getLicensePlateNumber(it) }
        }
    }

    private fun getLicensePlateNumber(cityName: String): String {
        return when (cityName) {
            "Thành phố Hà Nội" -> "29-33, 40"
            "Thành phố Hồ Chí Minh" -> "41, 50-59"
            "Thành phố Đà Nẵng" -> "43"
            "Thành phố Hải Phòng" -> "15-16"
            "Thành phố Cần Thơ" -> "65"
            "Cao Bằng" -> "11"
            "Lạng Sơn" -> "12"
            "Quảng Ninh" -> "14"
            "Thái Bình" -> "17"
            "Nam Định" -> "18"
            "Phú Thọ" -> "19"
            "Thái Nguyên" -> "20"
            "Yên Bái" -> "21"
            "Tuyên Quang" -> "22"
            "Hà Giang" -> "23"
            "Lào Cai" -> "24"
            "Lai Châu" -> "25"
            "Sơn La" -> "26"
            "Điện Biên" -> "27"
            "Hoà Bình" -> "28"
            "Hải Dương" -> "34"
            "Ninh Bình" -> "35"
            "Thanh Hóa" -> "36"
            "Nghệ An" -> "37"
            "Hà Tĩnh" -> "38"
            "Đắk Lắk" -> "47"
            "Đắk Nông" -> "48"
            "Lâm Đồng" -> "49"
            "Đồng Nai" -> "39, 60"
            "Bình Dương" -> "61"
            "Long An" -> "62"
            "Tiền Giang" -> "63"
            "Vĩnh Long" -> "64"
            "Đồng Tháp" -> "66"
            "An Giang" -> "67"
            "Kiên Giang" -> "68"
            "Cà Mau" -> "69"
            "Tây Ninh" -> "70"
            "Bến Tre" -> "71"
            "Bà Rịa - Vũng Tàu" -> "72"
            "Quảng Bình" -> "73"
            "Quảng Trị" -> "74"
            "Thừa Thiên Huế" -> "75"
            "Quảng Ngãi" -> "76"
            "Bình Định" -> "77"
            "Phú Yên" -> "78"
            "Khánh Hòa" -> "79"
            "Gia Lai" -> "81"
            "Kon Tum" -> "82"
            "Sóc Trăng" -> "83"
            "Trà Vinh" -> "84"
            "Ninh Thuận" -> "85"
            "Bình Thuận" -> "86"
            "Vĩnh Phúc" -> "88"
            "Hưng Yên" -> "89"
            "Hà Nam" -> "90"
            "Quảng Nam" -> "92"
            "Bình Phước" -> "93"
            "Bạc Liêu" -> "94"
            "Hậu Giang" -> "95"
            "Bắc Kạn" -> "97"
            "Bắc Giang" -> "98"
            "Bắc Ninh" -> "99"

            else -> "Unknown License Plate Number"

        }
    }
}