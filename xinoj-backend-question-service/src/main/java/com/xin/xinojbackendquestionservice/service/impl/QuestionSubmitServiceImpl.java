package com.xin.xinojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.xinojbackendcommon.common.ErrorCode;
import com.xin.xinojbackendcommon.constant.CommonConstant;
import com.xin.xinojbackendcommon.exception.BusinessException;
import com.xin.xinojbackendcommon.utils.SqlUtils;
import com.xin.xinojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xin.xinojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xin.xinojbackendmodel.model.entity.Question;
import com.xin.xinojbackendmodel.model.entity.QuestionSubmit;
import com.xin.xinojbackendmodel.model.entity.User;
import com.xin.xinojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.xin.xinojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.xin.xinojbackendmodel.model.vo.QuestionSubmitVO;
import com.xin.xinojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.xin.xinojbackendquestionservice.message.MyMessageProducer;
import com.xin.xinojbackendquestionservice.service.QuestionService;
import com.xin.xinojbackendquestionservice.service.QuestionSubmitService;
import com.xin.xinojbackendserviceclient.service.JudgeFeignClient;
import com.xin.xinojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xinji
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-05-26 20:34:03
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MyMessageProducer messageProducer;
    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        // 该用消息队列
        Long questionSubmitId = questionSubmit.getId();
        messageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        // TODO 执行判题服务
//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.doJudge(questionSubmit.getId());
//        });
        return questionSubmit.getId();
    }


    /**
     *  返回一个脱敏包-> 并且检查权限--> 只有创建者本身和管理员可以修改
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }


    /**
     *  脱敏, 获取查询提交VO页
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
//        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
//        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
//        if (CollectionUtils.isEmpty(questionSubmitList)) {
//            return questionSubmitVOPage;
//        }
//        // 对每个QuestionSubmit对象调用getQuestionSubmitVO方法进行转换处理，将每个转换后的QuestionSubmitVO对象收集起来，最终得到一个新的List<QuestionSubmitVO>列表
//        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
//                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
//                .collect(Collectors.toList());
//        // TODO 这里有点问题！实际是是没有userVO 和 questionVO的-->还可以优化，放到一个Cache里，如果Cache里就不再走数据库了（或者题目信息放到Redis？
//        for (QuestionSubmitVO questionSubmitVO : questionSubmitVOList) {
//            User user = userFeignClient.getById(questionSubmitVO.getUserId());
//            Long questionId = questionSubmitVO.getQuestionId();
//            Question question = questionService.getById(questionId);
//            UserVO userVO = userFeignClient.getUserVO(user);
//            QuestionVO questionVO = new QuestionVO();
//            BeanUtils.copyProperties(question, questionVO);
//            questionSubmitVO.setQuestionVO(questionVO);
//            questionSubmitVO.setUserVO(userVO);
//        }
//        questionSubmitVOPage.setRecords(questionSubmitVOList);

//        return questionSubmitVOPage;
        // TODO 这里不显示题目名称和用户名称
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;

    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}




