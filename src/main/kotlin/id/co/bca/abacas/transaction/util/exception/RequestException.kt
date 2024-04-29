package id.co.bca.abacas.transaction.util.exception
abstract class RequestException(
    open val errorValue: String = "",
    override val code: String,
) : BaseException()

open class RequestDateFormatException(
    override val errorValue: String = "",
    override val code: String = "400-01",
) : RequestException(errorValue, code)

open class RequestDataMissingException(
    override val errorValue: String = "",
    override val code: String = "400-02",
) : RequestException(errorValue, code)
