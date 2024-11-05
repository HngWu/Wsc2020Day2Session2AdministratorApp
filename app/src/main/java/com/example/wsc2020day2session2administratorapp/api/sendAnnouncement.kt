package com.example.wsc2020day2session2administratorapp.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.wsc2020day2session2administratorapp.models.Announcement
import com.example.wsc2020day2session2administratorapp.models.CreateUser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class sendAnnouncement {
    fun postFunction(
        announcement: Announcement,
        context: Context,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val url = URL("http://10.0.2.2:5006/api/Hospitality/announcement")

        try {
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true

            val json = Json.encodeToString(announcement)
            val os = OutputStreamWriter(con.outputStream)

            os.write(json)
            os.flush()
            os.close()

            val status = con.responseCode
            if (status == 200) {
                Handler(Looper.getMainLooper()).post {
                    onSuccess(true)
                }
            }
            else
            {
                Handler(Looper.getMainLooper()).post() {
                    onFailure(Throwable("Post asset failed"))
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}