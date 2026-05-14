package com.webapp.controller.admin.user;

import com.webapp.models.dtos.UserDTO;
import com.webapp.services.UserService;
import com.webapp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String viewProfile(Model model) {
        Long currentUserId = SecurityUtils.getPrincipal().getId();
        UserDTO userDTO = userService.findById(currentUserId);
        model.addAttribute("user", userDTO);
        return "admin/user/profile";
    }
}
