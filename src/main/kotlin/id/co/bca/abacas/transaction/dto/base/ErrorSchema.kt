package id.co.bca.abacas.transaction.dto.base

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  Error schema
 *  @property code error code
 *  @property message error message in english-indonesian mapping
 */
data class ErrorSchema (
        @field:JsonProperty("error_code") val code: String,
        @field:JsonProperty("error_message") val message: Map<String, String>,
)