package com.xin.xinojbackendjudgeservice.judge.codesandbox.impl;


import com.xin.xinojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.xin.xinojbackendmodel.model.codesandbox.JudgeInfo;
import com.xin.xinojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.xin.xinojbackendmodel.model.enums.QuestionSubmitStatusEnum;

/**
 * 实例代码沙箱，只是为了跑通流程
 */
public class ExampleCodeSandbox implements CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("示例代码沙箱");
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(executeCodeRequest.getInputList());
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        executeCodeResponse.setJudgeInfo(JudgeInfo.builder()
                .message(JudgeInfoMessageEnum.ACCEPTED.getText())
                .time(100L)
                .memory(100L)
                .build());

        return executeCodeResponse;
    }
}
