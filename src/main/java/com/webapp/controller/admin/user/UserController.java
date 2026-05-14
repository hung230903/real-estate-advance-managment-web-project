package com.webapp.controller.admin.user;

import com.webapp.constant.SystemConstant;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.UserDTO;
import com.webapp.pagination.PaginationResult;
import com.webapp.services.UserService;
import com.webapp.utils.MessageUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageUtils messageUtils;

    @GetMapping("/list")
    public ModelAndView userList(@RequestParam(value = "page", defaultValue = "1") String pageStr, @RequestParam(value = "key", required = false) String key, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/user/userList");
        int page = 1;
        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int MAX_RESULT = 3;
        final int MAX_NAVIGATION_PAGE = 3;
        PaginationResult<UserEntity> paginationResult = userService.listUserInfo(key, page, MAX_RESULT, MAX_NAVIGATION_PAGE);
        modelAndView.addObject("model", paginationResult);
        initMessageResponse(modelAndView, request);
        return modelAndView;
    }

    private void initMessageResponse(ModelAndView mav, HttpServletRequest request) {
        String message = request.getParameter("message");
        if (StringUtils.isNotEmpty(message)) {
            Map<String, String> messageMap = messageUtils.getMessage(message);
            mav.addObject(SystemConstant.ALERT, messageMap.get(SystemConstant.ALERT));
            mav.addObject(SystemConstant.MESSAGE_RESPONSE, messageMap.get(SystemConstant.MESSAGE_RESPONSE));
        }
    }

    @GetMapping("/{userName}")
    public ModelAndView getUser(@PathVariable String userName, HttpServletRequest request) {
        ModelAndView model = new ModelAndView("admin/user/userEdit");
        UserDTO userDTO = userService.findByUserName(userName);
        model.addObject("user", userDTO);
        initMessageResponse(model, request);
        return model;
    }

    @GetMapping()
    public ModelAndView addUser(UserDTO user, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/user/userEdit");
        user.initRoles();
        initMessageResponse(modelAndView, request);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/userImage")
    public void userImage(HttpServletResponse response, @RequestParam(value = "userName", defaultValue = "") String userName) throws IOException {
        byte[] image = userService.getImage(userName);
        if (image != null) {
            response.setContentType("image/jpeg");
            response.getOutputStream().write(image);
        }
        response.getOutputStream().close();
    }

    @GetMapping("/change-password/{id}")
    public ModelAndView resetPassword(@PathVariable Long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/user/change-password");
        UserDTO user = userService.findById(id);
        initMessageResponse(modelAndView, request);
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}

