package id.co.bca.abacas.transaction.dao.implement

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.co.bca.abacas.transaction.dao.AutoMapperDAO
import id.co.bca.abacas.transaction.dto.auto_mapper.FavoriteResponse
import id.co.bca.abacas.transaction.dto.auto_mapper.MapResponse
import id.co.bca.abacas.transaction.model.DefineModel
import id.co.bca.abacas.transaction.model.TableMapModel
import id.co.bca.abacas.transaction.util.exception.DataNotFoundException
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import java.io.ByteArrayOutputStream
import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat

@Repository
class AutoMapperDAOImpl(private val jdbc: JdbcTemplate) : AutoMapperDAO {

    @Autowired
    private lateinit var env: Environment

    override fun getTableMap(): ArrayList<TableMapModel> {
        val tableMap = ArrayList<TableMapModel>()

        val query =
            "SELECT table_name_1, table_name_2, column_name_1, column_name_2 FROM report_custom_mapping"

        jdbc.query(query) { rs, _ ->
            val table = TableMapModel(
                tableName1 = rs.getString("table_name_1"),
                tableName2 = rs.getString("table_name_2"),
                columnNameFirst = rs.getString("column_name_1"),
                columnNameSecond = rs.getString("column_name_2")
            )
            tableMap.add(table)
        }
        return tableMap
    }

    override fun getDefinedTableColumn(statement: String): DefineModel? {
        val params = ArrayList<Any?>()
        val query = buildString {
            append("SELECT defined_table as tableName, defined_column as columnName FROM report_custom_define ")

            append("WHERE define_name = ?")
            params.add(statement)
        }

        return jdbc.query(query, BeanPropertyRowMapper(DefineModel::class.java), *params.toTypedArray())
            .takeIf { it.size > 0 }?.get(0)
    }

    override fun getOutputToExcel(query: String): ByteArray {
        val outputDir = File(".\\target\\report-output").apply { mkdirs() }

        val workbook = WorkbookFactory.create(true)

        jdbc.query(query, ResultSetExtractor { rs ->

            if (!rs.next())
                throw DataNotFoundException()

            val numColumns = rs.metaData.columnCount
            val sheet = workbook.createSheet()
            val headerRow = sheet.createRow(0)

            // Write the column name
            for (i in 1..numColumns) {
                val tableName = rs.metaData.getTableName(i)
                val columnName = rs.metaData.getColumnName(i)
                headerRow.createCell(i - 1).setCellValue("$tableName.$columnName")
                sheet.autoSizeColumn(i - 1)
            }

            // Write the data start from second row
            var rowNum = 1
            while (rs.next()) {
                val dataRow = sheet.createRow(rowNum++)
                for (i in 1..numColumns) {
                    dataRow.createCell(i - 1).setCellValue(rs.getString(i))
                    sheet.autoSizeColumn(i - 1)
                }
            }
        })

        // write the workbook to byte array
        val byteArray = ByteArrayOutputStream().use {
            workbook.write(it)
            it.toByteArray()
        }

        workbook.close()

        return byteArray
    }

    override fun getOutputToJson(tableName: String, columnName: String): MapResponse {

        val result: MutableList<Map<String, Any>> = mutableListOf()

        val query = "SELECT DISTINCT ${columnName} FROM ${tableName}"

        jdbc.query(query, ResultSetExtractor { rs ->

            val numColumns = rs.metaData.columnCount

            while (rs.next()) {
                val row: MutableMap<String, Any> = mutableMapOf()
                for (i in 1..numColumns) {
                    row["$tableName.$columnName"] = rs.getObject(i)
                }
                result.add(row)
            }
        })
        if (result.isEmpty())
            throw DataNotFoundException()

        return MapResponse(query, ObjectMapper().writeValueAsString(result))
    }

    override fun getOutputToJson2(query: String): String {

        val result: MutableList<Map<String, Any>> = mutableListOf()

        jdbc.query(query, ResultSetExtractor { rs ->
            val numColumns = rs.metaData.columnCount
            while (rs.next()) {
                val row: MutableMap<String, Any> = mutableMapOf()
                for (i in 1..numColumns) {
                    val tableName = rs.metaData.getColumnLabel(i)
                    var myValue = rs.getObject(i)
                    if (myValue == null) myValue = ""
                    if(myValue is Timestamp) myValue = excelDateFormatter(myValue)
                    row[tableName] = myValue
                }
                result.add(row)
            }
        })
        if (result.isEmpty())
            throw DataNotFoundException()

        return ObjectMapper().writeValueAsString(result)
    }


