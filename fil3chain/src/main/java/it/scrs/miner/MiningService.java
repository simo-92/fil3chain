package it.scrs.miner;


import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.block.MerkleTree;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.transaction.TransactionRepository;
import it.scrs.miner.dao.user.User;
import it.scrs.miner.models.Pairs;
import it.scrs.miner.util.AudioUtil;
import it.scrs.miner.util.CryptoUtil;
import it.scrs.miner.util.IP;
import it.scrs.miner.util.PoolDispatcherUtility;

import org.h2.command.dml.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mysql.cj.api.x.Collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Created by Marco Date: 08/06/2016
 */
@Service
public class MiningService extends Thread implements Runnable {

	// Blocco da minare
	private Block block;

	// Chiave privata del creatore del blocco
	private String privateKey;

	// Difficoltà in cui si sta minando
	private Integer difficulty;

	// Maschera per il check dell'hash nei byte interi
	private Integer fullMask;

	// Maschera per il check dell'hash nel byte di "resto"
	private byte restMask;

	// Callback chiamata dopo l'interruzione del thread
	private Runnable interruptCallback;

	// Chiave pubblica dell'autore del blocco
	private String publicKey;

	// Blocco precedente nella catena
	private Block previousBlock;

	// Lista di transazioni presente nel blocco
	private List<Transaction> transactions;

	// Block repository
	private BlockRepository blockRepository;
	private TransactionRepository transRepo;

	
	@Autowired
	 RestTemplate restTemplate ;

	/**
	 * Costruttore di default (necessario)
	 */
	public MiningService() {
		block = null;
		difficulty = -1;
		fullMask = 0;
		restMask = 0b1111111;
		interruptCallback = null;
	}

	/**
	 * Costruttore
	 *
	 * @param block
	 * @param difficulty
	 */
	public MiningService(List<Transaction> transactions, Block previousBlock, String prKey, String puKey, Block block, Integer difficulty, BlockRepository blockRepository, TransactionRepository transRepo, Runnable interruptCallback) {
		this.block = block;
		this.privateKey = prKey;
		this.publicKey = puKey;
		this.previousBlock = previousBlock;
		this.transactions = transactions;
		this.difficulty = difficulty;
		this.interruptCallback = interruptCallback;
		this.blockRepository = blockRepository;
		this.transRepo = transRepo;
	}

	/**
	 * Metodo per calcolare le maschere per effettuare il check dell'hash
	 */
	private void calculateMasks() {

		// Calcolo full mask
		fullMask = difficulty / 8;

		// Calcolo rest mask
		int restanti = difficulty % 8;

		if (restanti == 0) {
			restMask = 0b000000;
		} else {
			restMask = (byte) 0b11111111;
			restMask = (byte) (restMask << (8 - restanti));
		}
	}

