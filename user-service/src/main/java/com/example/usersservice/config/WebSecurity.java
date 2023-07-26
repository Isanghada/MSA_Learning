package com.example.usersservice.config;

import com.example.usersservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    // 권한과 관련된 작업
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();  // users 요청은 전부 허용
        http.authorizeRequests()
                .antMatchers("/error/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/welcome").permitAll()
                .antMatchers("/health_check").permitAll()
                .antMatchers("/**")
                    .hasIpAddress("127.0.0.1") // IP 변경
                    .and()
                    .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();    // h2 접근을 위해 적용.
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
        return authenticationFilter;
    }

    // 인증과 관련된 작업
    // email이 동일한 데이터의 pwd 확인
    // 입력된 pwd와 암호화된 pwd 비교
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
