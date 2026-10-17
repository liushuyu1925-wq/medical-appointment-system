package org.example.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({
            "/",
            "/login",
            "/register",
            "/forget",
            "/department",
            "/doctor",
            "/appointment",
            "/my-appointments",
            "/medical-record",
            "/profile",
            "/profile/notice",
            "/payment",
            "/patient/reviews",
            "/patient/review",
            "/back/**"
    })
    public String forwardFrontendRoutes() {
        return "forward:/index.html";
    }
}
