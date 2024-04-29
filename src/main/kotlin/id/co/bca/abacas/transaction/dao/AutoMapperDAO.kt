package id.co.bca.abacas.transaction.dao

import id.co.bca.abacas.transaction.dto.auto_mapper.FavoriteResponse
import id.co.bca.abacas.transaction.dto.auto_mapper.MapResponse
import id.co.bca.abacas.transaction.model.DefineModel
import id.co.bca.abacas.transaction.model.TableMapModel

interface AutoMapperDAO {
    fun getTableMap(): ArrayList<TableMapModel>
    fun getDefinedTableColumn(statement: String): DefineModel?
    fun getOutputToExcel(query: String): ByteArray
    fun getOutputToJson(tableName: String, columnName: String): MapResponse
    fun getOutputToJson2(query: String): String
    fun getTableWhere(): MutableMap<String, MutableList<String>>
    fun getTableSelect(): MutableList<String>
    fun getTableFavorite(uDomain: String): MutableList<FavoriteResponse>
    fun getFavoriteQueryByName(favoriteName:String): String
    fun addTableFavorite(user: String, name: String, query: String, select: MutableList<String>, condition: MutableMap<String, MutableMap<String, Any>>): Boolean
    fun deleteTableFavorite(uDomain: String, name: String): Boolean
    fun getAllDefineColumn(): List<String>
}
