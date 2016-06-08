package it.scrs.miner.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import it.scrs.miner.util.JsonUtility;

import it.scrs.miner.models.Pairs;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;




/** classe di utilita per effettuare richieste http verso il sistema legacy */
public class HttpUtil {

	private static final int TIMEOUT_MILLIS = 3000;


	/**
	 * esegue una richiesta get
	 * 
	 * @param url
	 *            url a cui inviare la richiesta
	 * @return risposta ricevuta
	 * @throws possibili
	 *             errori di comunicazione HTTP
	 */
	public static String doGet(String url) throws Exception {

		HttpPost request = new HttpPost(url);

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_MILLIS).setConnectTimeout(TIMEOUT_MILLIS).setConnectionRequestTimeout(TIMEOUT_MILLIS).build();

		request.setConfig(requestConfig);

		HttpClient client = HttpClientBuilder.create().build();

		HttpResponse response;
		response = client.execute(request);
		BufferedReader rd;
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	/**
	 * esegue una richiesta get
	 * 
	 * @param url
	 *            url a cui inviare la richiesta
	 * @return risposta ricevuta
	 * @throws possibili
	 *             errori di comunicazione HTTP
	 */
	public static <T> T doGetJSON(String url, Type t) throws IOException {

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		 RequestConfig requestConfig = RequestConfig.custom()
		 .setSocketTimeout(TIMEOUT_MILLIS)
		 .setConnectTimeout(TIMEOUT_MILLIS)
		 .setConnectionRequestTimeout(TIMEOUT_MILLIS)
		 .build();
		
		 request.setConfig(requestConfig);

		HttpResponse response;
		response = client.execute(request);
		BufferedReader rd;
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return JsonUtility.fromJson(result.toString(), t);
	}

	/**
	 * esegue una richiesta post
	 * 
	 * @param url
	 *            url a cui inviare la richiesta
	 * @return risposta ricevuta
	 * @throws possibili
	 *             errori di comunicazione HTTP
	 */
	public static String doPost(String url, String raw_data) throws Exception {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
//		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//		for (Pairs<?, ?> p : parameters) {
//			urlParameters.add(new BasicNameValuePair(p.getValue1().toString(), p.getValue2().toString()));
//		}
		post.setEntity(new StringEntity(raw_data));
		HttpResponse response = client.execute(post);
		BufferedReader rd;
		rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

}
