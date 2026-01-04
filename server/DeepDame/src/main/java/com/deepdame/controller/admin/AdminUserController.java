package com.deepdame.controller.admin;

import com.deepdame.dto.user.UserDto;
import com.deepdame.entity.User;
import com.deepdame.service.user.UserEntityService;
import com.deepdame.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER-ADMIN')")
@RequiredArgsConstructor

public class AdminUserController {
    private final UserEntityService userEntityService;
    private final UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<User> users = userService.searchUsers(keyword);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/user/users";
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String userDetails(Model model, @PathVariable UUID id) {
        User user = userEntityService.findById(id);
        model.addAttribute("user", user);
        return "admin/user/user_details";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String editUser(@PathVariable UUID id, Model model) {
        UserDto userDto = userService.findById(id);
        model.addAttribute("user", userDto);
        return "admin/user/edit_user";
    }

    @PostMapping("/edit")
    @PreAuthorize("@userSecurity.canManage(#userDto.id, principal)")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
        try {
            userService.update(userDto);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/users/details/" + userDto.getId();
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

}