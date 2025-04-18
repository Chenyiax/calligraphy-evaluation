package io.chenyiax;

import io.chenyiax.entity.User;
import io.chenyiax.entity.WeChatUserDetails;
import io.chenyiax.mapper.UserMapper;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoginTest {
    @Autowired
    UserMapper mapper;

//    @Test
//    public void testLogin() {
//        User weChatUserDetails = mapper.getUserById(1);
//        System.out.println(weChatUserDetails);
//    }
}
