package id.co.bca.abacas.transaction.service

import id.co.bca.abacas.transaction.dto.auto_mapper.*

interface AutoMapperService {
    fun getMap(columns: String, columnsValue: List<ColumnValue>): MapResponse
    fun getWhere(): WhereResponse
    fun getSelect(): SelectResponse
    fun getFavorite(uDomain: String): MutableList<FavoriteResponse>
    fun addFavorite(
        user: String,
        name: String,
        query: String,
        select: MutableList<String>,
        condition: MutableMap<String, MutableMap<String, Any>>
    ): String
    fun deleteFavorite(uDomain: String, name: String): String
    fun  getDefine():List<String>
    fun getExcelByFavoriteName(favoriteName:String): ExcelResponse
}
