package com.xin.xinojbackendjudgeservice.judge.strategy;


import com.xin.xinojbackendjudgeservice.judge.strategy.impl.DefaultJudgeStrategy;
import com.xin.xinojbackendjudgeservice.judge.strategy.impl.JavaLanguageJudgeStrategy;
import com.xin.xinojbackendmodel.model.codesandbox.JudgeInfo;
import com.xin.xinojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class StrategyManage {

    /**
     * 策略选择 —————— 为什么返回值类型是JudgeInfo？ 因为原来的 DefaultJudgeStrategy.doJudge(返回的就是JudgeInfo
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (language.equals("java")) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);

    }
}
