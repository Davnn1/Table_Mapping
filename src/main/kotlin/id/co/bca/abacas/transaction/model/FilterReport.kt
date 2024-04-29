package id.co.bca.abacas.transaction.model

data class FilterReport(
    var customerGroup:String?=null,
    var customerCode:String?=null,
    var vendorName:String?=null,
    var serviceType:String?=null,
    var accountNumber:String?=null,
)
