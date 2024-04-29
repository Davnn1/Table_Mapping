package id.co.bca.abacas.transaction.config


import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import java.util.*

object Config: MessageSourceAware {
    private var msgSource: MessageSource? = null
    val supportedLanguage = mapOf(
        "indonesian" to Locale("in"),
        "english" to Locale("en")
    )

    override fun setMessageSource(messageSource: MessageSource) {
        this.msgSource = messageSource
    }
}