package web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity  //класс ответвенный за секьюрити конфигурации
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;        //надо поменять инмемори на юрейз дитэйл, связать с юзером
    private final LoginSuccessHandler successUserHandler;

    public SecurityConfig(UserDetailsService userDetailsService,
                          LoginSuccessHandler successUserHandler) {

        this.userDetailsService = userDetailsService;
        this.successUserHandler = successUserHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {  //
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll() // доступность всем
                .antMatchers("/user/**").access("hasRole('ROLE_USER')")  //роль котороой будет достпупна информация
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")// по этому юрлу,   **- этой роли будет доступен любой адрес начинающийсяс с **
                .and().formLogin()
                .successHandler(successUserHandler);
        //.permitAll()
    }
}
