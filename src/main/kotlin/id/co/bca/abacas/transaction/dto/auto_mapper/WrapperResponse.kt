package id.co.bca.abacas.transaction.dto.auto_mapper

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema
class WrapperResponse<T>(
    @field:Schema(
        description = "Output Schema",
        type = "object"
    )
    @JsonProperty("output_schema")
    val outputSchema: T?,

    @field:Schema(
        description = "Error Schema",
        type = "object"
    )
    @JsonProperty("error_schema")
    val errorSchema: ErrorSchema
){
    companion object{
        fun <T> generateSuccess(data: T): WrapperResponse<T> {
            return WrapperResponse(
                data,
                ErrorSchema("200", "OK", "OK")
            )
        }
    }
}