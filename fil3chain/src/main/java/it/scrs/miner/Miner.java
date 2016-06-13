package it.scrs.miner;

import com.google.gson.reflect.TypeToken;
import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.block.MerkleTree;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.user.User;
import it.scrs.miner.interfaces.MinerEventsListener;
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

    //private List<String> ipPeers; // contiene gli ip degli altri miner nella rete
    private IPManager ipManager;
    private String ip;
    private User me;

    private MiningService miningService;

    private static final Logger log = LoggerFactory.getLogger(Miner.class);
    private static int nBlockUpdate = 10;// TODO metter nel properties
    private static final String prefixVPNet = "10.192.";// TODO mettere nel properties
    private String myPublickKey;
    private String myPrivateKey;
    private BlockRepository blockRepository;
    private ServiceMiner serviceMiner;

    /**
     *
     */
    public Miner() {
        super();
        ipManager = IPManager.getManager();
        // TODO PRendi dal database ME USER
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

    public void loadMinerConfiguration() {
        // Carica la configurazione
        Properties prop = new Properties();
        InputStream in = Miner.class.getResourceAsStream("/miner.properties");
        try {
            prop.load(in);
            // Imposta il timeout
            this.nBlockUpdate = Integer.parseInt(prop.getProperty("nBlockUpdate", "10"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        /*
        String myIp = "";
		List<String> myIpS = new ArrayList<>();
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
			// e1.printStackTrace();
		}
         */

        //ipPeers = new ArrayList<>();
        try {
            System.out.println("URL: " + url);
            System.out.println("Il mio IP: " + ip);
            result = HttpUtil.doPost(url, "{\"user_ip\":\"" + this.getIp() + ":8080\"}");
            //System.out.println(result);
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
        //iplist.forEach(ip -> System.out.println("ip: "+ip));
        ipManager.setAllIp((List<IP>) iplist.clone());

//		if (MinerApplication.testMiner == Boolean.TRUE) {
//            // if (ipPeers == null || (ipPeers.size() == 0)) {
//			if (ipPeers == null) {
//				ipPeers = new ArrayList<>();
//            } else {
//                // IPVPN CICCIO RISERVA SERVER EP DOWN
//			    ipPeers.add("10.192.0.7");
//            }
//		}
        // ipPeers.removeAll(myIpS);
        System.out.println("Numero di IP ottenuti: " + ipManager.getIPList().size());
        ipManager.getIPList().forEach(ip -> System.out.println(ip));

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

    public Boolean verifyBlock(Block b, BlockRepository blockRepository, ServiceMiner serviceMiner) throws InterruptedException, ExecutionException, IOException {
        // Tutti i miei parmatetri
        // se non ho il blocco padre mi aggiorno da tutti (e mi tornerà anche questo)
        if (!blockRepository.findBychainLevel(b.getChainLevel() - 1).contains(b)) {
            // Stoppo il processo di mining
            stopMine();
            // Eseguo l'update della catena
            return updateFilechain(blockRepository, serviceMiner);
        } else {
            // Altrimenti verifico il blocco
            return singleBlockVerify(blockRepository, b);
        }

    }

    // Metodo di verifica di un singolo blocco
    private Boolean singleBlockVerify(BlockRepository blockRepository, Block blockToVerify) {

        // Ordine di verifica migliore: Firma, PoW, Markle root, Double Trans

        // Verifica firma
        if (!verifySignature(blockToVerify)) return Boolean.FALSE;

        // Verifica Proof of Work
        if (!verifyProofOfWork(blockToVerify)) return Boolean.FALSE;

        // Verifica MerkleRoot
        if (!verifyMerkleRoot(blockToVerify)) return Boolean.FALSE;

        // Verifica transazioni uniche
        if (!verifyUniqueTransactions(blockToVerify, blockRepository)) return Boolean.FALSE;

        // Se ha passato tutti i controlli allora ritorna TRUE
        return Boolean.TRUE;
    }

    // Metodo di verifica della proof of work
    private Boolean verifyProofOfWork(Block block) {
        Integer complexity = -1;
        try {
            JSONObject result  = new JSONObject(HttpUtil.doPost("http://" + ipEntryPoint + ":" + portEntryPoint + poolDispatcherBaseUri +  "/get_complexity", "{\"date\" : \"" + block.getCreationTime() + "\"}"));
            complexity = (Integer) result.get("complexity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Se c'è stato un errore o la complessità non è stata trovata nel server
        // allora termina con FALSE
        if (complexity == -1) return Boolean.FALSE;

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
        for(int i = 0; i < fullMask; i++) {
            if(hash[i] != 0) {
                return false;
            }
        }

        // Se non ci sono bit restanti allora restituisce true
        if (restMask == 0) return true;

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
        if (block.getFatherBlockContainer() != null) {
            List<Block> predecessori = blockRepository.findByhashBlock(block.getFatherBlockContainer().getHashBlock());

            for (Block p : predecessori) {
                for (Transaction t : block.getTransactionsContainer()) {
                    if (p.getTransactionsContainer().contains(t)) {
                        System.err.println("La transazione è presente in uno dei predecessori.");
                        return Boolean.FALSE;
                    }
                }
            }
        }

        return Boolean.FALSE;
    }

    /**
     * @param blockRepository
     * @param serviceMiner
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws Exception
     */
    public Boolean updateFilechain(BlockRepository blockRepository, ServiceMiner serviceMiner) throws InterruptedException, ExecutionException, IOException {

        List<IP> ipMiners = (List<IP>) ((ArrayList<IP>) ipManager.getIPList()).clone();

        // Rimuovo il mio IP
        ipMiners.remove(ip);

        Integer myChainLevel = 0;
        while (!ipMiners.isEmpty()) {
            // Lista contenente le richieste asincrone ai 3 ip
            List<Future<Pairs<IP, Integer>>> minerResp = new ArrayList<>();
            // Chiedi al db il valora del mio Max chainLevel
            myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel();

            // Finche non sono aggiornato(ovvero mi rispondono con stringa
            // codificata o blocco fittizio)
            // Prendo k ip random da tutta la lista di Ip che mi sono stati inviati
            askMinerChainLvl(ipMiners, minerResp, serviceMiner);

            // minerResp.add(serviceMiner.findMaxChainLevel("192.168.0.107"));
            System.out.println("1");

            // Oggetto che contiene la coppia IP,ChainLevel del Miner designato
            Pairs<IP, Integer> designedMiner = new Pairs<IP, Integer>();
            waitAndChooseMiner(minerResp, designedMiner);

            killRequest(minerResp);

            System.out.println("Il Miner designato = " + designedMiner.getValue1() + " ChainLevel = " + designedMiner.getValue2() + "\n");

            // Aggiorno la mia blockChain con i blocchi che mi arrivano in modo incrementale
            if (designedMiner.getValue1() != null) {
                getBlocksFromMiner(ipMiners, myChainLevel, designedMiner, blockRepository);
            }
            // aspetta una risposta
            // verifico i blocchi e aggiungo al db

            // mi connetto al primo che rispondi si e gli chiedo 10 blocchi o meno
            // chiusi dal blocco fittizio
            System.out.println(ipMiners.toString());
        }
        return Boolean.TRUE;
    }

    /**
     * @param minerResp
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void killRequest(List<Future<Pairs<IP, Integer>>> minerResp) throws InterruptedException, ExecutionException {

        for (Future<Pairs<IP, Integer>> f : minerResp) {
            System.out.println("\nElimino :" + f.get().getValue1().getIp());
            f.cancel(Boolean.TRUE);
        }
    }

    /**
     * Restituisce la lista di miner che hanno risposto con il loro livello di
     * block chain.
     *
     * @param ipMiners
     * @param minerResp
     */
    private void askMinerChainLvl(List<IP> ipMiners, List<Future<Pairs<IP, Integer>>> minerResp, ServiceMiner serviceMiner) {

        for (int i = 0; i < ipMiners.size(); i++) {
            // Double x = Math.random() * ipMiners.size();
            Future<Pairs<IP, Integer>> result = serviceMiner.findMaxChainLevel(ipMiners.get(i).getIp());
            if (result == null) {
                IP tmp = ipMiners.remove(i);
                System.out.println("\nHo rimosso l'IP: " + tmp.getIp());
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
    private void waitAndChooseMiner(List<Future<Pairs<IP, Integer>>> minerResp, Pairs<IP, Integer> designedMiner) throws InterruptedException, ExecutionException {

        Boolean flag = Boolean.TRUE;
        while (flag && !minerResp.isEmpty()) {
            System.out.print("1.5");
            System.out.println("size: " + minerResp.size());
            // Controlliamo se uno dei nostri messaggi di richiesta è tornato
            // indietro con successo
            Thread.sleep(250L);
            for (Future<Pairs<IP, Integer>> f : minerResp) {
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
    private void getBlocksFromMiner(List<IP> ipMiners, Integer myChainLevel, Pairs<IP, Integer> designedMiner, BlockRepository blockRepository) throws IOException {

        Integer i = 0;
        Boolean nullResponse = Boolean.FALSE;
        while (!nullResponse && (i < nBlockUpdate) && (designedMiner.getValue2() > myChainLevel)) {
            // TODO cambire la uri di richiesta
            myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel() + 1;
            Type type = new TypeToken<List<Block>>() {
            }.getType();
            List<Block> blockResponse = HttpUtil.doGetJSON("http://" + designedMiner.getValue1().getIp() + "/fil3chain/getBlock?chainLevel=" + myChainLevel, type);

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

        if (!nullResponse && designedMiner.getValue2() <= blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel()) {
            ipMiners.remove(designedMiner.getValue1());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Transaction> getTransFromDisp(Integer nTrans) throws Exception {
        // TODO List<Transaction> trans = HttpUtil.doGetJSON("http://" + getEntryPointBaseUri() +

        Type type = new TypeToken<List<Transaction>>() {
        }.getType();
        List<Transaction> trans = HttpUtil.doGetJSON("http://" + "10.198.0.7" + ":8080/JsonTransaction?nTrans=" + nTrans, type);

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
     * @param ipEntryPoint the ipEntryPoint to set
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
     * @param portEntryPoint the portEntryPoint to set
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
     * @param entryPointBaseUri the entryPointBaseUri to set
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

    public void initializeBlockChain(BlockRepository blockRepository) {
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
        block.setCreationTime(new Date(0).toString());
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

        if(isVerified) {
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
        for(Transaction transaction: transactionsList) {
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
