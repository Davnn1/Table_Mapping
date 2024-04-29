package id.co.bca.abacas.transaction.dto.base

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


/**
 *  Wrapper response, either output_schema should be set for successful
 *  response or error_schema is set for error response
 *  @property outputSchema generic return data type
 *  @property errorSchema error data type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class WrapperResponse<T>(
	@JsonProperty("output_schema") var outputSchema: T? = null,
	@JsonProperty("error_schema") var errorSchema: ErrorSchema? = null,
) {
    companion object {
        fun <T> generateSuccess(data: T?): WrapperResponse<T> {
            return WrapperResponse(
                data,
                ErrorSchema("200", mapOf("OK" to "OK"))
            )
        }
        fun <T> generateNotFound(data: T?): WrapperResponse<T> {
            return WrapperResponse(
                data,
                ErrorSchema("404", mapOf("Not Found" to "Not Found"))
            )
        }
    }
}