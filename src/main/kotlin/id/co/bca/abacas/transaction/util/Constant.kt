package id.co.bca.abacas.transaction.util

object Constant {
    var SQL_DATE_FORMAT = "dd-MMM-yy"
    var PAGEABLE_NO_PAGGING = Int.MAX_VALUE
}

object Path {
    object AREA{
        const val BASE_AREA = "/area"
        const val GET_AREA = BASE_AREA

    }
    const val AREA_HOLIDAY = "/area/holiday"
    const val CUSTOMER = "/customer"
    const val CUSTOMER_GROUP = "/customerGroup"
    const val CUSTOMER_EFES = "/customer/efes"
    const val VENDOR = "/vendor"
    const val TERMINAL = "/terminal"
    const val CARD = "/card"
}

