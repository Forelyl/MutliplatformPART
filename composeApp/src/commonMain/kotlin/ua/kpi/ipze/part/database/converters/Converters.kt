package ua.kpi.ipze.part.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ua.kpi.ipze.part.database.types.LayersList
import ua.kpi.ipze.part.database.types.PaletteList

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLayersList(layersList: LayersList): String {
        return gson.toJson(layersList)
    }

    @TypeConverter
    fun toLayersList(data: String): LayersList {
        val type = object : TypeToken<LayersList>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromPaletteList(paletteList: PaletteList): String {
        return gson.toJson(paletteList)
    }

    @TypeConverter
    fun toPaletteList(data: String): PaletteList {
        val type = object : TypeToken<PaletteList>() {}.type
        return gson.fromJson(data, type)
    }
}