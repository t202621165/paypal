package com.iwanol.paypal.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.iwanol.paypal.handler.AjaxAuthFailHandler;
import com.iwanol.paypal.handler.AjaxAuthSuccessHandler;
import com.iwanol.paypal.handler.UnauthorizedEntryPoint;


/**
 * 1.首先当我们要自定义Spring Security的时候我们需要继承自WebSecurityConfigurerAdapter来完成，相关配置重写对应 方法即可。 
 * 2.我们在这里注册CustomUserServiceImp的Bean，然后通过重写configure方法添加我们自定义的认证方式。
 * 3. 使用@EnableGlobalMethodSecurity(prePostEnabled = true)这个注解，可以开启security的注解，我们可以在需要控制权限的方法上面使用@PreAuthorize，@PreFilter这些注解。 
 * @author leo
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	/**
	 * 自定义UserDetailsService，从数据库中读取用户信息
	 * @return
	 */
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	private final String key = "iwanol";

	
	@Bean  
    public PasswordEncoder passwordEncoder() {  
        return new BCryptPasswordEncoder();   // 使用 BCrypt 加密
	}
	
	@Bean
    public AuthenticationProvider authenticationProvider() {  
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder); // 设置密码加密方式
        return authenticationProvider;  
    }
	
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
        		.exceptionHandling().authenticationEntryPoint(new UnauthorizedEntryPoint())
        		.and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/exception","/user/login","/login","/logout","/login-error","/assets/**","/core/**").permitAll()
                .anyRequest().authenticated()   //其他请求都需要授权
                .and()
                .formLogin()//基于 Form 表单登录验证
                .loginPage("/login")
                .loginProcessingUrl("/user/login")
                .usernameParameter("userName")
                .passwordParameter("passWord")
                .successHandler(new AjaxAuthSuccessHandler())
                .failureHandler(new AjaxAuthFailHandler()).permitAll()
                .and().logout().logoutUrl("/logout").clearAuthentication(true).invalidateHttpSession(true).logoutSuccessUrl("/")
                .and().rememberMe().key(key).tokenValiditySeconds(1209600) // 启用 remember me
                .and().exceptionHandling().accessDeniedPage("/403")//处理异常;拒绝访问就重定向到403页面
                .and().headers().frameOptions().sameOrigin();//允许使用frame访问同源域名中的页面
    }
    

	/**
	 * 认证信息管理
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}
}
