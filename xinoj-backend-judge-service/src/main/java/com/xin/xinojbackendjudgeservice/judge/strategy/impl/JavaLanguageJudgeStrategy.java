package com.xin.xinojbackendjudgeservice.judge.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.xin.xinojbackendjudgeservice.judge.strategy.JudgeContext;
import com.xin.xinojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.xin.xinojbackendmodel.model.codesandbox.JudgeInfo;
import com.xin.xinojbackendmodel.model.dto.question.JudgeCase;
import com.xin.xinojbackendmodel.model.dto.question.JudgeConfig;
import com.xin.xinojbackendmodel.model.entity.Question;
import com.xin.xinojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JavaLanguageJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题———— 策略
     *
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfoExecute = judgeContext.getJudgeInfo();
        // 实际的消耗
        Long memory = Optional.ofNullable(judgeInfoExecute.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfoExecute.getTime()).orElse(0L);

        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        JudgeInfo judgeInfoResponse = JudgeInfo
                .builder()
                .memory(memory)
                .time(time)
                .message(JudgeInfoMessageEnum.ACCEPTED.getValue())
                .build();


        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.WAITING;
        // 1 判断输入数列和输出数量是否一致 -- 这个不一致肯定错
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 2 依次判断输入和输出结果是否一致
        for (int i = 0; i < inputList.size(); i++) {
            if (!Objects.equals(outputList.get(i), judgeCaseList.get(i).getOutput())) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        // 获取该题目需要的限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONObject.parseObject(judgeConfigStr, JudgeConfig.class);

        /**
         * 假设JAVA实行本身就慢，所以就要放宽松些，这也是策略模式的目的所在，方便扩展，便于维护
         */
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();

        long JAVA_PROGRAM_TIME_COST = 2000; // 编译时间

        if ((time - JAVA_PROGRAM_TIME_COST) > timeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (memory > memoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        return judgeInfoResponse;
    }
}
