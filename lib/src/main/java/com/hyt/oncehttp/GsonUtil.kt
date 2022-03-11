package com.hyt.oncehttp

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object GsonUtil {

    fun toMap(json: String): Map<String, Any> {
        return toMap(JsonParser.parseString(json).asJsonObject)
    }

    fun toMap(json: JsonObject): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        val entrySet = json.entrySet()
        val iter: Iterator<Map.Entry<String, JsonElement>> = entrySet.iterator()
        while (iter.hasNext()) {
            val (key, value) = iter.next()
            when (value) {
                is JsonArray -> map[key] =
                    toList(value)
                is JsonObject -> map[key] =
                    toMap(value)
                else -> map[key] = if(value.isJsonPrimitive){
                    //这里 需要固定类型，否则 字符串会出现  两个双引号的情况  ""key""
                    val obj = value.asJsonPrimitive
                    when {
                        obj.isBoolean -> obj.asBoolean
                        obj.isNumber -> obj.asNumber
                        obj.isString -> obj.asString
                        else-> obj
                    }
                }else{
                    value
                }
            }
        }
        return map
    }

    fun toList(json: JsonArray): List<Any> {
        val list: MutableList<Any> = ArrayList()
        for (i in 0 until json.size()) {
            val value: Any = json[i]
            when (value) {
                is JsonArray -> {
                    list.add(toList(value))
                }
                is JsonObject -> {
                    list.add(toMap(value))
                }
                else -> {
                    list.add(value)
                }
            }
        }
        return list
    }
}