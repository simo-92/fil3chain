package it.scrs.miner.dao.transaction;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import it.scrs.miner.dao.block.Block;

@RepositoryRestResource(collectionResourceRel = "trsnsactions", path = "transactions")
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findByhashFile(String hashBlock);

    Block findByHashFile(@Param("hashFile") String hashFile);

}
