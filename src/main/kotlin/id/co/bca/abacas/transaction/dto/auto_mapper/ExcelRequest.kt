package id.co.bca.abacas.transaction.dto.auto_mapper

import io.swagger.v3.oas.annotations.media.Schema

@Schema
class ExcelRequest(
    @field:Schema(
        description = "Masukkan udomain",
        type = "string",
        required = true
    )
    val user: String,
    @field:Schema(
        description = "Masukkan nama favorit",
        type = "string",
        required = true
    )
    val name: String,
    @field:Schema(
        description = "Masukkan query favorit",
        type = "string",
        required = true
    )
    val query: String,
    @field:Schema(
        description = "Masukkan value select",
        type = "Array",
        required = true
    )
    val select: MutableList<String> = mutableListOf(),

    @field:Schema(
        description = "Masukkan value Where",
        type = "object",
        required = true,
        example =
        """{
  "name_area": {
      "condition": "=",
      "values": [
        "das",
        "fsav"
      ]
  },
  "nama_customer": {
      "condition": "=",
      "values": [
        "agus",
        "senti"
      ]
  }
}
        """
    )
    val condition: Any
)

