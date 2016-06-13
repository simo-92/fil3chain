package it.scrs.miner.dao.block;


import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



// findTop10BychainLevelOrderBychainLeveleAsc(Integer cLevel);

@Repository
public interface BlockRepository extends CrudRepository<Block, Long> {

	Block findFirstByOrderByChainLevelDesc();

	Block findByhashBlock(@Param("hashBlock") String hashBlock);

	List<Block> findBychainLevel(@Param("cLevel") Integer cLevel);
	
	List<Block> findBychainLevelGreaterThan(@Param("cLevel") Integer cLevel);

	List<Block> findByChainLevelGreaterThanOrderByChainLevelDesc(@Param("cLevel") Integer cLevel);

	List<Block> findTop10ByChainLevelGreaterThanOrderByChainLevelDesc(@Param("cLevel") Integer cLevel);

}
