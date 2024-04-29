package id.co.bca.abacas.transaction.dto.auto_mapper
import io.swagger.v3.oas.annotations.media.Schema

class SelectResponse (
    @field:Schema(
        description = "",
        type = "array"
    )
    val selectList: MutableList<String>
)
