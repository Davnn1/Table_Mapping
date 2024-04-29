package id.co.bca.abacas.transaction.dto.auto_mapper

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema
class ErrorSchema(
    @field:Schema(
        description = "Error Message",
        type = "string",
        example = "400"
    )
    @JsonProperty("error_code")
    val errorCode: String,

    @field:Schema(
        description = "Error Message in English",
        type = "string",
        example = "User already registered"
    )
    @JsonProperty("error_message_en")
    val errorMessageEN: String?,

    @field:Schema(
        description = "Error Message in Indonesian",
        type = "string",
        example = "User sudah terdaftar"
    )
    @JsonProperty("error_message_in")
    val errorMessageIN: String?
)