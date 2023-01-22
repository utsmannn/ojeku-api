package com.aej.ojekkuapi.utils

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class BaseComponent {

    inline fun <reified T> getHttp(url: String): Result<T> {
        val client = OkHttpClient()

        return try {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body
            val bodyString = body?.string()
            if (response.isSuccessful) {
                val data = ObjectMapper().readValue(bodyString, T::class.java)
                Result.success(data)
            } else {
                val throwable = IllegalArgumentException(response.message)
                Result.failure(throwable)
            }
        } catch (e: JsonParseException) {
            Result.failure(e)
        } catch (e: InvalidDefinitionException) {
            Result.failure(e)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend inline fun <reified T> postHttp(
        url: String,
        requestBody: RequestBody,
        header: Headers? = null
    ): Result<T> = suspendCoroutine { task ->
        val client = OkHttpClient()

        try {
            val request = Request.Builder()
                .url(url)
                .run {
                    if (header != null) {
                        this.headers(header)
                    } else {
                        this
                    }
                }
                .post(requestBody)
                .build()

            println("asuuuuu body -> ${requestBody}")
            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        task.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val bodyString = response.body?.string()
                        val data = ObjectMapper().readValue(bodyString, T::class.java)
                        val result = Result.success(data)
                        task.resume(result)
                    }
                })
        } catch (e: JsonParseException) {
            task.resume(Result.failure(e))
        } catch (e: InvalidDefinitionException) {
            task.resume(Result.failure(e))
        } catch (e: Throwable) {
            task.resume(Result.failure(e))
        }
    }
}