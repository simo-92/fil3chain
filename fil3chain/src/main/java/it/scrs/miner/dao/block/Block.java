/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package it.scrs.miner.dao.block;


import com.fasterxml.jackson.annotation.JsonIgnore;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.user.User;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;



/**
 *
 * 
 */
@Entity
public class Block {

	// Columns
	@Id
	private String hashBlock; // ProofOfWOrk //Costituisce l'id del blocco, per
								// essere identificato in maniera UNIVOCA nella
								// blockchain
	@Column(nullable = false)
	private String creationTime;

    @Column(nullable = false)
	private String merkleRoot;

    @Column(nullable = false)
	private String minerPublicKey;

    @Column(nullable = false)
	private Integer nonce;

	@Column(nullable = false)
	private Integer chainLevel;
        
        @Column(nullable=false)
        private String signature; //firma
	// Difficoltà per la proof of work ,inviato da poolDispatcher

	// Relationship

	@OneToMany(mappedBy = "blockContainer",fetch = FetchType.EAGER)
	private List<Transaction> transactionsContainer;

	@OneToOne
	@JoinColumn(name = "Block_prevHashBlock")
	private Block fatherBlockContainer;

	@ManyToOne
	@JoinColumn(name = "User_publicKeyHash") // Autore
	private User userContainer;


	/**
	 * 
	 */
	public Block() {

	}

	/**
	 * @param hashBlock
	 * @param creationTime
	 * @param merkleRoot
	 * @param minerPublicKey
	 * @param nonce
	 * @param chainLevel
	 */
	public Block(String hashBlock, String merkleRoot, String minerPublicKey, Integer nonce, Integer chainLevel) {
		super();
		this.hashBlock = hashBlock;
		this.creationTime = Long.toString(System.currentTimeMillis());
		this.merkleRoot = merkleRoot;
		this.minerPublicKey = minerPublicKey;
		this.nonce = nonce;
		this.chainLevel = chainLevel;
	}

	/**
	 * @param creationTime
	 * @param merkleRoot
	 * @param minerPublicKey
	 * @param nonce
	 * @param chainLevel
	 * @param transactionsContainer
	 * @param fatherBlockContainer
	 * @param userContainer
	 */
	public Block(String merkleRoot, String minerPublicKey, Integer nonce, Integer chainLevel, List<Transaction> transactionsContainer, Block fatherBlockContainer, User userContainer) {
		super();
		this.creationTime = Long.toString(System.currentTimeMillis());
		this.merkleRoot = merkleRoot;
		this.minerPublicKey = minerPublicKey;
		this.nonce = nonce;
		this.chainLevel = chainLevel;
		this.transactionsContainer = transactionsContainer;
		this.fatherBlockContainer = fatherBlockContainer;
		this.userContainer = userContainer;
	}


	public Boolean verifyHash(int difficulty) {

        // Se non è presente alcun hash allora ritorna falso
        if (hashBlock == null || hashBlock.isEmpty()) return Boolean.FALSE;

        // Costruisco la stringa di zeri che deve essere presente
        // all'inizio dell'hash, data la difficoltà
        String puzzle = "";

		/*
        for(int i = 1; i <= difficulty; i++) {
            puzzle += "1";
        }
        */

        for(int i = 0; i < difficulty; i++) {
            puzzle += "0";
        }

        // BigInteger puzzleInt = new BigInteger(puzzle, 2);
        // return puzzleInt.and(new BigInteger(hashBlock, 16)).compareTo(new BigInteger("0")) == 0;

        // Restituisce true se l'hash corrente inizia con la stringa definita da puzzle
        // e se l'hash è un hash del blocco.
        // False altrimenti
        return (hashBlock.startsWith(puzzle));

		/*
		Integer diff, i;
		diff = 2;
		i = 0;
		String s = this.getHashBlock();
		String puzzle = "";

		while (i < diff) {
			puzzle += "0";
			i++;
		}

		this.setHashBlock(null);
		
		String hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.toString());
		
		this.setHashBlock(hash);
		if (s.startsWith(puzzle) && (s.compareTo(hash) == 0))
			return Boolean.TRUE;
		return Boolean.FALSE;
		*/
	}

    @Override
    public String toString() {
        return fatherBlockContainer.getHashBlock() + merkleRoot + userContainer.getPublicKey();
    }
	
	//MychainLevel=Chiedi ultimo blocco nel db
	//se la differenza tra il mio blocco e il blocco è arrivato
	// se è negativa 
		//ed è 1 è successivo al mio blocco
		// facio la verifica
	
	
	
