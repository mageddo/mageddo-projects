package com.mageddo.hawtio;

import io.hawt.web.proxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ConditionalOnProperty(value = "spring.hawtio.enabled", matchIfMissing = true)
@Controller
@RequestMapping("/hawtio")
public class HawtioConfig implements WebMvcConfigurer {

	@Value("${:classpath:/hawtio-static/index.html}")
	private Resource index;

	@Value("${spring.hawtio.proxyWhitelist:}")
	private String proxyWhitelist;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/hawtio/**")
			.addResourceLocations("classpath:/hawtio-static/");
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean(){
		final ServletRegistrationBean registrator = new ServletRegistrationBean(new ProxyServlet(), "/hawtio/proxy/*");
		if(!proxyWhitelist.isEmpty()){
			registrator.addInitParameter("proxyWhitelist", proxyWhitelist);
		}
		return registrator;
	}

	@GetMapping(value = {"", "/"}, produces = MediaType.TEXT_HTML_VALUE)
	public String redirect(final HttpServletRequest request) {
		return getIndexHtmlRedirect(request);
	}

	@GetMapping(value = {"/jmx/*", "/jvm/*"}, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public ResponseEntity actions() throws IOException {
		return ResponseEntity.ok(new InputStreamResource(index.getInputStream()));
	}

	protected String getIndexHtmlRedirect(final HttpServletRequest request) {
		final ServletUriComponentsBuilder builder = ServletUriComponentsBuilder
			.fromRequest(request);
		return "redirect:" + builder.path("/index.html").build().toString();
	}
}