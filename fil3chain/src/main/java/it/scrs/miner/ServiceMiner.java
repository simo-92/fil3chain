package it.scrs.miner;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.scrs.miner.dao.block.Block;
import it.scrs.miner.models.Pairs;
import it.scrs.miner.util.IP;



@Service
public class ServiceMiner {

	public static final Integer nReqProp = 5;// TODO al properties

	@Autowired
	RestTemplate restTemplate;

	private int timeoutSeconds;

	@Async
	public Future<Pairs<IP, Integer>> findMaxChainLevel(String uriMiner) {

		loadConfiguration();
//
//		SimpleClientHttpRequestFactory rf = ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory());
//		rf.setReadTimeout(1000 * 5);
//		rf.setConnectTimeout(1000 * 5);
//		restTemplate.setRequestFactory(rf);

		String result = "";
		Integer level = -1;
		Integer counter = 0;
		while (counter <= nReqProp) {
			try {
				System.out.println("\nRichiesta ad :" + uriMiner);
				result = restTemplate.getForObject("http://" + uriMiner + "/fil3chain/updateAtMaxLevel", String.class);
				level = Integer.decode(result);
				return new AsyncResult<>(new Pairs<>(new IP(uriMiner), level));
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("\nSono Morto: " + uriMiner + " Causa: " + e.getMessage());
				counter++;
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * @param uriMiner
	 * @return
	 */
	@Async
	public Future<String> pingUser(String uriMiner) {

		loadConfiguration();

//		SimpleClientHttpRequestFactory rf = ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory());
//		rf.setReadTimeout(1000 * 5);
//		rf.setConnectTimeout(1000 * 5);
//		restTemplate.setRequestFactory(rf);

		Integer counter = 0;
		while (counter <= nReqProp) {
			try {
				System.out.println("\nRichiesta ad :" + uriMiner);
				String response = restTemplate.postForObject("http://" + uriMiner + "/user_ping", null, String.class);
				return new AsyncResult<>(response);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("\nSono Morto: " + uriMiner + " Causa: " + e.getMessage());
				counter++;
			}

			// Aspetto prima della prossima richiesta
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * 
	 */
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
