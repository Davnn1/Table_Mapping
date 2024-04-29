package id.co.bca.abacas.transaction.util.exception
abstract class InternalServerException(
    override val code: String,
    open val causeError: String = "",
) : BaseException(causeError)

open class LogicException(
    override val code: String = "-",
    override val causeError: String = "",
) : InternalServerException(causeError, code)