
package it.scrs.miner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import it.scrs.miner.ui.config.UiConfig;

/**
 * Questa classe si occupa della configurazione di springMVC
 * in particolare, vengono settati i percorsi per le risorse statiche ed 
 * il componente che si occupa del reperimento delle viste html
 * 
 * @author ivan18
 */

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties({UiConfig.class}) 
public class MvcConfig extends WebMvcConfigurerAdapter {
	@Autowired
	UiConfig uiCfg;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//Aggiunta di un view controller che al path / associa la view index
		registry.addViewController("/").setViewName("index");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/bower_components/**")
		.addResourceLocations("classpath:/static/development/bower_components/");

		registry.addResourceHandler("/vendors/**")
		.addResourceLocations("classpath:/static/production/vendors/");

		registry.addResourceHandler("/styles/**")
		.addResourceLocations("classpath:/static/development/app/styles/");

		registry.addResourceHandler("/scripts/**")
		.addResourceLocations("classpath:/static/development/app/scripts/");

		registry.addResourceHandler("/views/**")
		.addResourceLocations("classpath:/static/development/app/views/");

		registry.addResourceHandler("/images/**")
		.addResourceLocations("classpath:/static/development/app/images/");

		registry.addResourceHandler("/icons/**")
		.addResourceLocations("classpath:/static/development/app/icons/");
	}

	@Bean
	public InternalResourceViewResolver viewResolver() throws Exception {
		String env;
		String uiPrefix;

		InternalResourceViewResolver resolver;
		
		
		System.out.println("Environment found: "+uiCfg.getEnvironment());
		env = uiCfg.getEnvironment();
		if(env.equals("development")){
			//uiPrefix = uiCfg.getPrefix();
		}else if(env.equals("production")){

		}else{
			throw new Exception("Configuration Environment Error");
		}

		resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/development/app/");
		resolver.setSuffix(".html");
		return resolver;
	}


}