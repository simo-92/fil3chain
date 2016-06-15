package it.scrs.miner;

import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.block.MerkleTree;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.transaction.TransactionRepository;
import it.scrs.miner.dao.user.User;
import it.scrs.miner.util.CryptoUtil;
import it.scrs.miner.util.IP;
import java.util.ArrayList;

import it.scrs.miner.util.PoolDispatcherUtility;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Marco
 * Date: 08/06/2016
 */
@Service
public class MiningService extends Thread implements Runnable {

    // Blocco da minare
    private Block block;
    
    //Chiave privata del creatore del blocco
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
     * @param block
     * @param difficulty
     */
    public MiningService(List<Transaction> transactions, Block previousBlock, String prKey, String puKey, Block block, Integer difficulty, BlockRepository blockRepository,TransactionRepository transRepo, Runnable interruptCallback) {
        this.block = block;
        this.privateKey = prKey;
        this.publicKey = puKey;
        this.previousBlock = previousBlock;
        this.transactions = transactions;
        this.difficulty = difficulty;
        this.interruptCallback = interruptCallback;
        this.blockRepository = blockRepository;
        this.transRepo=transRepo;
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

        // Potenza di 10, per avere un idea su che iterazione sia il miner
        Integer power = 1;

        do {
            // Genera nuovo hash
            hash = org.apache.commons.codec.digest.DigestUtils.sha256(block.toString() + nonce);

            // Controlla se visualizzare i progressi
            if(nonce.equals(power)) {
                System.out.println("Nuovo tentativo con nonce: " + nonce);
                power *= 10;
            }

            // Incremento il nonce
            nonce++;
		} while (!verifyHash(hash));

        nonceFinish = nonce - 1;
        totalTime = (new Date().getTime() - startTime)/1000.0f;

        // Calcolo hash corretto in esadecimale
        // Spiegazione nonce - 1: Viene fatto -1 perché nell'ultima iterazione viene incrementato anche se l'hash era corretto.
        String hexHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(block.toString() + (nonce - 1));

        // Impostazione dell'hash e del nonce
        block.setHashBlock(hexHash);
        block.setNonce(nonce - 1);
        block.setSignature(CryptoUtil.sign(hexHash, privateKey));
        block.setMinerPublicKey(publicKey);
        block.setFatherBlockContainer(previousBlock);
        block.setTransactionsContainer(transactions);
        
        block.setCreationTime(Long.toString(System.currentTimeMillis()));
        System.out.println("Hash trovato: " + block.getHashBlock() + " con difficoltà: " + difficulty + " Nonce: " + nonce + " Tempo impiegato: " + totalTime + " secondi");
        System.out.println("Hash provati: " + (Math.abs(nonceFinish - nonceStart)) + " HashRate: " + (((Math.abs(nonceFinish - nonceStart))/totalTime)/1000000.0f) + " MH/s");
        // Chiude il thread
        //interrupt();
        // Salvo il blocco
        if (blockRepository != null) blockRepository.save(block);
        //per ogni transazione mette il riferimento al blocco container
        int indexInBlock=0;
        for(Transaction trans: transactions){
            trans.setBlockContainer(block.getHashBlock());
            trans.setIndexInBlock(indexInBlock);
            System.out.println(trans.getIndexInBlock());
            transRepo.save(trans);
            indexInBlock++;
        }
        sendBlockToMiners();

        //TODO: Ricomincia a minare
        initializeService();
        mine();
    }

    @Async
    public Future<List<Block>> sendBlockToMiners() {
        RestTemplate restTemplate =  new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory());
        requestFactory.setConnectTimeout(1000 * 3);
        List<Block> blocks = new ArrayList<>();

        for (IP ip: IPManager.getManager().getIPList()) {
            System.out.println("Invio blocco a: " + ip.getIp());
            try {
                Block newBlock = restTemplate.postForObject("http://" + ip.getIp() + "/fil3chain/newBlock", block, Block.class);
                blocks.add(newBlock);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Il miner " + ip.getIp() + " non è più connesso.");
            }
        }

        // Annullo il blocco appena minato
        block = null;

        return new AsyncResult<>(blocks);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if(interruptCallback != null) interruptCallback.run();
    }

    private Boolean verifyHash(byte[] hash) {

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
        interrupt();
        this.block = miningBlock;
        this.previousBlock = previousBlock;
        this.difficulty = difficulty;
        this.transactions = transactionList;
    }

    public void initializeService() {
        System.out.println("\n interrupt1");
        // Interrompe il servizio
        interrupt();
        System.out.println("\n interrupt2");
        // Prendo l'ultmo blocco della catena
        Block lastBlock = blockRepository.findFirstByOrderByChainLevelDesc();

        // Inizializzo il nuovo blocco da minare
        block = new Block();
        block.setFatherBlockContainer(lastBlock);
        block.setChainLevel(lastBlock.getChainLevel() + 1);
        block.setUserContainer(new User("", "Ciano", "Bug", "Miner", "Mail", "Cianone"));

        // Prendo le transazioni dal Pool Dispatcher
        List<Transaction> transactionsList = PoolDispatcherUtility.getTransactions();

        ArrayList<String> hashTransactions = new ArrayList<>();
        for(Transaction transaction: transactionsList) {
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
