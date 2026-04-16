package com.webapp.controller.admin.user;

import com.webapp.models.dtos.UserDTO;
import com.webapp.services.UserService;
import com.webapp.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewProfile(Model model) {
        Long currentUserId = SecurityUtils.getPrincipal().getId();
        UserDTO userDTO = userService.findById(currentUserId);
        model.addAttribute("user", userDTO);
        return "admin/user/profile";
    }
}
