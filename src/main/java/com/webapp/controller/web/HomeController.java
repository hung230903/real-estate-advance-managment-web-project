package com.webapp.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

  @GetMapping("/")
  public ModelAndView homePage() {
    return new ModelAndView("web/index");
  }

  @GetMapping("/contact")
  public ModelAndView contactPage() {
    return new ModelAndView("web/contact");
  }

  @GetMapping("/403")
  public ModelAndView accessDenied() {
    return new ModelAndView("error/403");
  }
}
