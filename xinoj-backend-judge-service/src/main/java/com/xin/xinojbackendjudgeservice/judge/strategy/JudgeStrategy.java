package com.xin.xinojbackendjudgeservice.judge.strategy;


import com.xin.xinojbackendmodel.model.codesandbox.JudgeInfo;

public interface JudgeStrategy {

    /**
     * 执行判题———— 策略
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
