package id.co.bca.abacas.transaction.model

data class TableMapModel(
    var tableName1: String,
    var tableName2: String,
    val columnNameFirst: String,
    val columnNameSecond: String
)