package id.co.bca.abacas.transaction.controller

import id.co.bca.abacas.transaction.dto.auto_mapper.*
import id.co.bca.abacas.transaction.service.AutoMapperService
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"] )
@RestController
class AutoMapperController(val mapService: AutoMapperService) {
    @PostMapping(value = ["/getMap"])
    fun getMap(@RequestBody mapRequest: MapRequest): WrapperResponse<MapResponse> {
        val output: MapResponse =
            mapService.getMap(
                mapRequest.columns,
                mapRequest.columnsValues
            )
        return WrapperResponse.generateSuccess(output)
    }

    @GetMapping(value = ["/getWhere"])
    fun getWhereCondition(): WrapperResponse<WhereResponse> {
        val output: WhereResponse = mapService.getWhere()
        return WrapperResponse.generateSuccess(output)
    }

    @GetMapping(value = ["/getSelect"])
    fun getSelect(): WrapperResponse<SelectResponse> {
        val output: SelectResponse = mapService.getSelect()
        return WrapperResponse.generateSuccess(output)
    }

    @GetMapping(value = ["/getFavorite"])
    fun getFavorite(@RequestParam uDomain: String): WrapperResponse<MutableList<FavoriteResponse>> {
        val output: MutableList<FavoriteResponse> = mapService.getFavorite(uDomain)
        return WrapperResponse.generateSuccess(output)
    }

    @PostMapping(value = ["/addFavorite"])
    fun addFavorite(@RequestBody favoriteRequest: FavoriteRequest): WrapperResponse<*> {
        val output = mapService.addFavorite(
            favoriteRequest.user,
            favoriteRequest.name,
            favoriteRequest.query,
            favoriteRequest.select,
            favoriteRequest.condition
        )
        return WrapperResponse.generateSuccess(output)
    }

    @DeleteMapping(value = ["/deleteFavorite/{uDomain}/{favoriteName}"])
    fun deleteFavorite(@PathVariable uDomain: String, @PathVariable favoriteName: String): WrapperResponse<*> {
        val output = mapService.deleteFavorite(
            uDomain,
            favoriteName
        )
        return WrapperResponse.generateSuccess(output)
    }

    @GetMapping(value = ["/getExcel"])
    fun getExcelByFavoriteName(@RequestParam favoriteName: String): WrapperResponse<ExcelResponse> {
        val output: ExcelResponse = mapService.getExcelByFavoriteName(favoriteName)
        return WrapperResponse.generateSuccess(output)
    }
    @GetMapping(value = ["/getAllDefineName"])
    fun getAllDefineName(): WrapperResponse<List<String>> {
        return WrapperResponse.generateSuccess(mapService.getDefine())
    }
}