	//verifica se esiste un padre 
	//Verify MerkleRooot
	////rooot = MerkleTree(getTransaction)
	//rooot.stringCompare(MerkleTree)
	
	
	
	
	
	// public void generateVerifiedHashBlock() {
	//
	// // richiesta a PoolDispatcher per saper la difficolta
	// try {
	// difficulty = Integer.getInteger(HttpUtil.doGet("http://localhost:8080/poolDispatcher"));
	// } catch (Exception e) {
	// difficulty = 0;
	// e.printStackTrace();
	// }
	// String s = "";
	// ArrayList<Integer> puzzle = new ArrayList<Integer>();
	// for (int i = 0; i < difficulty; i++)
	// puzzle.add(0);
	// int j=0;
	// this.setNonce(j);
	// do {
	// s = org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.toString());
	// j++;
	// this.setNonce(j);
	// } while (s.startsWith(puzzle.toString()));
	// this.setHashBlock(s);
	//
	// }

	public void generateHashBlock() {
		String s = org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.toString());
		this.setHashBlock(s);
	}

	public String generateAndGetHashBlock() {

		String s = org.apache.commons.codec.digest.DigestUtils.sha256Hex(this.toString());
		this.setHashBlock(s);
		return s;
	}

	/**
	 * @param merkleRoot
	 * @param minerPublicKey
	 * @param nonce
	 * @param chainLevel
	 */
	public Block(String merkleRoot, String minerPublicKey, Integer nonce, Integer chainLevel) {
		super();
		this.merkleRoot = merkleRoot;
		this.minerPublicKey = minerPublicKey;
		this.nonce = nonce;
		this.chainLevel = chainLevel;
	}

	/**
	 * @return the hashBlock
	 */
	public String getHashBlock() {

		return hashBlock;
	}

	/**
	 * @param hashBlock
	 *            the hashBlock to set
	 */
	public void setHashBlock(String hashBlock) {

		this.hashBlock = hashBlock;
	}

	/**
	 * @return the creationTime
	 */
	public String getCreationTime() {

		return creationTime;
	}

	/**
	 * @param creationTime
	 *            the creationTime to set
	 */
	public void setCreationTime(String creationTime) {

		this.creationTime = creationTime;
	}

	/**
	 * @return the merkleRoot
	 */
	public String getMerkleRoot() {

		return merkleRoot;
	}

	/**
	 * @param merkleRoot
	 *            the merkleRoot to set
	 */
	public void setMerkleRoot(String merkleRoot) {

		this.merkleRoot = merkleRoot;
	}

	/**
	 * @return the minerPublicKey
	 */
	public String getMinerPublicKey() {

		return minerPublicKey;
	}

	/**
	 * @param minerPublicKey
	 *            the minerPublicKey to set
	 */
	public void setMinerPublicKey(String minerPublicKey) {

		this.minerPublicKey = minerPublicKey;
	}

	/**
	 * @return the nonce
	 */
	public Integer getNonce() {

		return nonce;
	}

	/**
	 * @param nonce
	 *            the nonce to set
	 */
	public void setNonce(Integer nonce) {

		this.nonce = nonce;
	}

	/**
	 * @return the chainLevel
	 */
	public Integer getChainLevel() {

		return chainLevel;
	}

	/**
	 * @param chainLevel
	 *            the chainLevel to set
	 */
	public void setChainLevel(Integer chainLevel) {

		this.chainLevel = chainLevel;
	}

	/**
	 * @return the transactionsContainer
	 */
	public List<Transaction> getTransactionsContainer() {

		return transactionsContainer;
	}

	/**
	 * @param transactionsContainer
	 *            the transactionsContainer to set
	 */
	public void setTransactionsContainer(List<Transaction> transactionsContainer) {

		this.transactionsContainer = transactionsContainer;
	}

	/**
	 * @return the fatherBlockContainer
	 */
	public Block getFatherBlockContainer() {

		return fatherBlockContainer;
	}

	/**
	 * @param fatherBlockContainer
	 *            the fatherBlockContainer to set
	 */
	public void setFatherBlockContainer(Block fatherBlockContainer) {

		this.fatherBlockContainer = fatherBlockContainer;
	}

	/**
	 * @return the userContainer
	 */
	public User getUserContainer() {

		return userContainer;
	}

	/**
	 * @param userContainer the userContainer to set
	 */
	public void setUserContainer(User userContainer) {

		this.userContainer = userContainer;
	}

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

}
