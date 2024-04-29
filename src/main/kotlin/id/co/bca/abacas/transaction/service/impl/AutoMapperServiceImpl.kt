package id.co.bca.abacas.transaction.service.impl

import Node
import id.co.bca.abacas.transaction.dao.AutoMapperDAO
import id.co.bca.abacas.transaction.dto.auto_mapper.*
import id.co.bca.abacas.transaction.model.DefineModel
import id.co.bca.abacas.transaction.model.TableMapModel
import id.co.bca.abacas.transaction.service.AutoMapperService
import id.co.bca.abacas.transaction.util.exception.BadArgumentsException
import id.co.bca.abacas.transaction.util.exception.DataNotFoundException
import org.springframework.stereotype.Service

@Service
class AutoMapperServiceImpl(val dao: AutoMapperDAO) : AutoMapperService {
    override fun getMap(columns: String, columnsValue: List<ColumnValue>): MapResponse {
        val outputColumn = getOutputColumn(columns, columnsValue)
        val realColumn = columns.split(";").mapNotNull {
            dao.getDefinedTableColumn(it)
        }
        if (realColumn.size == 1) {
            val definedTableColumn = dao.getDefinedTableColumn(outputColumn[0])!!
            return dao.getOutputToJson(
                definedTableColumn.tableName,
                definedTableColumn.columnName
            )
        }

        var theRawMap = dao.getTableMap()
        var definedStart: List<DefineModel>
        var empty: Boolean = false
        var definedGoal: List<DefineModel>
        var definedSelect: List<DefineModel>

        if (columnsValue.isEmpty()) {
            definedStart = listOf<DefineModel>(dao.getDefinedTableColumn(outputColumn[0])!!)
            empty = true
            val temp = outputColumn.mapNotNull {
                dao.getDefinedTableColumn(it)
            }

            definedGoal = temp.drop(1)
            definedSelect = realColumn
        } else {
            definedStart = columnsValue.mapNotNull {
                dao.getDefinedTableColumn(it.name)
            }
            //check where conditions
//            val whereCondition = dao.getTableWhere()
//            definedStart.forEach { input ->
//                val matchFound = whereCondition[input.tableName]?.contains(input.columnName) == true
//                if (!matchFound) {
//                    throw DataNotFoundException()
//                }
//            }

            definedGoal = outputColumn.mapNotNull {
                dao.getDefinedTableColumn(it)
            }

            definedSelect = realColumn
        }

        val paths = mutableListOf<MutableList<String>>()
        definedStart.forEach { start ->
            definedGoal.forEach { goal ->
                bfs(theRawMap, start.tableName, goal.tableName)?.let { path ->
                    paths.add(path)
                }
            }
        }

        if (paths.isEmpty() || definedSelect.isEmpty() || definedStart.isEmpty() || definedGoal.isEmpty()) throw DataNotFoundException()

        val query =
            generateQuery(paths, definedSelect, definedStart, theRawMap, columnsValue, empty)
        return MapResponse(query, dao.getOutputToJson2(query))
    }

    fun <T> ArrayList<T>.enqueue(value: T) {
        this.add(value)
    }

    fun <T> ArrayList<T>.dequeue(): T {
        val value = this[0]
        this.removeAt(0)
        return value
    }

    override fun getWhere(): WhereResponse {
        val rawWhere = dao.getTableWhere()

        return WhereResponse(rawWhere)
    }

    override fun getSelect(): SelectResponse {
        val rawSelect = dao.getTableSelect()

        return SelectResponse(rawSelect)
    }

    override fun getFavorite(uDomain: String): MutableList<FavoriteResponse> {
        return dao.getTableFavorite(uDomain)
    }

    override fun addFavorite(
        user: String,
        name: String,
        query: String,
        select: MutableList<String>,
        condition: MutableMap<String, MutableMap<String, Any>>
    ): String {
        //check condition
        condition.entries.forEach { (outerKey, outerValue) ->
            if (outerValue["condition"] == null || outerValue["values"] == null) {
                throw BadArgumentsException()
            }
        }
        val resultQuery = autoMapperQuery(select, objectRemapper(condition))
        val successFlag: Boolean = dao.addTableFavorite(user, name, resultQuery, select, condition)
        return if (successFlag) "Success, Data Added" else "Failed to add favorite"
    }

    override fun deleteFavorite(uDomain: String, name: String): String {
        val successFlag: Boolean = dao.deleteTableFavorite(uDomain, name)
        return if (successFlag) "Success, Data Deleted" else "Failed to delete favorite"
    }

