package id.co.bca.abacas.transaction.dto.auto_mapper

import io.swagger.v3.oas.annotations.media.Schema

class WhereResponse(
    @field:Schema(
        description = "",
        type = "object"
    )
    val limitations: MutableMap<String, MutableList<String>> = mutableMapOf()
)
