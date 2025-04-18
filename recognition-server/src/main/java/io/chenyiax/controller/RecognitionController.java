package io.chenyiax.controller;

import io.chenyiax.entity.ImgRequest;
import io.chenyiax.entity.RestBean;
import io.chenyiax.service.RecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/app")
@RestController
public class RecognitionController {
    @Autowired
    RecognitionService recognitionService;

    @PostMapping("/recognition")
    public RestBean<String> recognition(@RequestBody ImgRequest request) {
        return RestBean.success(recognitionService.recognition(request));
    }
}
