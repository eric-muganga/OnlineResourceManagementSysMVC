package com.eric.onlineresourcemanagementsys.controllers;

import com.eric.onlineresourcemanagementsys.Entity.User;
import com.eric.onlineresourcemanagementsys.services.ServiceResponse;
import com.eric.onlineresourcemanagementsys.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class UserController {
    private final UserService _userService;

    public UserController(UserService userService) {
        this._userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @PostMapping("/loginProcess")
    public String loginProcess(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               Model model, HttpSession session) {
        if(username.isBlank() || password.isBlank()) {
            model.addAttribute("error", "Username and password cannot be empty.");
            return "user/login";
        }

        ServiceResponse<User> response = _userService.loginUser(username, password);
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "user/login";
        }
        session.setAttribute("user", response.getData());
        //System.out.println("User session" + session.getAttribute("user"));
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @PostMapping("/registerProcess")
    public String registerProcess(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  Model model){
        if(username.isBlank() || password.isBlank()) {
            model.addAttribute("error", "Username and password cannot be empty.");
            return "user/register";
        }

        ServiceResponse<User> response = _userService.registerUser(username, password);
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "user/register";
        }
        return "redirect:/user/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear the session
        return "redirect:/login"; // Redirect to login page
    }
}
