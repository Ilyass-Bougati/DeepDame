package com.deepdame.controller.admin;

import com.deepdame.dto.user.UserDto;
import com.deepdame.entity.User;
import com.deepdame.service.statistic.StatisticsService;
import com.deepdame.service.user.UserEntityService;
import com.deepdame.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER-ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserEntityService userEntityService;
    private final UserService userService;
    private final StatisticsService statisticsService;

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
        model.addAttribute("stats", statisticsService.getPlayerStats(id));
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

    @PostMapping("/{id}/ban-app")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String banFromApp(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Action: Banning User ID {} from the application", id);
        userService.banFromApp(id);
        redirectAttributes.addFlashAttribute("success", "User has been banned from the application.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unban-app")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String unbanFromApp(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Action: Unbanning User ID {} from the application", id);
        userService.unbanFromApp(id);
        redirectAttributes.addFlashAttribute("info", "Application access restored for this user.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/ban-chat")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String banFromChat(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Action: Banning User ID {} from chat", id);
        userService.banFromChat(id);
        redirectAttributes.addFlashAttribute("success", "User is now restricted from sending messages.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unban-chat")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String unbanFromChat(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Action: Restoring chat access for User ID {}", id);
        userService.unbanFromChat(id);
        redirectAttributes.addFlashAttribute("info", "User can now use the chat again.");
        return "redirect:/admin/users";
    }
}