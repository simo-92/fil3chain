package it.scrs.miner;


import java.net.InetAddress;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;

// import java.util.logging.Logger;




@SpringBootApplication
@ComponentScan("it.scrs.miner")
@EnableAutoConfiguration
public class MinerApplication implements CommandLineRunner {

	public static final Boolean testMiner = Boolean.TRUE;
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(MinerApplication.class);

	@Autowired
	private BlockRepository blockRepository;

	@Autowired
	private ServiceMiner serviceMiner;


	
	@Override
	public void run(String... args) throws Exception {

		Miner miner = new Miner();
		miner.loadNetworkConfig();
		miner.firstConnectToEntryPoint();
		System.out.println("\n il mio ip : "+ InetAddress.getLocalHost().getHostAddress());
	

		
		
		// Mino per prova
		Block block = miner.generateBlock(null, 17, 31, null, null, null);
		System.out.println(block.generateAndGetHashBlock());

		// prendi minerReq ip a caso dalla lista dei miner
		miner.updateFilechain(blockRepository, serviceMiner);
		System.out.println("3");
		//TODO Avviare MINING
	}
	
	

	public static void main(String[] args) {

		SpringApplication.run(MinerApplication.class);

	}


	
	/**
	 * @return the blockRepository
	 */
	public BlockRepository getBlockRepository() {
	
		return blockRepository;
	}



	
	/**
	 * @param blockRepository the blockRepository to set
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
	 * @param serviceMiner the serviceMiner to set
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
