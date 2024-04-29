package id.co.bca.abacas.transaction.util.exception

abstract class IllegalStateException(
        override val code: String,
        fieldValue: String? = null
) : BaseException(fieldValue)

data class IllegalDataStateException (
        override val message: String? = "",
        val fieldValue: String? = null
) : IllegalStateException("AP-50-001")

data class IllegalDataStoredException(
        val fieldName: String? = null,
        val fieldValue: String? = null
): IllegalStateException("AP-50-002") {
        override val message: String = "Found illegal data stored: " + fieldName?.let { "field name: $fieldName" } + fieldValue?.let {" has illegal value: $fieldValue" }
}

data class ProcessTookTooLong (
        override val message: String? = ""
) : IllegalStateException("AP-50-004")


open class NotMatchQueryException(
        override val fieldCauseError: String = "",
        override val code: String = "500-05-01",
) : ConflictException(fieldCauseError, code)