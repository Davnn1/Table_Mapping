package id.co.bca.abacas.transaction.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.toSqlDateFormat(): String{
    val dateFormat: DateFormat = SimpleDateFormat(Constant.SQL_DATE_FORMAT)
    return dateFormat.format(this)
}