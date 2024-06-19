package com.xin.xinojbackendjudgeservice.judge.proxy;


import com.xin.xinojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 静态代理类————  静态代理类只需要实现这个接口，要是动态代理的话就实现InvovationHandle方法，重写handle方法
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱输入信息: {}", executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱输出信息：{}", executeCodeResponse);
        return executeCodeResponse;
    }
}