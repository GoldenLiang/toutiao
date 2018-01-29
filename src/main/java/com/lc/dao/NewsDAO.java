package com.lc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.lc.model.News;

@Mapper
public interface NewsDAO {

	String TABLE_NAME = "news";
	String SELECT_FILEDS = "id, title, link, image, created_date, user_id";
	String UPDATE_FILEDS = "title, link, image, created_date, user_id";
	
	@Select({"SELECT " + SELECT_FILEDS +"  FROM " + TABLE_NAME + " WHERE id = #{id}"
			+ " ORDER BY id DESC LIMIT #{offset}, #{limit}"})
	List<News> getLastestNews(@Param("id")int id, @Param("offset")int offset, @Param("limit")int limit);
 
}
