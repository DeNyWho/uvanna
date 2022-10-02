package com.example.uvanna.config

//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
//import org.springframework.security.config.http.SessionCreationPolicy
//import org.springframework.security.authentication.AuthenticationManager
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.web.cors.CorsConfiguration
//import org.springframework.web.cors.CorsConfigurationSource
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource
//import java.util.*
//import org.springframework.security.config.annotation.web.builders.WebSecurity


//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//class WebSecurityConfig : WebSecurityConfigurerAdapter() {
//
//    @Autowired
//    lateinit var userService: UserService
//
//    @Autowired
//    lateinit var unauthorizedHandler: JwtAuthEntryPoint
//
//    @Bean
//    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
//
//    @Bean
//    fun authenticationJwtTokenFilter(): JwtAuthTokenFilter {
//        return JwtAuthTokenFilter()
//    }
//
//    @Throws(Exception::class)
//    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
//        authenticationManagerBuilder
//                .userDetailsService(userService)
//                .passwordEncoder(bCryptPasswordEncoder())
//    }
//
//    @Bean
//    @Throws(Exception::class)
//    override fun authenticationManagerBean(): AuthenticationManager {
//        return super.authenticationManagerBean()
//    }
//
//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val configuration = CorsConfiguration()
//        configuration.allowedOrigins = listOf("http://localhost:8054", "http://localhost:8080")
//        configuration.allowedHeaders = listOf("*")
//        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
//        configuration.allowCredentials = true
//        configuration.maxAge = 3600
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", configuration)
//        return source
//    }
//
//    @Throws(Exception::class)
//    override fun configure(http: HttpSecurity) {
//        http
//                .cors().and()
//                .csrf().disable().authorizeRequests()
//            .anyRequest().permitAll()
//                .and()
//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
//        http.headers().cacheControl().disable()
//    }
//
//    @Throws(Exception::class)
//    override fun configure(web: WebSecurity) {
//        web.ignoring().antMatchers("/api2/signin", "/api2/signup", "/api2/registrationConfirm", "/api2/manga/**", "/api2/manga/popular", "/swagger-ui/index.html#/", "/api2/user/favourite/", "/api2/user/favourite/**")
//    }
//}