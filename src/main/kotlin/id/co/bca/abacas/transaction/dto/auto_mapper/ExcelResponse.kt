package id.co.bca.abacas.transaction.dto.auto_mapper

import io.swagger.v3.oas.annotations.media.Schema

@Schema
class ExcelResponse (
    @field:Schema(
        description = "Output JSON",
        type = "string"
    )
    val output: String
)
