package io.chenyiax;


import io.chenyiax.service.HunyuanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class HunyuanServiceIntegrationTest {
    @Autowired
    HunyuanService hunyuanService;

//    @Test
//    void chatWithRealApi_TextOnly() {
//        // 注意: 这些测试会实际调用API，需要有效的凭证
//        // 建议只在有测试环境时运行，或使用@Disabled注解
//
//        String response = hunyuanService.chat("你好", null);
//        assertNotNull(response);
//        assertFalse(response.isEmpty());
//    }
//
//    @Test
//    void chatWithRealApi_ImageUrl() {
//        String imageUrl = "https://example.com/image.jpg"; // 替换为真实测试图片URL
//        String response = hunyuanService.chat("描述这张图片", imageUrl);
//        System.out.println("API Response: " + response);
//        assertNotNull(response);
//        assertFalse(response.isEmpty());
//    }

//    @Test
//    void chatWithRealApi_Base64Image() {
//        String base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg=="; // 替换为真实图片的base64
//        String response = hunyuanService.chat("这是什么图片?", base64Image);
//        System.out.println("API Response: " + response);
//        assertNotNull(response);
//        assertFalse(response.isEmpty());
//    }

}