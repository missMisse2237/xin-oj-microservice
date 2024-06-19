package com.xin.xinojbackendjudgeservice.judge.codesandbox.factory;


import com.xin.xinojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xin.xinojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.xin.xinojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.xin.xinojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.xin.xinojbackendmodel.model.enums.CodeSandboxEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 静态工厂
 * 使用一个Map来缓存一个Cache，这样子只有一个实例；
 */
public class CodeSandboxFactory {

    private static final Map<CodeSandboxEnum, CodeSandbox> codeSandboxCache = new ConcurrentHashMap<>();

    /**
     * 工厂模式
     */
    public static CodeSandbox getInstance(String type) {
        CodeSandboxEnum enumType = CodeSandboxEnum.getEnumByValue(type);
        if (enumType == null) {
            throw new IllegalStateException("未知的CodeSandbox类型");
        }

        // CodeSandboxFactory使用computeIfAbsent来确保每个CodeSandboxEnum类型只被实例化一次：
        return codeSandboxCache.computeIfAbsent(enumType, key -> {
            switch (key) {
                case EXAMPLE_CODESANDBOX:
                    return new ExampleCodeSandbox();
                case REMOTE_CODESANDBOX:
                    return new RemoteCodeSandbox();
                case THIRDPARTY_CODESANDBOX:
                    return new ThirdPartyCodeSandbox();
                default:
                    // 这里理论上不会到达，因为前面已经检查过enumType是否为null
                    throw new IllegalStateException("未知的CodeSandbox类型");
            }
        });
    }

}
