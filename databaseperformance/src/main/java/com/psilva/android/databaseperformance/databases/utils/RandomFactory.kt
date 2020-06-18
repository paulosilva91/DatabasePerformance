package com.psilva.android.databaseperformance.databases.utils

import java.util.*

class RandomFactory {

    companion object {
        private val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"

        fun getRandomString() : String {
            return getRandomString(20)
        }

        fun getRandomString(sizeOfRandomString: Int): String {
            val random = Random()
            val sb = StringBuilder(sizeOfRandomString)

            for (i in 0 until sizeOfRandomString)
                sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])

            return sb.toString()
        }

        fun getRandomInt() : Int {
            return kotlin.random.Random.nextInt(0, Int.MAX_VALUE)
        }

        fun getRandomLong() : Long{
            return kotlin.random.Random.nextLong(0, Long.MAX_VALUE)
        }
    }
}