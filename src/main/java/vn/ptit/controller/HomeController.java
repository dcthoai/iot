package vn.ptit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@SuppressWarnings("unused")
public class HomeController {

    @GetMapping
    public String homepage() {
        return "index";
    }
}
