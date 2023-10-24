package com.example.redistest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate redisTemplate;

    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    private final JwtExceptionFilter jwtExceptionFilter;

    private final String[] RESOURCE_PATH = new String[] {
    		"/js/**"
			 , "/css/**"
			 , "/images/**"
			 , "/img/**"
			 , "/fonts/**"
			 , "/plugins/**"
			 , "/acc/**"
			 , "/error"
			 , "/favicon.ico"
     };

//    @Bean
//    @Order(0)
//    public SecurityFilterChain resources(HttpSecurity http) throws Exception {
//      return http.requestMatchers(matchers -> matchers.antMatchers(RESOURCE_PATH))
//    		  .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
//    		  .requestCache(RequestCacheConfigurer::disable)
//    		  .securityContext(AbstractHttpConfigurer::disable)
//    		  .sessionManagement(AbstractHttpConfigurer::disable)
//    		  .build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                    .csrf().disable()
                    .headers().frameOptions().disable()
                .and()
                    .cors().configurationSource(corsConfigurationSource())
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .antMatchers("/order/cancel", "/payment/api/refundUrl", "/").permitAll()
                        .antMatchers("/mypage/**", "/order/**", "/payment/**", "/auth/token-test").authenticated()
                        .anyRequest().permitAll()
                .and()
                     .exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
                    .formLogin().disable()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
                .build();
    }
//
//      protected void configure(HttpSecurity http) throws Exception {
//
//          http.csrf().disable();
//          http.httpBasic().disable()
//                  .authorizeRequests()// 요청에 대한 사용권한 체크
//                  .antMatchers("/auth/test").authenticated()
//                  .antMatchers("/**").permitAll()
//                  .and()
//                  .cors()
//                  .and()
//                  .exceptionHandling()
//                  .authenticationEntryPoint(unauthorizedHandler)
//                  .and()
//                  .addFilterBefore(new JwtAuthenticationFilter(this.jwtTokenProvider, this.redisTemplate),
//                          UsernamePasswordAuthenticationFilter.class)
//                  .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);
//
//      }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.addAllowedOrigin("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 암호화에 필요한 PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
