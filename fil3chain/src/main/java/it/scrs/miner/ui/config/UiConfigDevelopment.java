package it.scrs.miner.ui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Basic properties for profile development of Ui module
 * @author ivan18
 */

@ConfigurationProperties(
		value = "ui.development",
		locations = "classpath:configurations/ui.properties",
		exceptionIfInvalid = true,
		ignoreInvalidFields = false, 
		ignoreUnknownFields = false
		)
public class UiConfigDevelopment {
	//Stringa raprpesentante il path delle risorse associate all'ambiente
	private String prefix;
	//Stringa rappresentate l'estensione dei file view
	private String suffix;
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
}
