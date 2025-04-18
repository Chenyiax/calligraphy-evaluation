package io.chenyiax.service;

import io.chenyiax.configuration.TencentCloudConfig;
import io.chenyiax.entity.ImgRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecognitionService {
    @Autowired
    HunyuanService hunyuanService;

    public String recognition(ImgRequest request) {
        return hunyuanService.chat("""
                你是一位小学书法老师。请根据提供的书法作品，从以下几个方面进行分析：
                1. 笔法分析：评价笔画的力度、流畅度和技法运用
                2. 结构分析：评价字形结构、比例和空间安排
                3. 章法分析：评价整体布局、行气连贯性和节奏感
                4. 改进建议：针对不足之处提出具体改进建议""", request.getImg());
    }
}
