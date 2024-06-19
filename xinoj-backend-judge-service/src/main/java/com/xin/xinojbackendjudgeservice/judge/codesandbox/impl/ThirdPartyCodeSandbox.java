package com.xin.xinojbackendjudgeservice.judge.codesandbox.impl;


import com.xin.xinojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方沙箱(可以拓展
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
