package it.scrs.miner.dao.transaction;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import it.scrs.miner.dao.block.Block;

@RepositoryRestResource(collectionResourceRel = "trsnsactions", path = "transactions")
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findByhashFile(String hashBlock);
}
