package com.seru.serujuragan.view.timelineview.extentions

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

fun String.formatDateTime(originalFormat: String, ouputFormat: String): String {
    val date = LocalDateTime.parse(this, DateTimeFormatter.ofPattern(originalFormat, Locale("in")))
    return date.format(DateTimeFormatter.ofPattern(ouputFormat, Locale("in")))
}

fun String.formatDate(date: LocalDate) : String{
    return date.format(DateTimeFormatter.ofPattern("EEEE, dd / MMMM/ yyyy", Locale("in")))
}