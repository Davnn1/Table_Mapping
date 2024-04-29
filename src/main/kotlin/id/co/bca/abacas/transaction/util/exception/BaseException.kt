package id.co.bca.abacas.transaction.util.exception

import java.lang.RuntimeException

abstract class BaseException(open val msg: String? = null): RuntimeException(msg) {
    abstract val code: String
}