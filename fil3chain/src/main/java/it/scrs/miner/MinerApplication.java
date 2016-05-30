package it.scrs.miner;


import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import it.scrs.miner.util.HttpUtil;
import it.scrs.miner.util.JsonUtility;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;

import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.transaction.TransactionRepository;
import it.scrs.miner.dao.user.User;
import it.scrs.miner.dao.user.UserRepository;
// import java.util.logging.Logger;
import it.scrs.miner.models.Pairs;



@SpringBootApplication
@ComponentScan("it.scrs.miner")
@EnableAutoConfiguration
public class MinerApplication implements CommandLineRunner {

	public static final Boolean testMiner = Boolean.TRUE;
	private static final Logger log = LoggerFactory.getLogger(MinerApplication.class);
	private static final Integer minerReq = 3;// TODO Mettere nel properties
	private static final Integer blockReq = 3;// TODO Mettere nel properties

	@Autowired
	private BlockRepository blockRepository;

	@Autowired
	private ServiceMiner serviceMiner;


	@Override
	public void run(String... args) throws Exception {

		Miner miner = new Miner();
		miner.loadNetworkConfig();
		miner.firstConnectToEntryPoint();

		// Mino per prova
		Block block = miner.generateBlock(null, 17, 31, null, null, null);
		System.out.println(block.generateAndGetHashBlock());

		// prendi minerReq ip a caso dalla lista dei miner
		List<String> ipMiners = miner.getIpPeers();
		// Lista contenente le richieste asincrone ai 3 ip
		List<Future<Pairs<String, Integer>>> minerResp = new ArrayList<Future<Pairs<String, Integer>>>();
		Integer myChainLevel = 0;

		while (!ipMiners.isEmpty()) {
			// Chiedi al db il valora del mio Max chainLevel(query fatta)
			// myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel();
			// Finche non sono aggiornato(ovvero mi rispondono con stringa
			// codificata o blocco fittizio)
			// Prendo 3 ip random da tutta la lista di Ip che mi sono stati inviati
			for (int i = 0; i < minerReq; i++) {
				Double x = Math.random() * ipMiners.size();
				minerResp.add(serviceMiner.findMaxChainLevel("http://192.168.0.107:8080/fil3chain/updateAtMaxLevel"));
			}

			Boolean flag = Boolean.TRUE;

			// Oggetto che contiene la coppia IP,ChainLevel del Miner designato
			Pairs<String, Integer> designedMiner = new Pairs<String, Integer>();

			while (flag) {
				// Controlliamo se uno dei nostri messaggi di richiesta è tornato
				// indietro con successo
				for (Future<Pairs<String, Integer>> f : minerResp) {
					// facciamo un For per ciclare tutte richieste attive
					// all'interno del nostro array e controlliamo se
					// sono arrivate le risposte

					if (f.isDone()) {
						if (f.get().getValue2() <= myChainLevel)
							ipMiners.remove(f.get().getValue1());
						else {
							// IP del miner designato da cui prendere la blockchain
							designedMiner.setValue1(f.get().getValue1());
							// ChainLevel del miner designato
							designedMiner.setValue2(f.get().getValue2());
							flag = Boolean.FALSE;
						}
					}
				}
			}

			System.out.println("Il Miner designato= " + designedMiner.getValue1() + " ChainLevel= " + designedMiner.getValue2() + "\n");

			// Aggiorno la mia blockChain con i blocchi che mi arrivano in modo incrementale
			// TODO realizzare la variante di chidere N blocchi tutti insieme
			while (myChainLevel < designedMiner.getValue2()) {
				// TODO cambire la uri di richiesta
				myChainLevel++;
				List<Block> blockResponse = HttpUtil.doGetJSON("http://192.168.0.107:8080/fil3chain/getBlock?chainLevel=" + myChainLevel);
				for (Block b : blockResponse) {
					// TODO qui dentro ora posso salvare nel mio DB tutti i blocchi appena ricevuti e verificarli
					blockRepository.save(b);
					System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");
				}

			}
			System.out.println("2");
			// aspetta una risposta
			// verifico i blocchi e aggiungo al db

			// mi connetto al primo che rispondi si e gli chiedo 10 blocchi o meno
			// chiusi dal blocco fittizio

			// SO SINCRONIZZATO ORA MINO DOPO AVER PREMUTO START
		}
	}

	public static void main(String[] args) {

		SpringApplication.run(MinerApplication.class);

	}


	public class ThreadControllerP2P {

		private ManageP2P rp = new ManageP2P();
		private Thread tp = new Thread(rp);


		public void startThread() {

			tp.start();
		}

		public void stopThread() {

			rp.stopRunning();
		}
	}

	public class ThreadControllerMiner {

		private ManageMine rm = new ManageMine();
		private Thread tm = new Thread(rm);


		public void startThread() {

			tm.start();
		}

		public void stopThread() {

			rm.stopRunning();
		}
	}

}

// @Bean
// public CommandLineRunner demo(BlockRepository repository,
// TransactionRepository transactionRepository, UserRepository
// userRepository) {
//
// return (args) -> {
// User user = new User("publicKey", "privcateKey", "Ivan", "Lupo",
// "ciccio@suker.com", "username");
// userRepository.save(user);
//
// Transaction t = new Transaction();
// transactionRepository.save(t);
//
// List<Transaction> lt = new ArrayList<Transaction>();
// // save a couple of block
// Block b = new Block("time", "MerkleRootHashq0dj8j3", "HashPrevius'ir2fc9j",
// 17, 35698, 0);
// b.getTransactionsContainer().add(t);
// repository.save(b);
//
// // fetch all block
// log.info("block found with findAll():");
// log.info("-------------------------------");
//
// for (Block block : repository.findAll()) {
// System.err.println(block.toString());
// }
// log.info("");
// /*
// * // fetch an individual customer by ID Customer customer =
// repository.findOne(1L);
// * log.info("Customer found with findOne(1L):");
// log.info("--------------------------------");
// * log.info(customer.toString()); log.info(""); // fetch customers by last
// name
// * log.info("Customer found with findByLastName('Bauer'):");
// * log.info("--------------------------------------------"); for (Customer
// bauer :
// * repository.findByLastName("Bauer")) { log.info(bauer.toString()); }
// log.info("");
// */
// };
// }
