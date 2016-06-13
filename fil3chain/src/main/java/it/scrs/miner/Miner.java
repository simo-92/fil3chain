package it.scrs.miner;


import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.block.MerkleTree;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.user.User;
import it.scrs.miner.interfaces.MinerEventsListener;
import it.scrs.miner.models.BlockChain;
import it.scrs.miner.models.Pairs;
import it.scrs.miner.util.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



// import java.util.logging.Logger;
/**
 *
 *
 */
@Component
public class Miner implements MinerEventsListener {

	private String ipEntryPoint;
	private String portEntryPoint;
	private String entryPointBaseUri;
	private String poolDispatcherBaseUri;
	private String actionConnect;

	private String actionDisconnect;
	private String actionKeepAlive;

	// private List<String> ipPeers; // contiene gli ip degli altri miner nella rete
	private String ip;
	private User me;

	private MiningService miningService;

	private static final Logger log = LoggerFactory.getLogger(Miner.class);

	private static final String prefixVPNet = "10.192.";// TODO mettere nel properties

	private String myPublickKey;
	private String myPrivateKey;
	private BlockRepository blockRepository;
	private ServiceMiner serviceMiner;
	private BlockChain blockChain;


	/**
	 *
	 */
	public Miner() {
		super();
		// TODO PRendi dal database ME USER
	}

	public Miner(String ip, BlockRepository blockRepository, ServiceMiner serviceMiner) {
		super();
		loadNetworkConfig();
		loadKeyConfig(); // carica le chiavi dal file properties
		setIp(ip);
		setBlockRepository(blockRepository);
		setServiceMiner(serviceMiner);
		firstConnectToEntryPoint();
		
		blockChain = new BlockChain(this);
		initializeBlockChain();
		blockChain.updateFilechain();
		// TODO PRendi dal database ME USER
	}

	/**
	 * @return the blockRepository
	 */
	public BlockRepository getBlockRepository() {

		return blockRepository;
	}

	/**
	 * @return the serviceMiner
	 */
	public ServiceMiner getServiceMiner() {

		return serviceMiner;
	}

