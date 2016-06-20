package it.scrs.miner.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


//TODO Controllare se li prende o sostituire con application properties

@Configuration
@PropertySource("classpath:/network.properties")
public class ApplicationConfig {

//	@Value("${jpa.database.url}")
//	private String database;

	@Autowired
	Environment env;


	@Bean
	@ConfigurationProperties(prefix = "custom.rest.connection")
	public HttpComponentsClientHttpRequestFactory customHttpRequestFactory()

	{	// se li prende queso ezzo è inutile perche li carica da application/network properties
		//vedere se i tempi cambiano
		System.out.println("test "+Integer.parseInt(env.getProperty("custom.rest.connection.connection-request-timeout")));
		HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory();
		rf.setConnectionRequestTimeout(Integer.parseInt(env.getProperty("custom.rest.connection.connection-request-timeout")));
		rf.setConnectTimeout(Integer.parseInt(env.getProperty("custom.rest.connection.connect-timeout")));
		rf.setReadTimeout(Integer.parseInt(env.getProperty("custom.rest.connection.read-timeout")));
		return rf;
	}

	@Bean
	public RestTemplate customRestTemplate() {
		System.out.println("REST TEMPLATE");
		return new RestTemplate(customHttpRequestFactory());
	}
}
