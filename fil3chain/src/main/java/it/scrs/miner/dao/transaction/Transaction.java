/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package it.scrs.miner.dao.transaction;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.user.User;



@Entity
@Table(name = "transaction")
public class Transaction {

	// Columns
	@Id
	@Column(name = "hashFile")
	private String hashFile;
	@Column(name = "filename")
	private String filename;
	// @Column(name = "continues") // Serve per avere blocchi di dimensione fissa
	// private Transaction continues; // True se il numero di citazioni non entra nel blocco e questa è una
	// continuazione di un altra.

	// Relations

	@ManyToOne
	@JoinColumn(name = "Block_hashBlock")
	private Block blockContainer;

	@ManyToOne
	@JoinColumn(name = "User_publicKeyHash") // Autore
	private User authorContainer;

	
	
	@ManyToMany
	@JoinTable(name = "Citations", joinColumns = { 
			@JoinColumn(name = "Transaction_hashFileCite", referencedColumnName = "hashFile") }, inverseJoinColumns = {
			@JoinColumn(name = "Transaction_hashFileCited", referencedColumnName = "hashFile") })
	private List<Transaction> citationsContainer;



	public Transaction(String hashFile, String filename) {
		super();
		this.hashFile = hashFile;
		this.filename = filename;
	}

	public Transaction() {
	}

	/**
	 * @return the hashFile
	 */
	public String getHashFile() {

		return hashFile;
	}

	/**
	 * @param hashFile
	 *            the hashFile to set
	 */
	public void setHashFile(String hashFile) {

		this.hashFile = hashFile;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {

		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {

		this.filename = filename;
	}

	/**
	 * @return the blockContainer
	 */
	public Block getBlockContainer() {

		return blockContainer;
	}

	/**
	 * @param blockContainer
	 *            the blockContainer to set
	 */
	public void setBlockContainer(Block blockContainer) {

		this.blockContainer = blockContainer;
	}

	/**
	 * @return the authorContainer
	 */
	public User getAuthorContainer() {

		return authorContainer;
	}

	/**
	 * @param authorContainer
	 *            the authorContainer to set
	 */
	public void setAuthorContainer(User authorContainer) {

		this.authorContainer = authorContainer;
	}

	/**
	 * @return the citationsContainer
	 */
	public List<Transaction> getCitationsContainer() {

		return citationsContainer;
	}

	/**
	 * @param citationsContainer
	 *            the citationsContainer to set
	 */
	public void setCitationsContainer(List<Transaction> citationsContainer) {

		this.citationsContainer = citationsContainer;
	}

}