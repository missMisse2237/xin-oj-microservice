package com.xin.xinojbackendjudgeservice.judge;


import com.xin.xinojbackendmodel.model.entity.QuestionSubmit;

public interface JudgeService {

    /**
     * 判题服务
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(Long questionSubmitId);
}
