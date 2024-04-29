package id.co.bca.abacas.transaction.dto.auto_mapper

import io.swagger.v3.oas.annotations.media.Schema

@Schema
class FavoriteRequest(
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
    val condition: MutableMap<String, MutableMap<String, Any>>
)
//data class Condition(
//    var name: String = "",
//    var condition: String = "",
//    var values: List<String> = listOf()
//)
//@Schema
//class DeleteFavoriteRequest(
//    @field:Schema(
//        description = "user Domain favorite yang ingin dihapus",
//        type = "string",
//        required = true,
//        example = "u1234"
//    )
//    val user: String,
//
//    @field:Schema(
//        description = "Nama favorite yang ingin dihapus",
//        type = "string",
//        required = true,
//        example = "fav 1",
//    )
//    val favoriteName: String
//)
