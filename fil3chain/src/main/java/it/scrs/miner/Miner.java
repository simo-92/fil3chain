package it.scrs.miner;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;
import it.scrs.miner.util.HttpUtil;
import it.scrs.miner.util.JsonUtility;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.persistence.Column;

import java.net.NetworkInterface;
import java.util.Enumeration;
import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.user.User;
// import java.util.logging.Logger;
import it.scrs.miner.models.Pairs;



/**
 * 
 *
 */

@Component
public class Miner {

	private String ipEntryPoint;
	private String portEntryPoint;
	private String entryPointBaseUri;
	private List<String> ipPeers; // contiene gli ip degli altri miner nella rete
	private User me;
	private static final Logger log = LoggerFactory.getLogger(Miner.class);
	private static final int nBlockUpdate = 10;// TODO metter nel properties
	private static final String prefixVPNet = "10.192.";// TODO mettere nel properties


	/**
	 * 
	 */
	public Miner() {
		super();
		// TODO PRendi dal database ME USER
	}

	/**
	 * 
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
	}

	/**
	 * @return
	 * @throws SocketException
	 */
	@SuppressWarnings("unchecked")
	public boolean firstConnectToEntryPoint() {

		String url = "http://" + this.getIpEntryPoint() + ":" + this.getPortEntryPoint() + this.getEntryPointBaseUri();
		String result = "";
		String myIp = "";
		List<String> myIpS = new ArrayList<String>();
		Enumeration<NetworkInterface> e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<InetAddress> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().startsWith("1"))
						;
					myIpS.add(i.getHostAddress());
					if (i.getHostAddress().startsWith(prefixVPNet))
						;
					myIp = i.getHostAddress();
				}
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
		}

		ipPeers = new ArrayList<String>();
		try {

			result = HttpUtil.doPost(url, new Pairs<String, String>("ip", myIp));
		} catch (Exception ex) {
			// Logger.getLogger(Miner.class.getName()).log(Level.SEVERE, null, ex);
		}
		Type type = new TypeToken<ArrayList<String>>() {
		}.getType();
		ipPeers = (List<String>) JsonUtility.fromJson(result, type);

		if (MinerApplication.testMiner == Boolean.TRUE) {
			if (ipPeers == null)
				ipPeers = new ArrayList<String>();
			ipPeers.add("10.192.0.7");// TODO IPVPN CICCIO RISERVA SERVER EP DOWN

		}
		ipPeers.removeAll(myIpS);
		ipPeers.forEach(ip -> System.out.println(ip));

		return true;
		// merdreeeeeee
	}

	/**
	 * @param merkleRoot
	 * @param minerPublicKey
	 * @param chainLevel
	 * @param transactions
	 * @param bf
	 * @param usr
	 * @return
	 */
