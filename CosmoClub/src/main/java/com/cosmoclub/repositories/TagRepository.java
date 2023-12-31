package com.cosmoclub.repositories; 

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cosmoclub.models.Tag;

@Repository
public interface TagRepository extends CrudRepository<Tag,Long>{
	
	List<Tag> findAll();
	List<Tag> findAllByIdIn(List<Long> ids);
}