	@Override
	public void run() {

		try {
			mine();
		} catch (Exception ex) {
			Logger.getLogger(MiningService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Metodo per minare un blocco
	 */
	public void mine() throws Exception {

		if (difficulty == -1) {
			System.err.println("Complessità per il calcolo del blocco errata, impossibile minare");
			return;
		}

		if (block == null)
			initializeService();

		// Calcolo le maschere per il check dell'hash.
		calculateMasks();

		// Tempo di inizio mining
		long startTime = new Date().getTime();

		// Nonce
		Integer nonce = new Random().nextInt();
		Integer nonceStart = nonce;
		Integer nonceFinish = 0;
		float totalTime = 0;

		System.out.println("Nonce di partenza: " + nonce);

		// Hash del blocco
		byte[] hash;

		do {
			// Genera nuovo hash
			hash = org.apache.commons.codec.digest.DigestUtils.sha256(block.toString() + nonce);

			// Incremento il nonce
			nonce++;
		} while (!verifyHash(hash));
		AudioUtil.alert(); // avviso sonoro
		nonceFinish = nonce - 1;
		totalTime = (new Date().getTime() - startTime) / 1000.0f;

		// Calcolo hash corretto in esadecimale
		// Spiegazione nonce - 1: Viene fatto -1 perché nell'ultima iterazione
		// viene incrementato anche se l'hash era corretto.
		String hexHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(block.toString() + (nonce - 1));

		// Impostazione dell'hash e del nonce
		block.setHashBlock(hexHash);
		block.setNonce(nonce - 1);
		block.setSignature(CryptoUtil.sign(hexHash, privateKey));
		block.setMinerPublicKey(publicKey);
		block.setFatherBlockContainer(previousBlock.getHashBlock());
		block.setTransactionsContainer(transactions);

		block.setCreationTime(Long.toString(System.currentTimeMillis()));
		System.out.println("Hash trovato: " + block.getHashBlock() + " con difficoltà: " + difficulty + " Nonce: " + nonce + " Tempo impiegato: " + totalTime + " secondi");
		System.out.println("Hash provati: " + (Math.abs(nonceFinish - nonceStart)) + " HashRate: " + (((Math.abs(nonceFinish - nonceStart)) / totalTime) / 1000000.0f) + " MH/s");
		// Chiude il thread
		// interrupt();
		// Salvo il blocco
		if (blockRepository != null)
			blockRepository.save(block);
		// per ogni transazione mette il riferimento al blocco container
		int indexInBlock = 0;
		for (Transaction trans : transactions) {
			trans.setBlockContainer(block.getHashBlock());
			trans.setIndexInBlock(indexInBlock);
			System.out.println(trans.getIndexInBlock());
			transRepo.save(trans);
			indexInBlock++;
		}

		sendBlockToMiners();

		// TODO: Ricomincia a minare
		initializeService();
		mine();
	}

	@Async
	public Future<List<Block>> sendBlockToMiners() throws InterruptedException {

		

//		SimpleClientHttpRequestFactory rf = ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory());
//		rf.setReadTimeout(1000 * 5);
//		rf.setConnectTimeout(1000 * 5);

		List<Block> blocks = new ArrayList<Block>();
		String bool = Boolean.FALSE.toString();
		Map<IP, Integer> map = new HashMap<IP, Integer>();
		Map<IP, Integer> counter = Collections.synchronizedMap(map);
		Miner.getInstance().firstConnectToEntryPoint();
		synchronized (counter) {
			for (IP ip : IPManager.getManager().getIPList()) {
				counter.put(ip, 0);
			}

			System.out.println("dimensione lista hashmap " + counter.size());

		}

		while (counter.size() > 0) {

			for (IP ip : IPManager.getManager().getIPList()) {
				System.out.println("Invio blocco a: " + ip.getIp());
				try {
					// String response = HttpUtil.doPost("http://" + ip.getIp() + "/fil3chain/newBlock",
					// JsonUtility.toJson(block));
					String response = restTemplate.postForObject("http://" + ip.getIp() + "/fil3chain/newBlock", block, String.class);
					System.out.println("Ho inviato il blocco e mi è ritornato come risposta: " + response);
					synchronized (counter) {

						// Se ho mandato il blocco rimuovo il miner
						counter.remove(ip);

					}
				} catch (Exception e) {
					e.printStackTrace();
					sleep(1000);
					System.out.println("Il miner " + ip.getIp() + " non è più connesso.");
					System.out.println("Errore invio blocco: " + bool);
				} finally {
					synchronized (counter) {
						// altrimenti aumenta il counter di uno
					
						counter.put(ip, counter.get(ip) + 1);
						if(counter.get(ip)> ServiceMiner.nReqProp)
							counter.remove(ip);

					}
				}
			}

		}

		// Annullo il blocco appena minato
		block = null;

		return new AsyncResult<>(blocks);
	}

	@Override
	public void interrupt() {

		super.interrupt();
		if (interruptCallback != null)
			interruptCallback.run();
	}

	private Boolean verifyHash(byte[] hash) {

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

	public Block getBlock() {

		return block;
	}

	public void setBlock(Block block) {

		this.block = block;
	}

	public String getPrivateKey() {

		return privateKey;
	}

	public void setPrivateKey(String privateKey) {

		this.privateKey = privateKey;
	}

	public Integer getDifficulty() {

		return difficulty;
	}

	public void setDifficulty(Integer difficulty) {

		this.difficulty = difficulty;
	}

	public Boolean isInitialized() {

		return (block != null && difficulty != -1);
	}

	public void updateService(Block miningBlock, Block previousBlock, int difficulty, List<Transaction> transactionList) {

		System.out.println("Update service");
		interrupt();
		this.block = miningBlock;
		this.previousBlock = previousBlock;
		this.difficulty = difficulty;
		this.transactions = transactionList;
	}

	public void initializeService() {

		System.out.println("Inizializza servizio");
		// Prendo l'ultmo blocco della catena
		Block lastBlock = blockRepository.findFirstByOrderByChainLevelDesc();

		// Inizializzo il nuovo blocco da minare
		block = new Block();
		block.setFatherBlockContainer(lastBlock.getHashBlock());
		block.setChainLevel(lastBlock.getChainLevel() + 1);
		block.setMinerPublicKey(publicKey);
		block.setUserContainer(new User("", "Ciano", "Bug", "Miner", "Mail", "Cianone"));

		// Prendo le transazioni dal Pool Dispatcher
		List<Transaction> transactionsList = PoolDispatcherUtility.getTransactions();

		ArrayList<String> hashTransactions = new ArrayList<>();
		for (Transaction transaction : transactionsList) {
			hashTransactions.add(transaction.getHashFile());
		}
		block.setMerkleRoot(MerkleTree.buildMerkleTree(hashTransactions));

		// Test chiamata per difficoltà
		Integer complexity = PoolDispatcherUtility.getCurrentComplexity();

		previousBlock = lastBlock;
		difficulty = complexity;
		transactions = transactionsList;
	}

}
