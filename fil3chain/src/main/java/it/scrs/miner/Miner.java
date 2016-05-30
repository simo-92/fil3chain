package it.scrs.miner;


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
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;
import it.scrs.miner.util.HttpUtil;
import it.scrs.miner.util.JsonUtility;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import it.scrs.miner.dao.block.Block;
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
	private static final Logger log = LoggerFactory.getLogger(Miner.class);


	/**
	 * 
	 */
	public Miner() {
		super();
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
	 */
	public boolean firstConnectToEntryPoint() {

		String url = "http://" + this.getIpEntryPoint() + ":" + this.getPortEntryPoint() + this.getEntryPointBaseUri();
		String result = "";
		String localIp = "";

		ipPeers = new ArrayList<String>();
		try {
			localIp = InetAddress.getLocalHost().getHostAddress();
			result = HttpUtil.doPost(url, new Pairs<String, String>("ip", localIp));
		} catch (Exception ex) {
			// Logger.getLogger(Miner.class.getName()).log(Level.SEVERE, null, ex);
		}
		Type type;
		type = new TypeToken<ArrayList<String>>() {
		}.getType();
		ipPeers = (List<String>) JsonUtility.fromJson(result, type);
		
		if(MinerApplication.testMiner==Boolean.TRUE){
			if(ipPeers == null)
				ipPeers.add("192.168.0.108");
			
		}
		
		ipPeers.forEach(ip -> System.out.println(ip));
		return true;
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
	public Block generateBlock(String merkleRoot, Integer minerPublicKey, Integer chainLevel, List<Transaction> transactions, Block bf, User usr) {
		int nonce = 0;
		Block block;
		do {
			block = new Block(merkleRoot, minerPublicKey, nonce, chainLevel, transactions, bf, usr);
			block.generateHashBlock();
			nonce++;
		} while (!block.verify());
		return block;
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
