package com.example.shoppinglist.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Повертає статичний JSON замість реального сервера (для лабораторної).
 */
class MockShoppingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        if (!path.contains("items", ignoreCase = true)) {
            return chain.proceed(chain.request())
        }

        val now = System.currentTimeMillis()
        val json = """
            [
              {"id":"srv-1","name":"Хліб","quantity":1,"isBought":false,"createdAt":${now - 86_400_000L}},
              {"id":"srv-2","name":"Масло","quantity":1,"isBought":true,"createdAt":${now - 172_800_000L}},
              {"id":"srv-3","name":"Яйця","quantity":10,"isBought":false,"createdAt":${now - 3_600_000L}},
              {"id":"srv-4","name":"Молоко","quantity":2,"isBought":false,"createdAt":${now - 7_200_000L}},
              {"id":"srv-5","name":"Сир","quantity":1,"isBought":true,"createdAt":${now - 400_000L}}
            ]
        """.trimIndent()

        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(json.toResponseBody(JSON_MEDIA_TYPE))
            .build()
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
