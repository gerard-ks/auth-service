package dev.ks.authlayerarchitecture.config;

import dev.ks.authlayerarchitecture.filter.MDCFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public MDCFilter mdcFilter() {
        return new MDCFilter();
    }

    @Bean
    public FilterRegistrationBean<MDCFilter> mdcFilterRegistration(
            MDCFilter mdcFilter
    ) {
        FilterRegistrationBean<MDCFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(mdcFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");

        return registration;
    }
}