    override fun getDefine(): List<String> {
        return dao.getAllDefineColumn()
    }

    override fun getExcelByFavoriteName(favoriteName: String): ExcelResponse {
        val favoriteQuery: String = dao.getFavoriteQueryByName(favoriteName)
        return ExcelResponse(dao.getOutputToJson2(favoriteQuery))
    }

    private fun autoMapperQuery(
        columns: MutableList<String>,
        columnsValue: List<ColumnValue>
    ): String {
        val outputColumn = columns.filter { col ->
            columnsValue.none { (it.name == col) }
        }
        var theRawMap = dao.getTableMap()
        var definedStart: List<DefineModel>
        var empty: Boolean = false
        var definedGoal: List<DefineModel>
        var definedSelect: List<DefineModel>

        definedStart = columnsValue.mapNotNull {
            dao.getDefinedTableColumn(it.name)
        }

        definedGoal = outputColumn.mapNotNull {
            dao.getDefinedTableColumn(it)
        }

        definedSelect = columns.mapNotNull {
            dao.getDefinedTableColumn(it)
        }

        val paths = mutableListOf<MutableList<String>>()
        definedStart.forEach { start ->
            definedGoal.forEach { goal ->
                bfs(theRawMap, start.tableName, goal.tableName)?.let { path ->
                    paths.add(path)
                }
            }
        }

        if (paths.isEmpty() || definedSelect.isEmpty() || definedStart.isEmpty() || definedGoal.isEmpty()) throw DataNotFoundException()

        return generateQuery(paths, definedSelect, definedStart, theRawMap, columnsValue, empty)
    }

    private fun objectRemapper(condition: MutableMap<String, MutableMap<String, Any>>): List<ColumnValue> {
        val conditionPair = condition.flatMap { entry ->
            val name = entry.key
            val conditionValue = entry.value
            val conditionKey = conditionValue["condition"].toString()
            val value = conditionValue["values"] as List<String>
            listOf(ColumnValue(name = name, condition = conditionKey, values = value))
        }
        return conditionPair
    }

    private fun bfs(tree: ArrayList<TableMapModel>, start: String, end: String): MutableList<String>? {
        var counter = 0
        var isFound = false
        var endNode: Node? = null
        val node = Node()
        node.value = start

        val queue = ArrayList<Node>()
        queue.add(node)

        while (counter < 10 && !isFound) {
            val tempQueue = ArrayList<Node>()
            while (queue.isNotEmpty() && !isFound) {
                var currentNode = queue.dequeue()

                val filteredMap = tree.filter {
                    // filter tabel start bisa kemana aja
                    it.tableName1 == currentNode.value
                }.toMutableList()

                tree.filter {
                    // filter tabel start bisa kemana aja
                    it.tableName2 == currentNode.value
                }.forEach {
                    filteredMap.add(
                        TableMapModel(
                            it.tableName2,
                            it.tableName1,
                            it.columnNameSecond,
                            it.columnNameFirst
                        )
                    )
                }

                for (table in filteredMap) {
                    if (table.tableName2 == end) {
                        isFound = true
                        endNode = Node(end, currentNode)
                        break;
                    }
                    tempQueue.add(Node(value = table.tableName2, parent = currentNode))
                    currentNode.child.add(Node(value = table.tableName2, parent = currentNode))
                }
            }
            queue.addAll(tempQueue)
            counter++
        }
//        if (!isFound)
//            println("found $end, from $start, not found")
//
//        println("\nfound $end, from $start, with $counter step(s)")

        val result = mutableListOf<String>()

//        print("with path:")
        while (endNode?.parent != null) {
//            print(endNode.value + " - ")


            try {
                tree.filter {
                    it.tableName1 == endNode?.value && it.tableName2 == endNode?.parent?.value
                }[0].let {
//              result.add("${it.tableName1}.${it.columnName1}|${it.tableName2}.${it.columnName2}")
                    result.addAll(
                        arrayOf(it.tableName1, it.tableName2)
                    )
                }
            } catch (e: Exception) {
                tree.filter {
                    it.tableName2 == endNode?.value && it.tableName1 == endNode?.parent?.value
                }[0].let {
//              result.add("${it.tableName1}.${it.columnName1}|${it.tableName2}.${it.columnName2}")
                    result.addAll(
                        arrayOf(it.tableName1, it.tableName2)
                    )
                }
            }
            endNode = endNode.parent
        }
//        print(endNode?.value)
        return result
    }

