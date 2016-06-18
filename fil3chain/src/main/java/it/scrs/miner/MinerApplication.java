package it.scrs.miner;


import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.block.MerkleTree;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.transaction.TransactionRepository;
import it.scrs.miner.dao.user.User;
import it.scrs.miner.models.BlockChain;
import it.scrs.miner.util.PoolDispatcherUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;



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
	private TransactionRepository transRepo;
	@Autowired
	private ServiceMiner serviceMiner;


	@Override
	public void run(String... args) throws Exception {

		// Seleziona l'IP da utilizzare per la sessione corrente
		String myIp = selectIp();

		Miner miner = Miner.getInstance(myIp, blockRepository, serviceMiner);

		// Mino per prova
		// Block block = miner.generateBlock(null, 17, 31, null, null, null);
		// System.out.println(block.generateAndGetHashBlock());
		// prendi minerReq ip a caso dalla lista dei miner
		
	

		// Registro il miner per gli eventi
		MinersListenerRegister.getInstance().registerMiner(miner);

		// Update della block chain
		
		
		// System.out.println("3");

		// Prendo il mio ultimo blocco
		Block myLastBlock = blockRepository.findFirstByOrderByChainLevelDesc();

		User me = new User("", "Ciano", "Bug", "Miner", "Mail@Ndaro.it", "Cianone");

		// Inizializzo il nuovo blocco da minare
		Block block = new Block();
		block.setFatherBlockContainer(myLastBlock.getHashBlock());
		block.setChainLevel(myLastBlock.getChainLevel() + 1);
		block.setUserContainer(me);
		block.setMinerPublicKey(miner.getMyPublickKey());

		// Prendo le transazioni dal Pool Dispatcher
		List<Transaction> transactionsList = PoolDispatcherUtility.getTransactions();

		ArrayList<String> hashTransactions = new ArrayList<>();
		for (Transaction transaction : transactionsList) {
			hashTransactions.add(transaction.getHashFile());
		}

		block.setMerkleRoot(MerkleTree.buildMerkleTree(hashTransactions));

		// Test chiamata per difficoltà
		Integer complexity = PoolDispatcherUtility.getCurrentComplexity();

		System.out.println("Complessità per minare: " + complexity);

		// Il miner inizia a minare
		miner.setMiningService(new MiningService(transactionsList, myLastBlock, miner.getMyPrivateKey(), miner.getMyPublickKey(), block, complexity, blockRepository,transRepo, new Runnable() {

			@Override
			public void run() {

				System.out.println("Miner interrotto");
				System.out.println("Sta minando: " + miner.isMining());
				// System.out.println("Numero di IP: " + IPManager.getManager().getIPList().size());
			}
		}));

		miner.startMine();

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
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().matches(IP_REGEX)) {
						ips.add(i.getHostAddress());
					}
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
