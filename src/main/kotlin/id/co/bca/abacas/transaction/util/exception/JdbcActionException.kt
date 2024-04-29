package id.co.bca.abacas.transaction.util.exception

abstract class JdbcActionException(
    override val message: String? = null,
    override val code: String = "409-03",
) : BaseException()

open class UpdateFailedException(
    override val message: String? = null,
    override val code: String = "409-03-01",
) : JdbcActionException(message, code)

open class CreateFailedException(
    override val message: String? = null,
    override val code: String = "409-03-02",
) : JdbcActionException(message, code)

open class DeleteFailedException(
    override val message: String? = null,
    override val code: String = "409-03-03",
) : JdbcActionException(message, code)
