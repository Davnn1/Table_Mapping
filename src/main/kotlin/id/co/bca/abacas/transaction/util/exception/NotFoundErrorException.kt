package id.co.bca.abacas.transaction.util.exception

abstract class NotFoundErrorException(
    override val code: String
) : BaseException()

open class ResourceNotFoundException(
    override val code: String = "AP-20-001"
) : NotFoundErrorException(code)
