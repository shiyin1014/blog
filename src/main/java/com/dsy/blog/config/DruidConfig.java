package com.dsy.blog.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created on 2020/4/2
 * Package com.dsy.blog.config
 *
 * @author dsy
 */
@Configuration
public class DruidConfig {

    @ConfigurationProperties("spring.datasource")
    @Bean
    public DataSource druid() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setFilters("stat");
        return druidDataSource;
    }

    // 主要实现web监控的配置处理
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(
                new StatViewServlet(), "/druid/*");//表示进行druid监控的配置处理操作
        //servletRegistrationBean.addInitParameter("allow", "127.0.0.1,192.168.0.108");//白名单
        //servletRegistrationBean.addInitParameter("deny", "192.168.121.234");//黑名单
        servletRegistrationBean.addInitParameter("loginUsername", "admin");//用户名
        servletRegistrationBean.addInitParameter("loginPassword", "admin");//密码
        servletRegistrationBean.addInitParameter("resetEnable", "false");//是否可以重置数据源
        return servletRegistrationBean;
    }


    //监控
    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");//所有请求进行监控处理
        filterRegistrationBean.addInitParameter("exclusions", "/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");//排除
        return filterRegistrationBean;
    }


}
