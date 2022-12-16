package com.example.uvanna.config

//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
//import org.springframework.security.config.annotation.web.builders.WebSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
//import org.springframework.security.config.web.server.ServerHttpSecurity
//import org.springframework.security.web.server.SecurityWebFilterChain


//@Configuration
//@EnableWebFluxSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//class WebSecurityConfig : WebSecurityConfigurerAdapter() {
//
////    @Bean
////    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
////        return http
////            .authorizeExchange()
////            .pathMatchers(
////                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
////            .permitAll()
////            .anyExchange()
////            .authenticated()
////            .and()
////            .formLogin().loginPage("/login")
////            .and()
////            .build()
////    }
//
////    @Bean
////    fun corsConfigurationSource(): CorsConfigurationSource {
////        val configuration = CorsConfiguration()
////        configuration.allowCredentials = false
////        configuration.allowedOrigins = listOf("http://localhost:3000", "https://uvanna.vercel.app/")
////        configuration.allowedMethods = listOf(CorsConfiguration.ALL)
////        configuration.allowedHeaders = listOf(CorsConfiguration.ALL)
////        val source = UrlBasedCorsConfigurationSource()
////        source.registerCorsConfiguration("/**", configuration)
////        return source
////    }
//
//    @Throws(Exception::class)
//    override fun configure(web: WebSecurity) {
//        web.ignoring().antMatchers("/api/**", "/images/**")
//    }
//
//}

