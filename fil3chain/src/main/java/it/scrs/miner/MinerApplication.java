package it.scrs.miner;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import it.scrs.miner.dao.block.BlockRepository;

import javax.swing.*;



@SpringBootApplication
@ComponentScan("it.scrs.miner")
@EnableAutoConfiguration
public class MinerApplication implements CommandLineRunner {

	private static final String IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

	public static final Boolean testMiner = Boolean.TRUE;

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(MinerApplication.class);

	@Autowired
	private BlockRepository blockRepository;

	@Autowired
	private ServiceMiner serviceMiner;


	@Override
	public void run(String... args) throws Exception {

		// Seleziona l'IP da utilizzare per la sessione corrente
		String myIp = selectIp();

		Miner miner = new Miner();
		miner.loadNetworkConfig();
                miner.loadKeyConfig(); // carica le chiavi dal file properties
                miner.loadMinerConfiguration(); //carica numero blocchi update
		miner.setIp(myIp);
		miner.firstConnectToEntryPoint();
                
		// Mino per prova
		// Block block = miner.generateBlock(null, 17, 31, null, null, null);
		// System.out.println(block.generateAndGetHashBlock());

		// prendi minerReq ip a caso dalla lista dei miner
		miner.initializeBlockChain(blockRepository);
		miner.updateFilechain(blockRepository, serviceMiner);
		System.out.println("3");
		// TODO Avviare MINING
	}

	public static void main(String[] args) {

		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(MinerApplication.class).headless(false);
		springApplicationBuilder.run(args);
		// springApplication.run(MinerApplication.class);
	}

	/**
	 * Permette di selezionare l'IP da utilizzare per la sessione corrente tramite un dialog.
	 * 
	 * @return
	 */
	private String selectIp() {

		ArrayList<String> ips = getAllIpAddresses();
		if (ips == null) {
			System.err.println("Non sei connesso a nessuna rete.");
			return null;
		}

		String input = (String) JOptionPane.showInputDialog(null, "Scegli il tuo indirizzo IP", "Lista IP", JOptionPane.QUESTION_MESSAGE, null, // Use
				// default
				// icon
				ips.toArray(), // Array of choices
				ips.get(0)); // Initial choice
		return input;
	}

	private ArrayList<String> getAllIpAddresses() {

		ArrayList<String> ips = new ArrayList<>();

		try {
			Enumeration<?> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<?> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().matches(IP_REGEX))
						ips.add(i.getHostAddress());
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}

		return ips;
	}

	/**
	 * @return the blockRepository
	 */
	public BlockRepository getBlockRepository() {

		return blockRepository;
	}

	/**
	 * @param blockRepository
	 *            the blockRepository to set
	 */
	public void setBlockRepository(BlockRepository blockRepository) {

		this.blockRepository = blockRepository;
	}

	/**
	 * @return the serviceMiner
	 */
	public ServiceMiner getServiceMiner() {

		return serviceMiner;
	}

	/**
	 * @param serviceMiner
	 *            the serviceMiner to set
	 */
	public void setServiceMiner(ServiceMiner serviceMiner) {

		this.serviceMiner = serviceMiner;
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
