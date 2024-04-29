package id.co.bca.abacas.transaction.util.exception

abstract class ConflictException(
    override val code: String,
    open val fieldCauseError: String = "",
) : BaseException(fieldCauseError)

open class DataUsedException(
    override val code: String = "409-01",
    override val fieldCauseError: String = "",
) : ConflictException(fieldCauseError, code)
open class DataNotFoundException(
    override val code: String = "409-02",
    override val fieldCauseError: String = "",
) : ConflictException(fieldCauseError, code)