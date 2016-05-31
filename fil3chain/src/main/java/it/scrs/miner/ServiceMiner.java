package it.scrs.miner;


import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplate;

import it.scrs.miner.models.Pairs;




@Service
public class ServiceMiner {

	RestTemplate restTemplate=new RestTemplate();

	// TODO aggiungere nei parametri l'ip che arriva da fuori a cui deve essere fatta la richiesta
	@Async
	public Future<Pairs<String, Integer>> findMaxChainLevel(String uriMiner)  {
		String s="";
		Integer level=-1;
		try {
			System.out.println("\nRichiesta ad :" + uriMiner);
			s=restTemplate.getForObject("http://"+uriMiner+":8080/fil3chain/updateAtMaxLevel",String.class);
//			s = HttpUtil.doGet("http://"+uriMiner+":8080/fil3chain/updateAtMaxLevel");
			level = Integer.decode(s);
			return new AsyncResult<Pairs<String, Integer>>(new Pairs<String, Integer>(uriMiner, level));
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("\nSono Morto: " + uriMiner + " Causa: " + e.getMessage());
			return null;
		}
		
	}
}
