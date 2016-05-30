package it.scrs.miner;


import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

import it.scrs.miner.models.Pairs;
import it.scrs.miner.util.HttpUtil;



@Service
public class ServiceMiner {

	// RestTemplate restTemplate=new RestTemplate();

	// TODO aggiungere nei parametri l'ip che arriva da fuori a cui deve essere fatta la richiesta
	@Async
	public Future<Pairs<String, Integer>> findMaxChainLevel(String uriMiner) throws Exception {
		String s = HttpUtil.doGet(uriMiner);
		Integer level = Integer.decode(s);
		return new AsyncResult<Pairs<String, Integer>>(new Pairs<String, Integer>(uriMiner, level));
	}
}
