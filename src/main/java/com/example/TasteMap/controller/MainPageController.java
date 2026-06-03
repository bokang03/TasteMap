package com.example.TasteMap.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainPageController {

    @GetMapping({"/", "/pages/main"})
    public ModelAndView mainPage(){
        return new ModelAndView("/main");
    }
}
