package id.co.bca.abacas.transaction.controller


import id.co.bca.abacas.transaction.dto.base.ErrorSchema
import id.co.bca.abacas.transaction.dto.base.WrapperResponse
import id.co.bca.abacas.transaction.util.exception.AccessException
import id.co.bca.abacas.transaction.util.exception.ConflictException
import id.co.bca.abacas.transaction.util.exception.RequestException
import id.co.bca.abacas.transaction.util.getErrorMsg
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.io.IOException
import java.sql.SQLException
import java.sql.SQLSyntaxErrorException
import javax.servlet.http.HttpServletResponse
import kotlin.Any
import kotlin.Exception
import kotlin.Pair
import kotlin.Unit
import id.co.bca.abacas.transaction.dto.auto_mapper.ErrorSchema as error
import id.co.bca.abacas.transaction.dto.auto_mapper.WrapperResponse as wrapper

/**
 *  Global error exception handling, any error thrown will be captured and response will be
 *  sent according to error thrown
 *  @property msgSource error message builder
 */
@ControllerAdvice
class GlobalExceptionController @Autowired constructor(
    val msgSource: MessageSource
) {
    
    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun returnInternalServerError(response: HttpServletResponse, exception: Exception): WrapperResponse<Unit> {
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        val error = when (exception) {
            is SQLSyntaxErrorException -> Pair("500", msgSource.getErrorMsg("500"))
            is SQLException -> Pair("500-04", msgSource.getErrorMsg("500-04"))
            else -> Pair("500", msgSource.getErrorMsg("500"))
        }
        val errorResponse = WrapperResponse<Unit>(errorSchema = ErrorSchema("${error.first} ${exception.message}", error.second))
        exception.printStackTrace()
        return errorResponse
    }

    @ExceptionHandler(RequestException::class)
    @ResponseBody
    fun returnBadRequest(response: HttpServletResponse, exception: RequestException): WrapperResponse<Unit> {
        response.status = HttpStatus.BAD_REQUEST.value()
        val error = Pair(exception.code, msgSource.getErrorMsg(exception.code))
        val errorResponse = WrapperResponse<Unit>(errorSchema = ErrorSchema(error.first, error.second))

        return errorResponse
    }
    @ExceptionHandler(IOException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun exceptionHandler(e: IOException): Any? {
        return if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(e), "Broken pipe")) {
            null //socket is closed, cannot return any response
        } else {
            HttpEntity(e.message!!)
        }
    }
    @ExceptionHandler(ConflictException::class)
    @ResponseBody
    fun conflictException(e: ConflictException): ResponseEntity<wrapper<Unit>> {
        e.printStackTrace()
        val errorEn = msgSource.getErrorMsg(e.code)["english"]
        val errorIn = msgSource.getErrorMsg(e.code)["indonesian"]
        val errorSchema = error(e.code, errorEn, errorIn)
        return ResponseEntity(wrapper(null, errorSchema), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(AccessException::class)
    @ResponseBody
    fun accessException(e: AccessException): ResponseEntity<wrapper<Unit>> {
        e.printStackTrace()
        val errorEn = msgSource.getErrorMsg(e.code)["english"]
        val errorIn = msgSource.getErrorMsg(e.code)["indonesian"]
        val errorSchema = error(e.code, errorEn, errorIn)
        return ResponseEntity(wrapper(null, errorSchema), HttpStatus.BAD_REQUEST)
    }
}