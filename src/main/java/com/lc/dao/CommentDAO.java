package com.lc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PathVariable;

import com.lc.model.Comment;
import com.lc.model.EntityType;

@Mapper
public interface CommentDAO {

	String TABLE_NAME = "comment";
	String INSERT_FIELDS = "user_id, content, entity_id, entity_type, created_date";
	String SELECT_FIELDS = "id, user_id, content, entity_id, entity_type, created_date";
	
	@Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where entity_id = #{entity_id}"
		+ " and entity_type = #{entity_type} order by id desc"})
	List<Comment> selectByEntity(@Param("entity_id")int id, @Param("entity_type")int entityType);
	
	@Insert({"insert into " + TABLE_NAME + " (" + INSERT_FIELDS + ")" + " values(#{userId},"
			+ " #{content}, #{entityId}, #{entityType}, #{createdDate})"})
	int addComment(Comment comment);
	
	@Select({"select count(id) from " + TABLE_NAME + " where entity_id = #{entity_id}"
			+ " and entity_type = #{entity_type}"})
	int selectCount(@Param("entity_id")int id, @Param("entity_type")int entityType);

	@Select({"select count(id) from " + TABLE_NAME + " where user_id = #{id}"})
	int selectUserCount(@Param("id")int id);
	
	@Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where"
			+ " user_id = #{userId} order by id desc"})
	List<Comment> getUserComments(@Param("userId")int userId);

}
