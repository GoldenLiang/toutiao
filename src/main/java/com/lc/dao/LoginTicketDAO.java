package com.lc.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.lc.model.LoginTicket;


@Mapper
public interface LoginTicketDAO {
	 String TABLE_NAME = "login_ticket";
	    String INSET_FIELDS = "user_id, status, ticket, expired, salt";
	    String SELECT_FIELDS = "id, user_id, status, ticket, expired, salt";

	    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS, 
	            ") values (#{userId},#{status},#{ticket},#{expired},#{salt})"})
	    int addTicket(LoginTicket ticket);

	    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
	    LoginTicket selectById(int id);
	    
	    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
	    LoginTicket selectByTicket(String ticket);
	    
	    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
	    void updateTicketStatus(@Param("ticket")String ticket, @Param("status")int status);

	    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
	    void deleteById(int id);
}
