package com.example.spiicev2.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object Converters {
    fun Long.toDate (): String {
        val instant = Instant.fromEpochMilliseconds(this)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Форматирование даты в dd.MM.yyyy
        val day = dateTime.date.dayOfMonth.toString().padStart(2, '0')
        val month = (dateTime.date.monthNumber).toString().padStart(2, '0')
        val year = dateTime.date.year

        return "$day.$month.$year"

    }
}