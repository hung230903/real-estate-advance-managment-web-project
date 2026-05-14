package com.webapp.controller.admin;

import com.webapp.models.dtos.UserDTO;
import com.webapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new UserDTO());
        return mav;
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("user") UserDTO userDTO, @RequestParam("confirmPassword") String confirmPassword) {
        if (!userDTO.getPassword().equals(confirmPassword)) {
            return "redirect:/register?passwordMismatch=true";
        }
        
        userDTO.setRoleCode("ROLE_USER");
        userDTO.setStatus(1); // Set active by default
        try {
            userService.save(userDTO);
        } catch (Exception e) {
            return "redirect:/register?error=true";
        }
        return "redirect:/login?registered=true";
    }
}
