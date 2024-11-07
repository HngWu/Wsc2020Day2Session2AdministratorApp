package com.example.wsc2020day2session2administratorapp.api

import android.R
import android.R.string
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.wsc2020day2session2administratorapp.models.CheckIn
import com.example.wsc2020day2session2administratorapp.models.CheckInResponse
import com.example.wsc2020day2session2administratorapp.models.CreateUser
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class checkin {
    fun postFunction(
        competitorId: String,
        context: Context,
    ): CheckInResponse? {
        val url = URL("http://10.0.2.2:5006/api/Hospitality/checkin")

        try {
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true

            val json = Json.encodeToString(CheckIn(competitorId))
            val os = OutputStreamWriter(con.outputStream)

            os.write(json)
            os.flush()
            os.close()

            val status = con.responseCode
            if (status == 200) {
                val response = con.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = Json.decodeFromString<CheckInResponse>(response)
                return jsonResponse
            }
            else {
                return null
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }


}

