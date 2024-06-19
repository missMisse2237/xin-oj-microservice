package com.xin.xinojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.xinojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xin.xinojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xin.xinojbackendmodel.model.entity.QuestionSubmit;
import com.xin.xinojbackendmodel.model.entity.User;
import com.xin.xinojbackendmodel.model.vo.QuestionSubmitVO;


/**
* @author xinji
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-05-26 20:34:03
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装(脱敏
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
