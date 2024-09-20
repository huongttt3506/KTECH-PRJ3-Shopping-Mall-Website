package com.example.ShoppingMall;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/views")
public class ViewController {
    @GetMapping
    public String index() {
        return "index";
    }
    @GetMapping("login")
    public String login() {
        return "login";
    }

    @GetMapping("signup")
    public String signUp() {
        return "signup";
    }

    @GetMapping("user/update")
    public String userUpdate() {
        return "user/update";
    }


}
