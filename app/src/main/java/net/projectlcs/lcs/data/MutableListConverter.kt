package net.projectlcs.lcs.data

import androidx.room.TypeConverter
import com.google.gson.Gson


class MutableListConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun listToJson(value: MutableList<String>?): String? {
            return Gson().toJson(value)
        }

        @JvmStatic
        @TypeConverter
        fun jsonToList(value: String): MutableList<String>? {
            return Gson().fromJson(value, Array<String>::class.java)?.toMutableList()
        }
    }
}