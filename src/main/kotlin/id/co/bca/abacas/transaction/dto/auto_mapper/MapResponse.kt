package id.co.bca.abacas.transaction.dto.auto_mapper

import io.swagger.v3.oas.annotations.media.Schema

@Schema
class MapResponse (
    @field:Schema(
        description = "Query for the mapper",
        type = "string"
    )
    val query: String,

    @field:Schema(
        description = "Output JSON",
        type = "string"
    )
    val output: String
)