	/**
	 *
	 */
	public void loadKeyConfig() {

		Properties prop = new Properties();
		InputStream in = Miner.class.getResourceAsStream("/keys.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.myPublickKey = prop.getProperty("public");
		this.myPrivateKey = prop.getProperty("private");
	}

	/*
	 * public void loadMinerConfiguration() { // Carica la configurazione Properties prop = new Properties();
	 * InputStream in = Miner.class.getResourceAsStream("/miner.properties"); try { prop.load(in); // Imposta il timeout
	 * blockChain.setnBlockUpdate(Integer.parseInt(prop.getProperty("nBlockUpdate", "10"))); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 */

	public void loadNetworkConfig() {

		Properties prop = new Properties();
		InputStream in = Miner.class.getResourceAsStream("/network.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setIpEntryPoint(prop.getProperty("ipEntryPoint"));
		this.setPortEntryPoint(prop.getProperty("portEntryPoint"));
		this.setEntryPointBaseUri(prop.getProperty("entryPointBaseUri"));
		this.setPoolDispatcherBaseUri(prop.getProperty("poolDispatcherBaseUri"));
		this.setActionConnect(prop.getProperty("actionConnect"));
		this.setActionDisconnect(prop.getProperty("actionDisconnect"));
		this.setActionKeepAlive(prop.getProperty("actionKeepAlive"));

	}

	/**
	 * @return @throws SocketException
	 */
	@SuppressWarnings("unchecked")
	public boolean firstConnectToEntryPoint() {

		String url = "http://" + this.getIpEntryPoint() + ":" + this.getPortEntryPoint() + this.getEntryPointBaseUri() + this.getActionConnect();
		String result = "";

		try {
			System.out.println("URL: " + url);
			System.out.println("Il mio IP: " + ip);
			result = HttpUtil.doPost(url, "{\"user_ip\":\"" + this.getIp() + ":8080\"}");
			// System.out.println(result);
		} catch (Exception ex) {
			System.err.println("Errore durante la richiesta di IP\n" + ex);
			// Logger.getLogger(Miner.class.getName()).log(Level.SEVERE, null, ex);
		}

		Type type = new TypeToken<ArrayList<String>>() {
		}.getType();
		List<String> ips = JsonUtility.fromJson(result, type);
		ArrayList<IP> iplist = new ArrayList<>();
		for (String ip : ips) {
			iplist.add(new IP(ip));
		}

		IPManager.getManager().setAllIp((List<IP>) iplist.clone());

		System.out.println("Numero di IP ottenuti: " + IPManager.getManager().getIPList().size());
		IPManager.getManager().getIPList().forEach(ip -> System.out.println(ip));

		return true;
	}

	public void setActionConnect(String actionConnect) {

		this.actionConnect = actionConnect;
	}

	public void setActionDisconnect(String actionDisconnect) {

		this.actionDisconnect = actionDisconnect;
	}

	public void setActionKeepAlive(String actionKeepAlive) {

		this.actionKeepAlive = actionKeepAlive;
	}

	public MiningService getMiningService() {

		return miningService;
	}

	public void setMiningService(MiningService miningService) {

		this.miningService = miningService;
	}

	/**
	 * @param b
	 *            blocco che cerco
	 * @param blockRepository
	 * @param serviceMiner
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public Boolean verifyBlock(Block b, BlockRepository blockRepository, ServiceMiner serviceMiner) throws InterruptedException, ExecutionException, IOException {

		// nell primo updateh

		if (blockRepository.findByhashBlock(b.getFatherBlockContainer().getHashBlock()) == null)
			blockChain.updateBranChain(b.getFatherBlockContainer().getHashBlock());

		return singleBlockVerify(blockRepository, b);
		
		// devo chiedere a tutti i miner se hanno questo blocco

		// lista vuota scarto Cristo
		// lista piena
		// Controllar DB Se ho blocco se si verifica e convalida!
		// Altrimenti devi chiedere ricorsivamente per il padre del current

		// Tutti i miei parmatetri
		// se non ho il blocco padre mi aggiorno da tutti (e mi tornerà anche questo)
//		if (!blockRepository.findBychainLevel(b.getChainLevel() - 1).contains(b)) {
//			// Eseguo l'update della catena
//			return blockChain.updateFilechain();
//		} else {
//			// Altrimenti verifico il blocco
//			return singleBlockVerify(blockRepository, b);
//		}

	}

	// Metodo di verifica di un singolo blocco
	/**
	 * @param blockRepository
	 * @param blockToVerify
	 * @return boolean true if is ok
	 */
	private Boolean singleBlockVerify(BlockRepository blockRepository, Block blockToVerify) {

		// Ordine di verifica migliore: Firma, PoW, Markle root, Double Trans
		// TODO
		// COntrolla se il apdre è ad un livello meno 1 del mio chain level

		// Verifica firma
		if (!verifySignature(blockToVerify))
			return Boolean.FALSE;

		// Verifica Proof of Work
		if (!verifyProofOfWork(blockToVerify))
			return Boolean.FALSE;

		// Verifica MerkleRoot
		if (!verifyMerkleRoot(blockToVerify))
			return Boolean.FALSE;

		// Verifica transazioni uniche
		if (!verifyUniqueTransactions(blockToVerify, blockRepository))
			return Boolean.FALSE;

		// Se ha passato tutti i controlli allora ritorna TRUE
		return Boolean.TRUE;
	}

	// Metodo di verifica della proof of work
	private Boolean verifyProofOfWork(Block block) {

		Integer complexity = PoolDispatcherUtility.getBlockComplexity(block.getCreationTime());

		// Se c'è stato un errore o la complessità non è stata trovata nel server
		// allora termina con FALSE
		if (complexity == -1)
			return Boolean.FALSE;

		// Calcolo full mask
		int fullMask = complexity / 8;

		// Calcolo rest mask
		int restanti = complexity % 8;

		byte restMask;

		if (restanti == 0) {
			restMask = 0b000000;
		} else {
			restMask = (byte) 0b11111111;
			restMask = (byte) (restMask << (8 - restanti));
		}

		byte[] hash = org.apache.commons.codec.digest.DigestUtils.sha256(block.toString() + block.getNonce());

		// Verifica dei primi fullMask byte interi
		for (int i = 0; i < fullMask; i++) {
			if (hash[i] != 0) {
				return false;
			}
		}

		// Se non ci sono bit restanti allora restituisce true
		if (restMask == 0)
			return true;

		// Altrimenti controlla i bit rimanenti
		return (hash[fullMask] & restMask) == 0;
	}

	// Metodo di verifica della firma di un blocco
	// Abbiamo stabilito di firmare solo l'hash del blocco essendo già esso fatto su tutti gli altri campi
	private Boolean verifySignature(Block block) {

		try {
			if (!CryptoUtil.verifySignature(block.getHashBlock(), block.getSignature(), block.getMinerPublicKey())) {
				return Boolean.FALSE;
			}
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	// Metodo di verifica del merkle root di un blocco
	private Boolean verifyMerkleRoot(Block block) {

		ArrayList<String> transactionsHash = new ArrayList<>();
		for (Transaction transaction : block.getTransactionsContainer()) {
			transactionsHash.add(transaction.getHashFile());
		}

		String checkMerkle = MerkleTree.buildMerkleTree(transactionsHash);

		if (!checkMerkle.equals(block.getMerkleRoot())) {
			System.err.println("MerkleRoot diverso.");
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	// Metodo di verifica della transazione unica
	// Tutti i predecessori del blocco arrivato NON devono avere la transazione
	private Boolean verifyUniqueTransactions(Block block, BlockRepository blockRepository) {

		List<Block> predecessori = new ArrayList<Block>();
		Block currentBlock = block;
		while (currentBlock.getFatherBlockContainer() != null) {
			predecessori.add(blockRepository.findByhashBlock(currentBlock.getFatherBlockContainer().getHashBlock()));
			currentBlock = currentBlock.getFatherBlockContainer();
		}
		for (Block p : predecessori) {
			for (Transaction t : block.getTransactionsContainer()) {
				if (p.getTransactionsContainer().contains(t)) {
					System.err.println("La transazione è presente in uno dei predecessori.");
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * @return the ipEntryPoint
	 */
	public String getIpEntryPoint() {

		return ipEntryPoint;
	}

	/**
	 * @param ipEntryPoint
	 *            the ipEntryPoint to set
	 */
	public void setIpEntryPoint(String ipEntryPoint) {

		this.ipEntryPoint = ipEntryPoint;
	}

	/**
	 * @return the portEntryPoint
	 */
	public String getPortEntryPoint() {

		return portEntryPoint;
	}

	/**
	 * @param portEntryPoint
	 *            the portEntryPoint to set
	 */
	public void setPortEntryPoint(String portEntryPoint) {

		this.portEntryPoint = portEntryPoint;
	}

	/**
	 * @return the entryPointBaseUri
	 */
	public String getEntryPointBaseUri() {

		return entryPointBaseUri;
	}

	/**
	 * @param entryPointBaseUri
	 *            the entryPointBaseUri to set
	 */
	public void setEntryPointBaseUri(String entryPointBaseUri) {

		this.entryPointBaseUri = entryPointBaseUri;
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {

		return log;
	}

	public void startMine() {

		if (miningService == null && miningService.isInitialized()) {
			return;
		}

		miningService.start();
	}

	public void stopMine() {

		if (miningService == null && miningService.isInitialized()) {
			return;
		}

		miningService.interrupt();
	}

	public Boolean isMining() {

		if (miningService == null && miningService.isInitialized()) {
			return Boolean.FALSE;
		}

		return !miningService.isInterrupted();
	}

	public void initializeBlockChain() {

		// Se non ho nessun blocco ne aggiungo uno fittizio
		if (!blockRepository.findAll().iterator().hasNext()) {
			Block block = getFirstBlock();
			blockRepository.save(block);
		}
	}

	private Block getFirstBlock() {

		Block block = new Block();
		block.setHashBlock("0");
		block.setChainLevel(0);
		block.setCreationTime(new Date(0).getTime() + "");
		block.setMerkleRoot("0");
		block.setNonce(0);
		block.setSignature("0");
		block.setMinerPublicKey("0");
		return block;
	}

	public void setIp(String ip) {

		this.ip = ip;
	}

	public String getIp() {

		return ip;
	}

	public String getActionConnect() {

		return actionConnect;
	}

	public String getActionDisconnect() {

		return actionDisconnect;
	}

	public String getActionKeepAlive() {

		return actionKeepAlive;
	}

	public String getMyPublickKey() {

		return myPublickKey;
	}

	public void setMyPublickKey(String myPublickKey) {

		this.myPublickKey = myPublickKey;
	}

	public String getMyPrivateKey() {

		return myPrivateKey;
	}

	public void setMyPrivateKey(String myPrivateKey) {

		this.myPrivateKey = myPrivateKey;
	}

	public void setPoolDispatcherBaseUri(String poolDispatcherBaseUri) {

		this.poolDispatcherBaseUri = poolDispatcherBaseUri;
	}

	@Override
	public void onNewBlockArrived(Block block) {

		System.out.println("Nuovo blocco arrivato, verifico...");

		Boolean isVerified = Boolean.FALSE;

		try {
			isVerified = verifyBlock(block, blockRepository, serviceMiner);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Blocco valido? " + isVerified);

		if (isVerified) {
			// Stoppo il processo di mining
			stopMine();
			// Salvo il blocco nella catena
			blockRepository.save(block);
			// Aggiorno il servizio di mining
			updateMiningService();
			// Ricomincio a minare
			miningService.run();
		}
	}

	private void updateMiningService() {

		// Prendo l'ultmo blocco della catena
		Block lastBlock = blockRepository.findFirstByOrderByChainLevelDesc();
		// Inizializzo il nuovo blocco da minare
		Block newBlock = new Block();
		newBlock.setFatherBlockContainer(lastBlock);
		newBlock.setChainLevel(lastBlock.getChainLevel() + 1);
		newBlock.setUserContainer(new User("", "Ciano", "Bug", "Miner", "Mail", "Cianone"));

		// Prendo le transazioni dal Pool Dispatcher
		List<Transaction> transactionsList = PoolDispatcherUtility.getTransactions();

		ArrayList<String> hashTransactions = new ArrayList<>();
		for (Transaction transaction : transactionsList) {
			hashTransactions.add(transaction.getHashFile());
		}

		newBlock.setMerkleRoot(MerkleTree.buildMerkleTree(hashTransactions));

		// Test chiamata per difficoltà
		Integer complexity = PoolDispatcherUtility.getCurrentComplexity();

		miningService.updateService(newBlock, lastBlock, complexity, transactionsList);
	}

	public void setBlockRepository(BlockRepository blockRepository) {

		this.blockRepository = blockRepository;
	}

	public void setServiceMiner(ServiceMiner serviceMiner) {

		this.serviceMiner = serviceMiner;
	}
}
