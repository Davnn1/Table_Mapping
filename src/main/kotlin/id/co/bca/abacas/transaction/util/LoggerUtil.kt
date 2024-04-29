package id.co.bca.abacas.transaction.util

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

fun getLogger(clazz: Class<*>): Log {
	return LogFactory.getLog(clazz)
}

/**
 * log debug
 * only print log on dev
 * */
fun Log.d(msg: String){
	val isDebuging = true
	if (isDebuging)
		this.info(msg)
}