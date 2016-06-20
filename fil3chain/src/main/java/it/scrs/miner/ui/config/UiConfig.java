package it.scrs.miner.ui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Basic properties for Ui module
 * @author ivan18
 */

@ConfigurationProperties(
		value = "ui",
		locations = "classpath:configurations/ui.properties",
		exceptionIfInvalid = true
		)
public class UiConfig {
	//Stringa rappresentante l'ambiente da utilizzare
	private String environment;
	
	
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	

}