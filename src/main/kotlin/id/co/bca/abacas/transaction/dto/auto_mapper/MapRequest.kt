package id.co.bca.abacas.transaction.dto.auto_mapper

import io.swagger.v3.oas.annotations.media.Schema

@Schema()
class MapRequest(
    @field:Schema(
        description = "Masukkan column yang ingin dicari (gunakan ';' untuk memisahkan column) (Contoh:" +
                " mid_outlet_terminalinit;id_mesin_status;id_mesin_type)",
        type = "string",
        required = true
    )
    var columns: String = "",

    @field:Schema(
        description = "Masukkan value dari column (gunakan '_' untuk memisahkan column dengan table)" +
                " (Contoh: \"mid_outlet\") dan value condition (=, !=, >, <, >=, <=",
        type = "object",
        required = true,
        example = """
        [
            {
                "name": "mid_outlet_terminalinit",
                "values": [
                    "01234",
                    "012345"
                ],
                "condition": "="
            },
            {
                "name": "mid_outlet_terminalinit2",
                "values": [
                    "01234",
                    "012345"
                ],
                "condition": "!="
            }
        ]
    """
    )
    var columnsValues: List<ColumnValue> = listOf(),

    )

data class ColumnValue(
    var name: String = "",
    var condition: String = "",
    var values: List<String> = listOf()
)
