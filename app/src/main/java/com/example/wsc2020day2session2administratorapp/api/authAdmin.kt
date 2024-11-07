package com.example.wsc2020day2session2administratorapp.api
import android.R.string
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.wsc2020day2session2administratorapp.models.SessionManager
import com.example.wsc2020day2session2administratorapp.models.UserSession
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.toString


class authAdmin {
    fun postFunction(
        context: Context,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Boolean) -> Unit
    ) {
        val url = URL("http://10.0.2.2:5006/api/Hospitality/AuthAdmin")

        try {
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true

            val sessionManager = SessionManager(context)
            var session = sessionManager.getSession()

//            if (session == null) {
//                Handler(Looper.getMainLooper()).post() {
//                    onFailure(true)
//                }
//            }


            val newSession = session?.let { UserSession(it.token, it.role, it.userId) }

            val json = Json.encodeToString(newSession)
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
                    onFailure(true)
                }
            }
        } catch (e: Exception) {
            onFailure(true)
        }
    }

}