    fun generateQuery(
        paths: List<List<String>>,
        selects: List<DefineModel>,
        starts: List<DefineModel>,
        tree: ArrayList<TableMapModel>,
        columnsValue: List<ColumnValue>,
        empty: Boolean
    ): String {
        val query = StringBuilder()

        //select statement
        query.append("SELECT ")
        query.append(selects.joinToString(", ") { "${it.tableName}.${it.columnName} AS ${it.tableName}_${it.columnName}" })
        query.append(" ")

        //from statement

        query.append("FROM ${paths[0][1]} ")

        //join statement
        val joinedTables = mutableSetOf<String>()
        paths.forEach { path ->
            path.drop(1).forEachIndexed { i, tableName ->
                val previousTableName = path[i]
                if (joinedTables.contains("$previousTableName-$tableName")) {
                    return@forEachIndexed
                }
                tree.find { it.tableName2 == tableName && it.tableName1 == previousTableName }?.let { temp ->
                    query.append("JOIN $previousTableName ON $tableName.${temp.columnNameSecond} = $previousTableName.${temp.columnNameFirst} ")
                    joinedTables.add("$previousTableName-$tableName")
                }
            }
        }

        if (!empty) {
            //where statement
            query.append("WHERE ")
            if (starts.size == 1) {
                val values = columnsValue[0].values
                val condition = columnsValue[0].condition

                when (condition) {
                    "=" -> {
                        query.append("${starts[0].tableName}.${starts[0].columnName} IN (")
                        values?.joinTo(query, separator = "', '", prefix = "'", postfix = "'")
                        query.append(")")
                    }

                    "!=" -> {
                        var isNull: Boolean = false
                        values.forEach { value ->
                            if (value == "NULL" || value == "null") {
                                isNull = true
                            }
                        }
                        if (isNull) {
                            query.append("${starts[0].tableName}.${starts[0].columnName} IS NOT NULL")
                        } else {
                            query.append("${starts[0].tableName}.${starts[0].columnName} NOT IN (")
                            values?.joinTo(query, separator = "', '", prefix = "'", postfix = "'")
                            query.append(")")
                        }
                    }

                    "<", ">", "<=", ">=" -> {
                        if (values != null && values.size > 1) {
                            throw BadArgumentsException()
                        }
                        query.append("${starts[0].tableName}.${starts[0].columnName} $condition '${values?.get(0)}'")
                    }

                    else -> throw BadArgumentsException()
                }
            } else {
                val valuesMap = mutableMapOf<String, List<*>>()
                val columnNames = starts.map { "${it.columnName.toLowerCase()}_${it.tableName.toLowerCase()}" }.toSet()

                columnsValue.filter { it.name in columnNames }
                    .forEach { valuesMap[it.name] = it.values as? List<*> ?: emptyList<String>() }

                starts.forEachIndexed { index, temp ->
                    val values = valuesMap["${temp.columnName.toLowerCase()}_${temp.tableName.toLowerCase()}"]
                        ?: return@forEachIndexed
                    val condition = columnsValue[index].condition as? String

                    if (index != 0) {
                        query.append(" AND ")
                    }

                    when (condition) {
                        "=" -> {
                            query.append(
                                "${temp.tableName}.${temp.columnName} IN (${
                                    values.joinToString(
                                        "', '", "'", "'"
                                    )
                                })"
                            )
                        }

                        "!=" -> {
                            var isNull: Boolean = false
                            values.forEach { value ->
                                if (value.toString() == "NULL" || value.toString() == "null") {
                                    isNull = true
                                }
                            }
                            if (isNull) {
                                query.append(
                                    "${temp.tableName}.${temp.columnName} IS NOT NULL"
                                )
                            } else {
                                query.append(
                                    "${temp.tableName}.${temp.columnName} NOT IN (${
                                        values.joinToString(
                                            "', '", "'", "'"
                                        )
                                    })"
                                )
                            }
                        }

                        "BETWEEN" -> {
                            query.append(
                                "${temp.tableName}.${temp.columnName} BETWEEN ${
                                    values.joinToString(
                                        "' AND '", "'", "'"
                                    )
                                }"
                            )
                        }

                        "<", ">", "<=", ">=" -> {
                            if (values.size > 1) {
                                throw BadArgumentsException()
                            }
                            query.append("${temp.tableName}.${temp.columnName} $condition '${values[0]}'")
                        }

                        else -> throw BadArgumentsException()
                    }
                }
            }
        }
        query.append(" ")
        return query.toString()
    }


    private fun getOutputColumn(columns: String, values: List<ColumnValue>): Array<String> {
        return columns.split(";").filter { col ->
            values.none { (it.name == col) }
        }.toTypedArray()
    }
}