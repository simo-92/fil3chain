package it.scrs.miner;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplate;

import it.scrs.miner.models.Pairs;




@Service
public class ServiceMiner {
	
	private int timeoutSeconds = 5;

	RestTemplate restTemplate=  new RestTemplate();
	
	@Async
	public Future<Pairs<String, Integer>> findMaxChainLevel(String uriMiner)  {

		loadConfiguration();

        System.out.println("Timeout: " + timeoutSeconds);

		SimpleClientHttpRequestFactory rf = ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory());
		rf.setReadTimeout(1000 * timeoutSeconds);
		rf.setConnectTimeout(1000 * timeoutSeconds);

		String s = "";
		Integer level = -1;
		try {
			System.out.println("\nRichiesta ad :" + uriMiner);
			s = restTemplate.getForObject("http://" + uriMiner + ":8080/fil3chain/updateAtMaxLevel", String.class);
//			s = HttpUtil.doGet("http://"+uriMiner+":8080/fil3chain/updateAtMaxLevel");
			level = Integer.decode(s);
			return new AsyncResult<>(new Pairs<>(uriMiner, level));
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("\nSono Morto: " + uriMiner + " Causa: " + e.getMessage());
			return null;
		}
		
	}

	private void loadConfiguration() {
		// Carica la configurazione
		Properties prop = new Properties();
		InputStream in = ServiceMiner.class.getResourceAsStream("/network.properties");
		try {
			prop.load(in);
			// Imposta il timeout
			this.timeoutSeconds = Integer.parseInt(prop.getProperty("timeoutSeconds", "3"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}



