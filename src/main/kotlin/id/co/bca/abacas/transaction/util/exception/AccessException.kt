package id.co.bca.abacas.transaction.util.exception

abstract class AccessException (
    override val code: String,
    open val fieldCauseError: String = ""
) : BaseException(fieldCauseError)

open class BadArgumentsException(
    override val code: String = "400-02",
    override val fieldCauseError: String = ""
) : AccessException(fieldCauseError, code)

open class DataSaveFailedException(
    override val code: String = "509-01",
    override val fieldCauseError: String = ""
) : AccessException(fieldCauseError, code)

open class DataUpdateFailedException(
    override val code: String = "509-02",
    override val fieldCauseError: String = ""
) : AccessException(fieldCauseError, code)

