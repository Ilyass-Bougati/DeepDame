package com.deepdame.controller.admin;

import com.deepdame.entity.User;
import com.deepdame.service.user.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/users")

public class AdminUserController {
    private final UserEntityService userEntityService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> clients = userEntityService.findAll();
        model.addAttribute("users", clients);
        return "admin/user/users";
    }

    @GetMapping("/details/{id}")
    public String userDetails(Model model, @PathVariable UUID id) {
        User user = userEntityService.findById(id);
        model.addAttribute("user", user);
        return "admin/user/user_details";
    }

}
