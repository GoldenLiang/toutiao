package com.lc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.lc.model.Message;

@Mapper
public interface MessageDAO {

	String TABLE_NAME = "message";
	String INSERT_FIELDS = "from_id, to_id, content, has_read, created_date, conversation_id";
	String SELECT_FIELDS = "id, " + INSERT_FIELDS;
		
	@Insert({"insert into " + TABLE_NAME + " (" + INSERT_FIELDS + ")" + " values(#{fromId},"
			+ " #{toId}, #{content}, #{hasRead}, #{createdDate}, #{conversationId})"})
	int addMessage(Message message);
	
	@Select({"select count(id) from " + TABLE_NAME + " where has_read = 0 and to_id = #{toId}"
			+ " and conversation_id = #{conversationId}"})
	int getUnreadCount(@Param("toId")int id, @Param("conversationId")String conversationId);

	@Select({"select " + SELECT_FIELDS + ",count(id) as id from (select * from message"
			+ " where from_id=#{id} or to_id=#{id} order by created_date desc) ss"
			+ " group by conversation_id limit #{offset},#{limit}"})
	List<Message> getConversationList(@Param("id")int localUserId, @Param("offset")int offset, 
			@Param("limit")int limit);
	
	@Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where conversation_id=#{id} "
			+ "order by created_date desc limit #{offset},#{limit}"})
	List<Message> getConversationDetail(@Param("id")String conversationId,  @Param("offset") 
			int offset, @Param("limit")int limit);

	@Select({"select count(conversation_id) from " + TABLE_NAME + " where"
			+ " conversation_id = #{conversationId}"})
	int getConversationCount(@Param("conversationId")String conversationId);
	
}
