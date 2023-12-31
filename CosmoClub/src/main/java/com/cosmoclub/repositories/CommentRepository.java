package com.cosmoclub.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.cosmoclub.models.Comment;

public interface CommentRepository extends CrudRepository<Comment,Long>{
	
	List<Comment> findAllCommentByPostId(Long id);
	
	Long countByPostId(Long postId);
	
	

}
