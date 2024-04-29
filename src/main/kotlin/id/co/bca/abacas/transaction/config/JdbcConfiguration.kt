package id.co.bca.abacas.transaction.config


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
@ComponentScan("id.co.bca.abacas.transaction")
class JdbcConfiguration {

    @Bean
    fun jdbcTemplate(dataSource: DataSource?): JdbcTemplate {
        val jdbcTemplate = JdbcTemplate(dataSource!!)
        jdbcTemplate.isResultsMapCaseInsensitive = true
        return jdbcTemplate
    }
}
