package it.scrs.miner.ui.config;

import java.util.Iterator;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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
public class Development extends AUiConfig{
	//Stringa raprpesentante il path delle risorse associate all'ambiente
	private String prefix;
	//Stringa rappresentate l'estensione dei file view
	private String suffix;
	//Array contentente una lista di risorse
	@NestedConfigurationProperty
	private List<Resource> resources;

	@Override
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	@Override
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	@Override
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	
	@Override
	public String toString() {
		Resource resource;
		String toString= "Development [prefix=" + prefix + ", suffix=" + suffix + ", resources=[\n"; 
		Iterator<Resource> resourceIterator = resources.iterator();
		while (resourceIterator.hasNext()) {
			resource = resourceIterator.next();
			toString+= resource.toString()+"\n";
		} 
		toString += "]";
		return toString;
	}
}
