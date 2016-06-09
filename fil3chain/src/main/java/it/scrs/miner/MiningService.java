package it.scrs.miner;

import it.scrs.miner.dao.block.Block;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Marco
 * Date: 08/06/2016
 */
@Service
public class MiningService extends Thread implements Runnable {

    // Blocco da minare
    private Block block;

    // Difficoltà in cui si sta minando
    private Integer difficulty;

    // Maschera per il check dell'hash nei byte interi
    private Integer fullMask;

    // Maschera per il check dell'hash nel byte di "resto"
    private byte restMask;

    // Callback chiamata dopo l'interruzione del thread
    private Runnable interruptCallback;

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
    public MiningService(Block block, Integer difficulty, Runnable interruptCallback) {
        this.block = block;
        this.difficulty = difficulty;
        this.interruptCallback = interruptCallback;
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
        mine();
    }

    /**
     * Metodo per minare un blocco
     */
    public void mine() {

        // Calcolo le maschere per il check dell'hash.
        calculateMasks();

        // Tempo di inizio mining
        long startTime = new Date().getTime();

        // Nonce
        Integer nonce = 0;

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

        // Calcolo hash corretto in esadecimale
        // Spiegazione nonce - 1: Viene fatto -1 perché nell'ultima iterazione viene incrementato anche se l'hash era corretto.
        String hexHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(block.toString() + (nonce - 1));

        // Impostazione dell'hash e del nonce
        block.setHashBlock(hexHash);
        block.setNonce(nonce - 1);

        System.out.println("Hash trovato: " + block.getHashBlock() + " con difficoltà: " + difficulty + " Nonce: " + nonce + " Tempo impiegato: " + (new Date().getTime() - startTime)/1000.0f + " secondi");

        // Chiude il thread
        interrupt();
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

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean isInitialized() {
        return (block != null && difficulty != -1);
    }

}
