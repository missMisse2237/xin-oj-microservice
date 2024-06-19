package com.xin.xinojbackendjudgeservice.judge.codesandbox;


import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xin.xinojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口————提高通用性，之后要是项目代码只调用接口，不调用具体的实现类，这样在使用其他的沙箱是不用改代码
 */
public interface CodeSandbox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

    // TODO 拓展点，可以查看沙箱的状态
}
