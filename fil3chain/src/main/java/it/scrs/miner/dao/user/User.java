/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package it.scrs.miner.dao.user;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.transaction.Transaction;



/**
 *
 *
 */
@Entity
@Table(name = "User")
public class User {

	// Columns
	@Id
	@Column(name = "publicKeyHash")
	private String publicKeyHash;
	@Column(name = "publicKey")
	private String publicKey;
	@Column(name = "name")
	private String name;
	@Column(name = "lastname")
	private String lastName;
	@Column(name = "email")
	private String email;
	@Column(name = "username")
	private String username;

	// Relations
	@OneToMany(mappedBy = "userContainer")
	private List<Block> calculatedBlocks;

	@OneToMany(mappedBy = "authorContainer")
	// @JoinColumn(name = "hashFile")// Autore
	private List<Transaction> fileContainer;
	
	
	
	
	public User(){};

	/**
	 * @param publicKeyHash
	 * @param publicKey
	 * @param name
	 * @param lastName
	 * @param email
	 * @param username
	 */
	public User(String publicKeyHash, String publicKey, String name, String lastName, String email, String username) {
		super();
		this.publicKeyHash = publicKeyHash;
		this.publicKey = publicKey;
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
	}

	/**
	 * @return the publicKeyHash
	 */
	public String getPublicKeyHash() {

		return publicKeyHash;
	}

	/**
	 * @param publicKeyHash
	 *            the publicKeyHash to set
	 */
	public void setPublicKeyHash(String publicKeyHash) {

		this.publicKeyHash = publicKeyHash;
	}

	/**
	 * @return the publicKey
	 */
	public String getPublicKey() {

		return publicKey;
	}

	/**
	 * @param publicKey
	 *            the publicKey to set
	 */
	public void setPublicKey(String publicKey) {

		this.publicKey = publicKey;
	}

	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {

		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {

		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {

		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {

		this.email = email;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {

		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {

		this.username = username;
	}

	/**
	 * @return the calculatedBlocks
	 */
	public List<Block> getCalculatedBlocks() {

		return calculatedBlocks;
	}

	/**
	 * @param calculatedBlocks
	 *            the calculatedBlocks to set
	 */
	public void setCalculatedBlocks(List<Block> calculatedBlocks) {

		this.calculatedBlocks = calculatedBlocks;
	}

	/**
	 * @return the fileContainer
	 */
	public List<Transaction> getFileContainer() {

		return fileContainer;
	}

	/**
	 * @param fileContainer
	 *            the fileContainer to set
	 */
	public void setFileContainer(List<Transaction> fileContainer) {

		this.fileContainer = fileContainer;
	}

}
