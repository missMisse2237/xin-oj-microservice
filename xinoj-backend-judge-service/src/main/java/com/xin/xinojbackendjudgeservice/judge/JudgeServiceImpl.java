package com.xin.xinojbackendjudgeservice.judge;

import com.alibaba.fastjson.JSONObject;
import com.xin.xinojbackendcommon.common.ErrorCode;
import com.xin.xinojbackendcommon.exception.BusinessException;
import com.xin.xinojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xin.xinojbackendjudgeservice.judge.codesandbox.factory.CodeSandboxFactory;
import com.xin.xinojbackendjudgeservice.judge.proxy.CodeSandboxProxy;
import com.xin.xinojbackendjudgeservice.judge.strategy.JudgeContext;
import com.xin.xinojbackendjudgeservice.judge.strategy.StrategyManage;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.xin.xinojbackendmodel.model.codesandbox.JudgeInfo;
import com.xin.xinojbackendmodel.model.dto.question.JudgeCase;
import com.xin.xinojbackendmodel.model.entity.Question;
import com.xin.xinojbackendmodel.model.entity.QuestionSubmit;
import com.xin.xinojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.xin.xinojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Value("${codesandbox.type}")
    private String type;



    /**
     * 通过题目ID 获取到输入用例和输出结果---> 封装判题结果
     */
    @Autowired
    private QuestionFeignClient questionFeignClient;

    /**
     * 策略模式 通过变成语言选择策略
     */
    @Autowired
    private StrategyManage strategyManage;

    /**
     * 判题服务
     *
     * @param questionSubmitId
     * @return
     */
    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        // 1. 通过questionSubmitId 获取到题目questionSubmit TODO 这里就报错了！
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "在服务器中未检索到该信息");
        }
        Long questionId = questionSubmit.getQuestionId();
        // 2. 判断题目状态是否为Waiting （如果不是Waiting就不给他判，直接报错
        if (!Objects.equals(questionSubmit.getStatus(), QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3. 将题目状态设置为判题中--》先相应用户，异步判题
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean isUpdate = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!isUpdate) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目信息更新失败");
        }
        // 4. 代码沙箱判题
        CodeSandbox codeSandbox = CodeSandboxFactory.getInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
            // 4.1 获取题目输入用例
        Question question = questionFeignClient.getQuestionById(questionId);
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONObject.parseArray(judgeCase, JudgeCase.class);
            // 4.2封装成inputList
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .inputList(inputList)
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5.根据沙箱中的结果返回结果
        JudgeContext judgeContext = JudgeContext.builder()
                .judgeCaseList(judgeCaseList)
                .judgeInfo(executeCodeResponse.getJudgeInfo())
                .outputList(outputList)
                .inputList(inputList)
                .question(question)
                .questionSubmit(questionSubmit)
                .build();
        // 策略模式，一般会把策略的选择也单独的定义一个类
        JudgeInfo judgeInfo = strategyManage.doJudge(judgeContext);
        // 修改数据库中的结果
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONObject.toJSONString(judgeInfo));
        isUpdate = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!isUpdate) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目信息更新失败");
        }
        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }
}
