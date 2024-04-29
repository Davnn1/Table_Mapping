package id.co.bca.abacas.transaction.util.exception

import java.sql.SQLException

abstract class SqlException(
    override val code: String,
    open val sqlException: SQLException? = null
) : BaseException(){
    fun getValueCauseError():String {
        return sqlException?.let {
            it.localizedMessage.split("\"")[1]
        } ?: ""
    }

    fun getSpecificException(): SqlException{
        return when(sqlException?.errorCode){
            904 -> SqlInvalidIdentifierException(sqlException = sqlException)
            else -> SqlSyntaxException("500")
        }
    }
}

open class SpringJdbcException(
    override val code: String = "500-04",
    override val sqlException: SQLException? = null
) : SqlException(code, sqlException)

open class SqlSyntaxException(
    override val code: String = "500-04",
    override val sqlException: SQLException? = null
) : SqlException(code, sqlException)

open class SqlInvalidIdentifierException(
    override val code: String = "500-04-01",
    override val sqlException: SQLException? = null
) : SqlException(code, sqlException)
