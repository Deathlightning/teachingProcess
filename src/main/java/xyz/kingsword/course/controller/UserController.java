package xyz.kingsword.course.controller;

import cn.hutool.core.lang.Validator;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.service.UserService;
import xyz.kingsword.course.util.ConditionUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static xyz.kingsword.course.enmu.ErrorEnum.UN_LOGIN;


@Api(tags = "用户操作相关类")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "登录", notes = "通过账户密码进行登录")
    @ApiImplicitParam(name = "user", value = "用户名和密码必填", required = true, dataType = "User")
    public Object login(@RequestBody User user, HttpSession session) {
        user = userService.login(user);
        setSession(session, user);
        return new Result<>();
    }

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息")
    public Object userInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        System.out.println(session.getId());
        Optional.ofNullable(user).orElseThrow(() -> new AuthException(UN_LOGIN));
        return new Result<>(userService.getUserInfo(user));
    }


    /**
     * 保留接口，退出时做一些事情
     */
    @ApiOperation(value = "退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Result logout(HttpSession session, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
        }
        session.invalidate();
        return new Result();
    }


    @ApiOperation(value = "重置密码")
    @ApiImplicitParam(name = "newPassword", value = "新密码，md5加密过", required = true, paramType = "form", dataType = "String")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public Result resetPassword(String newPassword, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Optional.ofNullable(user).orElseThrow(() -> new AuthException(UN_LOGIN));
        int flag = userService.resetPassword(newPassword, user);
        Validator.validateTrue(flag == 1, "旧密码错误");
        return new Result();
    }

    @RequestMapping(value = "/loginOnRole", method = RequestMethod.POST)
    @ApiOperation(value = "按角色登录", notes = "通过账户密码进行登录")
    @ApiImplicitParam(name = "roleId", value = "角色Id", required = true, paramType = "form", dataType = "int")
    public Result loginOnRole(HttpServletRequest request, int roleId) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Optional.ofNullable(user).orElseThrow(() -> new AuthException(UN_LOGIN));
        List<Integer> roleList = JSONArray.parseArray(user.getRole()).toJavaList(Integer.class);
        ConditionUtil.validateTrue(roleList.contains(roleId) && roleId < 6).orElseThrow(() -> new AuthException(ErrorEnum.NO_AUTH));
        session.invalidate();
        user.setCurrentRole(roleId);
        user.setCurrentRoleName(RoleEnum.valueOf(roleId).getContent());
        setSession(request.getSession(), user);
        return new Result<>();
    }

    /**
     * 登陆时在session中配置常用变量
     *
     * @param user 登录信息
     */
    private void setSession(HttpSession session, User user) {
        session.setAttribute("user", user);//top上的角色列表
    }
}
