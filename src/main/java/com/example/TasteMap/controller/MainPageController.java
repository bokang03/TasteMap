package com.example.TasteMap.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/pages")
public class MainPageController {

    @GetMapping("/main")
    public ModelAndView mainPage(){
        return new ModelAndView("/main");
    }
}