//	public Block generateBlock( List<Transaction> transactions) {
//
//		int nonce = 0;
//		Block block;
//
//		// Tutti i miei parmatetri
//
//		// TODO
//
//		// Verifica le transazioni se valide (ovvero se SHA(file) già è presente in un blocco)
//
//		// get trans from Dispatcher
//
//		// richiesta a PoolDispatcher per saper la difficolta
//
//		// try {
//		// diff = Integer.getInteger(HttpUtil.doGet("http://localhost:8080/poolDispatcher"));
//		// } catch (Exception e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//		//
//		// getLastChainLevel l altezza massima
//		// bf = prendi l hash del blocco piu lungo
//		//
//
//		// encodeMerkleTree (Trans)
//
//		// minerPublic Key = me.pKey
//		// usr = me
//
//		do {
//			String merkleRoot;
//			Integer minerPublicKey;
//			Integer chainLevel;
//			Block bf;
//			User usr;
////			block = new Block(merkleRoot, minerPublicKey, nonce, chainLevel, transactions, bf, usr);
//			block.generateHashBlock();
//			nonce++;
//		} while (!block.verifyHash());
//		return block;
//	}

	/**
	 * @param merkleRoot
	 * @param minerPublicKey
	 * @param chainLevel
	 * @param transactions
	 * @param bf
	 * @param usr
	 * @return
	 */
	public Block verifyBlock(Block block) {

		String creationTime;// bLOCK.CREAIONtIME
		String merkleRoot = null;
		Integer minerPublicKey = null;
		Integer nonce = 0;
		Integer chainLevel = null;
		List<Transaction> trans = null;
		Block bf = null;
		User usr = null;
		// Tutti i miei parmatetri

		// TODO
		//

		// Calcola la differenza tra il mio chainLevel e quello del blocco

		// Verifica le transazioni BEL BLOCCO se valide (ovvero se SHA(file) già è presente in un blocco)

		// Merkletree del BLOCCO =encodeMerkleTree (Trans DEL BLOCCO)
		// minerPublic Key = BLOCCO.pKey
		// usr = Blocco.User

		// nounce = BLOCK.nOUNCE

		do {
			block = new Block(merkleRoot, minerPublicKey, nonce, chainLevel, trans, bf, usr);
			block.generateHashBlock();
			nonce++;
		} while (!block.verifyHash());
		return block;
	}

	/**
	 * @param blockRepository
	 * @param serviceMiner
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	public void updateFilechain(BlockRepository blockRepository, ServiceMiner serviceMiner) throws InterruptedException, ExecutionException, Exception {

		List<String> ipMiners = this.getIpPeers();
		// Rimuovo il mio IP
		// TODO Bisogna prendere l ip
		ipMiners.remove("192.168.0.143");
		Integer myChainLevel = 0;
		while (!ipMiners.isEmpty()) {
			// Lista contenente le richieste asincrone ai 3 ip
			List<Future<Pairs<String, Integer>>> minerResp = new ArrayList<Future<Pairs<String, Integer>>>();
			// Chiedi al db il valora del mio Max chainLevel
			myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel();

			// Finche non sono aggiornato(ovvero mi rispondono con stringa
			// codificata o blocco fittizio)
			// Prendo k ip random da tutta la lista di Ip che mi sono stati inviati
			askMinerChainLvl(ipMiners, minerResp, serviceMiner);

			// minerResp.add(serviceMiner.findMaxChainLevel("192.168.0.107"));
			System.out.println("1");

			// Oggetto che contiene la coppia IP,ChainLevel del Miner designato
			Pairs<String, Integer> designedMiner = new Pairs<String, Integer>();
			waitAndChooseMiner(minerResp, designedMiner);

			killRequest(minerResp);

			System.out.println("Il Miner designato = " + designedMiner.getValue1() + " ChainLevel = " + designedMiner.getValue2() + "\n");

			// Aggiorno la mia blockChain con i blocchi che mi arrivano in modo incrementale
			// TODO realizzare la variante di chidere N blocchi tutti insieme
			if (designedMiner.getValue1() != null)
				getBlocksFromMiner(ipMiners, myChainLevel, designedMiner, blockRepository);
			// aspetta una risposta
			// verifico i blocchi e aggiungo al db

			// mi connetto al primo che rispondi si e gli chiedo 10 blocchi o meno
			// chiusi dal blocco fittizio

			System.out.println(ipMiners.toString());
		}
	}

	/**
	 * @param minerResp
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void killRequest(List<Future<Pairs<String, Integer>>> minerResp) throws InterruptedException, ExecutionException {

		for (Future<Pairs<String, Integer>> f : minerResp) {
			System.out.println("\nElimino :" + f.get().getValue1());
			f.cancel(Boolean.TRUE);
		}
	}

	/**
	 * @param ipMiners
	 * @param minerResp
	 */
	private void askMinerChainLvl(List<String> ipMiners, List<Future<Pairs<String, Integer>>> minerResp, ServiceMiner serviceMiner) {

		for (int i = 0; i < ipMiners.size(); i++) {
			// Double x = Math.random() * ipMiners.size();
			Future<Pairs<String, Integer>> result = serviceMiner.findMaxChainLevel(ipMiners.get(i));
			if (result == null) {
				String tmp = ipMiners.remove(i);
				System.out.println("\n ho rimosso l ip " + tmp);
			} else {
				minerResp.add(result);
			}

		}
	}

	/**
	 * @param minerResp
	 * @param designedMiner
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void waitAndChooseMiner(List<Future<Pairs<String, Integer>>> minerResp, Pairs<String, Integer> designedMiner) throws InterruptedException, ExecutionException {

		Boolean flag = Boolean.TRUE;
		while (flag && !minerResp.isEmpty()) {
			System.out.print("1.5");
			System.out.println("size: " + minerResp.size());
			// Controlliamo se uno dei nostri messaggi di richiesta è tornato
			// indietro con successo
			Thread.sleep(250L);
			for (Future<Pairs<String, Integer>> f : minerResp) {
				// facciamo un For per ciclare tutte richieste attive
				// all'interno del nostro array e controlliamo se
				// sono arrivate le risposte

				if (f != null && f.isDone()) {
					// IP del miner designato da cui prendere la blockchain
					designedMiner.setValue1(f.get().getValue1());
					// ChainLevel del miner designato
					designedMiner.setValue2(f.get().getValue2());
					System.out.println("\nRisposto da: " + f.get().getValue1() + "chain level " + f.get().getValue2());
					flag = Boolean.FALSE;

				} else {
					// TODO rivedere questa cosa wait/////
					minerResp.remove(f);
				}

			}

		}
	}

	/**
	 * @param ipMiners
	 * @param myChainLevel
	 * @param designedMiner
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void getBlocksFromMiner(List<String> ipMiners, Integer myChainLevel, Pairs<String, Integer> designedMiner, BlockRepository blockRepository) throws Exception {

		Integer i = 0;
		Boolean nullResponse = Boolean.FALSE;
		while (!nullResponse && (i < nBlockUpdate) && (designedMiner.getValue2() > myChainLevel)) {
			// TODO cambire la uri di richiesta
			myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel() + 1;
			Type type = new TypeToken<List<Block>>() {
			}.getType();
			List<Block> blockResponse = (List<Block>) HttpUtil.doGetJSON("http://" + designedMiner.getValue1() + ":8080/fil3chain/getBlock?chainLevel=" + myChainLevel, type);

			if (blockResponse != null) {
				System.out.println("\nBlock response: " + blockResponse.toString());
				for (Block b : blockResponse) {
					// TODO qui dentro ora posso salvare nel mio DB tutti i blocchi appena ricevuti e verificarli
					blockRepository.save(b);
					System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");
				}
			} else {
				nullResponse = Boolean.TRUE;
			}
			i++;

		}
		System.out.println("2");

		if (!nullResponse && designedMiner.getValue2() <= blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel())
			ipMiners.remove(designedMiner.getValue1());
	}

	/**
	 * @param ipMiners
	 * @param myChainLevel
	 * @param designedMiner
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<Transaction> getTransFromDisp(Integer nTrans) throws Exception {
		
		
		
		// TODO List<Transaction> trans = HttpUtil.doGetJSON("http://" + getEntryPointBaseUri() +
		
		Type type = new TypeToken<List<Transaction>>() {
		}.getType();
		List<Transaction> trans = (List<Transaction>)HttpUtil.doGetJSON("http://" + "10.198.0.7" + ":8080/JsonTransaction?nTrans=" + nTrans, type);

		// Integer i = 0;
		// Boolean nullResponse = Boolean.FALSE;
		// while (!nullResponse && (i < nBlockUpdate) && (designedMiner.getValue2() > myChainLevel)) {
		// // TODO cambire la uri di richiesta
		// myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel() + 1;
		//
		// List<Block> blockResponse = HttpUtil.doGetJSON("http://" + designedMiner.getValue1() +
		// ":8080/fil3chain/getBlock?chainLevel=" + myChainLevel);
		//
		// if (blockResponse != null) {
		// System.out.println("\nBlock response: " + blockResponse.toString());
		// for (Block b : blockResponse) {
		// // TODO qui dentro ora posso salvare nel mio DB tutti i blocchi appena ricevuti e verificarli
		// blockRepository.save(b);
		// System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");
		// }
		// } else {
		// nullResponse = Boolean.TRUE;
		// }
		// i++;
		//
		// }
		// System.out.println("2");
		//
		// if (!nullResponse && designedMiner.getValue2() <=
		// blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel())
		// ipMiners.remove(designedMiner.getValue1());
		return trans;
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
	 * @return the ipPeers
	 */
	public List<String> getIpPeers() {

		return ipPeers;
	}

	/**
	 * @param ipPeers
	 *            the ipPeers to set
	 */
	public void setIpPeers(List<String> ipPeers) {

		this.ipPeers = ipPeers;
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {

		return log;
	}

}
