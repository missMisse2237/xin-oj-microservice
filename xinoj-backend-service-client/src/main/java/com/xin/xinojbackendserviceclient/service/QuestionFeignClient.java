package com.xin.xinojbackendserviceclient.service;

import com.xin.xinojbackendmodel.model.entity.Question;
import com.xin.xinojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xinji
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2024-05-26 20:32:35
 */
@FeignClient(name = "xinoj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient{


    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);

    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

}
