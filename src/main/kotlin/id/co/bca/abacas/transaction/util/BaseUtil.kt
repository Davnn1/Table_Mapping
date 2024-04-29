package id.co.bca.abacas.transaction.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import id.co.bca.abacas.transaction.config.Config
import id.co.bca.abacas.transaction.util.exception.BadArgumentsException
import org.springframework.context.MessageSource

/**
 *  Error code message mapping
 *  @param code internal (AP-) error code message mapping
 *  @param message exception message thrown, if code not available
 */
fun MessageSource.getErrorMsg(code: String, message: String? = null) =
        Config.supportedLanguage.map { (language, locale) ->
            if (!message.isNullOrBlank()) {
                language to "${getMessage(code, null, locale)} $message"
            } else {
                language to getMessage(code, null, locale)
            }
        }.toMap()

fun convertJsonToMap(jsonString: String): List<Map<String, Any>> {
    try {
        val objectMapper = ObjectMapper()
        val typeRef = object : TypeReference<List<Map<String, Any>>>() {}
        return objectMapper.readValue(jsonString, typeRef)
    } catch (e: Exception) {
        throw BadArgumentsException()
    }
}

fun convertJsonToCondition(jsonString: String): MutableMap<String, MutableMap<String, Any>> {
    try {
        val objectMapper = ObjectMapper()
        val typeRef = object : TypeReference<MutableMap<String, MutableMap<String, Any>>>() {}
        return objectMapper.readValue(jsonString, typeRef)
    } catch (e: Exception) {
        throw BadArgumentsException()
    }
}
/**
 * @return SQL order query based on pageable
 *
 * sample "ORDER BY *columname* ASC"
 * */
//fun Pageable?.getSortQuery(defaultColumnName: String = "", defaultSortDirection: Sort.Direction): String {
//    val orderQuery = defaultColumnName.trim().toUpperCase().let{
//        if (it.matches(Regex.ORDERBY_QUERY))
//            " ORDER BY $it ${defaultSortDirection.name} "
//        else
//            ""
//    }
//
//    return when{
//        this == null -> {
//            orderQuery
//        }
//        this.sort == Sort.unsorted() -> {
//            orderQuery
//        }
//        else -> {
//            val order = this.sort.toList()[0]
//            if (!COL_NAME_FORMAT.matches(order.property)) throw RequestDataFormatException(errorValue = order.property)
//
//            "ORDER BY ${order.property} ${order.direction.name} "
//        }
//    }
//}
//fun Pageable?.queryPagination(): String{
//    if (this == null || (this.pageSize == PAGEABLE_NO_PAGGING && this.pageNumber == PAGEABLE_NO_PAGGING))
//        return ""
//    return "OFFSET ${this.offset} ROWS FETCH NEXT ${this.pageSize} ROWS ONLY "
//}
//
//// TIMER
//class ExecutionTime{
//    var start : Long = 0L
//    var end : Long = 0L
//    fun start(){
//        start = System.currentTimeMillis()
//    }
//
//    /**
//     * @return time in second
//     * */
//    fun end(): Double{
//        end = System.currentTimeMillis()
//        return ((end - start) / 1_000.0)
//    }
//}