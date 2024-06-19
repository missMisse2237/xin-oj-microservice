package com.xin.xinojbackenduserservice.controller.innner;

import com.xin.xinojbackendmodel.model.entity.User;
import com.xin.xinojbackendserviceclient.service.UserFeignClient;
import com.xin.xinojbackenduserservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * 该服务仅内部调用，不是个给前端的
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Autowired
    private UserService userService;

    /**
     * 根据 id 获取用户
     *
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    @Override
    public User getById(@RequestParam("userId") long userId){
        return userService.getById(userId);
    }

    /**
     * 根据 id 获取用户列表
     *
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList){
        return userService.listByIds(idList);
    }
}
