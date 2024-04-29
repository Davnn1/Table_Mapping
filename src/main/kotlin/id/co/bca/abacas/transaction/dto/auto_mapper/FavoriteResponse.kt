package id.co.bca.abacas.transaction.dto.auto_mapper

import io.swagger.v3.oas.annotations.media.Schema

@Schema
class FavoriteResponse(
    @field:Schema(
        description = "Name of the Favorite Report",
        type = "string"
    )
    val name: String,

    @field:Schema(
        description = "Query of the Favorite Report",
        type = "string"
    )
    val query: String,

    @field:Schema(
        description = "Select value",
        type = "Array"
    )
    val select: MutableList<String> = mutableListOf(),

    @field:Schema(
        description = "Where Value",
        type = "object"
    )
    val condition: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()
)
