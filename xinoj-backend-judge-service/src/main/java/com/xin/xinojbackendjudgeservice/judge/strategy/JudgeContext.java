package com.xin.xinojbackendjudgeservice.judge.strategy;


import com.xin.xinojbackendmodel.model.codesandbox.JudgeInfo;
import com.xin.xinojbackendmodel.model.dto.question.JudgeCase;
import com.xin.xinojbackendmodel.model.entity.Question;
import com.xin.xinojbackendmodel.model.entity.QuestionSubmit;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 上下文
 * 使用策略模式的时候，不确定传入什么参数就先创建一个JudgeContext
 */
@Data
@Builder
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
