package it.scrs.miner;

import it.scrs.miner.dao.block.Block;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Marco
 * Date: 08/06/2016
 */
@Service
public class MiningService {

    // Blocco da minare
    private Block block;

    // Difficoltà in cui si sta minando
    private Integer difficulty;

    public MiningService() {
        block = null;
        difficulty = -1;
    }

    /**
     * Costruttore
     * @param block
     * @param difficulty
     */
    public MiningService(Block block, Integer difficulty) {
        this.block = block;
        this.difficulty = difficulty;
    }

    /**
     * Metodo per minare un blocco
     */
    @Async
    public void mine() {

        long startTime = new Date().getTime();

        Integer nonce = 0;
        String hash;
        Integer power = 1;

        while(!block.verifyHash(difficulty)) {
            hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(block.toString() + nonce);
            block.setHashBlock(hash);

            if(nonce.equals(power)) {
                System.out.println("Nuovo tentativo di hash: " + block.getHashBlock() + " Nonce: " + nonce);
                power *= 10;
            }

            nonce++;
            // System.out.println("Nuovo tentativo di hash: " + block.getHashBlock() + " Nonce: " + nonce);
        }

        System.out.println("Hash trovato: " + block.getHashBlock() + " con difficoltà: " + difficulty + " Nonce: " + nonce + " Tempo impiegato: " + (new Date().getTime() - startTime)/1000.0f + " secondi");

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
