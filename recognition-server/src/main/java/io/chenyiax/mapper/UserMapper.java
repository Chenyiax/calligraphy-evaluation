package io.chenyiax.mapper;

import io.chenyiax.entity.User;
import io.chenyiax.entity.WeChatUserDetails;
import io.chenyiax.handler.StringListTypeHandler;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select id from wechat_users where openid = #{openid}")
    Integer getUserIdByOpenid(User user);

    @Insert("insert into wechat_users(openid, session_key, auth) values(#{openid}, #{sessionKey}, 'USER')")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertUser(User user);

    @Select("select * from wechat_users where id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "sessionKey", column = "session_key"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "avatarUrl", column = "avatar_url"),
            @Result(property = "auth", column = "auth", typeHandler = StringListTypeHandler.class)
    })
    User getUserById(Integer id);
}