    override fun getTableWhere(): MutableMap<String, MutableList<String>> {
        val tableWhere: MutableMap<String, MutableList<String>> = mutableMapOf()

        val query = "SELECT DISTINCT table_name, column_name " +
                "FROM report_custom_where " +
                "ORDER BY table_name, column_name"

        jdbc.query(query) { rs, _ ->
            val table = rs.getString("table_name")
            val column = rs.getString("column_name")
            tableWhere.computeIfAbsent(table) { mutableListOf() }.add(column)
        }
        return tableWhere
    }

    override fun getTableSelect(): MutableList<String> {
        val tableSelect: MutableList<String> = mutableListOf()
        val query = "SELECT DISTINCT define_name FROM report_custom_define"

        jdbc.query(query) { rs, _ ->
            val table = rs.getString("define_name")
            tableSelect.add(table)
        }

        return tableSelect
    }

    override fun getTableFavorite(uDomain: String): MutableList<FavoriteResponse> {
        val tableFavorite = mutableListOf<FavoriteResponse>()
        val query =
            "SELECT DISTINCT favorite_name, favorite_query,favorite_select, favorite_condition FROM report_custom_favorite WHERE favorite_owner = '${uDomain}'"

        jdbc.query(query) { rs ->
            val name = rs.getString("favorite_name")
            val kuery = rs.getString("favorite_query")
            val select = separateSelectCondition(rs.getString("favorite_select"))
            val conditionRaw = rs.getString("favorite_condition")
            val condition: MutableMap<String, MutableMap<String, Any>> = separateConditionMap(conditionRaw)

            val fav = FavoriteResponse(name, kuery, select, condition)

            tableFavorite.add(fav)
        }

        return tableFavorite.ifEmpty { throw DataNotFoundException() }
    }

    override fun getFavoriteQueryByName(favoriteName: String): String {
        var favoriteQuery = buildString { }
        val syntax =
            "SELECT FAVORITE_QUERY FROM REPORT_CUSTOM_FAVORITE WHERE FAVORITE_NAME = '${favoriteName}'"
        jdbc.query(syntax) { rs ->
            favoriteQuery = rs.getString(1)
        }
        return if (!favoriteQuery.isNullOrBlank()) favoriteQuery else throw DataNotFoundException()
    }

    override fun addTableFavorite(
        user: String,
        name: String,
        query: String,
        select: MutableList<String>,
        condition: MutableMap<String, MutableMap<String, Any>>
    ): Boolean {
        var successFlag = false

        val params = ArrayList<Any?>()
        val syntax = buildString {
            append("INSERT INTO report_custom_favorite ")

            append("(favorite_owner, favorite_name, favorite_query, favorite_select, favorite_condition) ")
            params.addAll(listOf(user, name, query, joinSelectCondition(select), joinConditionMap(condition)))

            append("VALUES (?,?,?,?,?) ")
        }

        if (jdbc.update(syntax, *params.toTypedArray()) != 0) {
            successFlag = true
        }
        return successFlag
    }

    override fun getAllDefineColumn(): List<String> {
        val query =
            "SELECT DEFINE_NAME FROM REPORT_CUSTOM_DEFINE"
        return jdbc.queryForList(query, String::class.java)
    }

    override fun deleteTableFavorite(user: String, name: String): Boolean {
        var successFlag = false

        val params = ArrayList<Any?>()
        val query = buildString {
            append("DELETE FROM report_custom_favorite ")

            append("WHERE favorite_owner = ? ")
            params.add(user)

            append("AND favorite_name = ?")
            params.add(name)
        }

        if (jdbc.update(query, *params.toTypedArray()) != 0) {
            successFlag = true
        }
        return successFlag
    }

    fun excelDateFormatter(timestamp: Timestamp): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(timestamp)
    }

    fun separateConditionMap(raw: String): MutableMap<String, MutableMap<String, Any>> {
        val mapType = object : TypeToken<MutableMap<String, MutableMap<String, Any>>>() {}.type
        return Gson().fromJson(raw, mapType)
//        return Json.decodeFromString(raw)
    }

    fun joinConditionMap(raw: MutableMap<String, MutableMap<String, Any>>): String {
        return Gson().toJson(raw)
//        return Json.encodeToString(raw)
    }

    fun joinSelectCondition(raw: MutableList<String>): String {
        return raw.joinToString(separator = ",")
    }

    fun separateSelectCondition(raw: String): MutableList<String> {
        return raw.split(",").toMutableList()
    }

}
