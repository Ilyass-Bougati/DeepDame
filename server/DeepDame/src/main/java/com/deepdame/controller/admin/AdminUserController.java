package com.deepdame.controller.admin;

import com.deepdame.dto.notification.BanNotification;
import com.deepdame.dto.role.RoleDto;
import com.deepdame.dto.user.UserDto;
import com.deepdame.entity.Role;
import com.deepdame.entity.User;
import com.deepdame.exception.NotFoundException;
import com.deepdame.service.role.RoleService;
import com.deepdame.service.statistic.StatisticsService;
import com.deepdame.service.user.UserEntityService;
import com.deepdame.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
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
    private final RoleService roleService;
    private final StatisticsService statisticsService;
    private final SimpMessagingTemplate messagingTemplate;

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

        String topic = "/topic/application-ban/" + id;
        messagingTemplate.convertAndSend(topic, new BanNotification(true));
        log.info("App ban signal sent to topic: {}", topic);

        redirectAttributes.addFlashAttribute("success", "User has been banned from the application.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unban-app")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String unbanFromApp(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Action: Unbanning User ID {} from the application", id);
        userService.unbanFromApp(id);

        messagingTemplate.convertAndSend("/topic/application-ban/" + id, new BanNotification(false));

        redirectAttributes.addFlashAttribute("info", "Application access restored for this sender.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/ban-chat")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String banFromChat(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Action: Banning User ID {} from chat", id);
        userService.banFromChat(id);

        messagingTemplate.convertAndSend("/topic/chat-ban/" + id, new BanNotification(true));

        redirectAttributes.addFlashAttribute("success", "User is now restricted from sending messages.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unban-chat")
    @PreAuthorize("@userSecurity.canManage(#id, principal)")
    public String unbanFromChat(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Action: Restoring chat access for User ID {}", id);
        userService.unbanFromChat(id);

        messagingTemplate.convertAndSend("/topic/chat-ban/" + id, new BanNotification(false));

        redirectAttributes.addFlashAttribute("info", "User can now use the chat again.");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('SUPER-ADMIN')")
    public String showEditRolesForm(@PathVariable UUID id, Model model) {
        try {
            UserDto user = userService.findById(id);
            List<Role> allRoles = roleService.findAll();

            model.addAttribute("user", user);
            model.addAttribute("allRoles", allRoles);
            return "admin/user/edit-roles";
        } catch (NotFoundException e) {
            return "redirect:/admin/users?error=UserNotFound";
        }
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('SUPER-ADMIN')")
    public String updateUserRoles(@PathVariable UUID id,
                                  @RequestParam(required = false) List<UUID> roleIds,
                                  RedirectAttributes redirectAttributes) {
        try {
            List<UUID> idsToProcess = (roleIds != null) ? roleIds : new ArrayList<>();

            userService.updateUserRoles(id, idsToProcess);

            redirectAttributes.addFlashAttribute("success", "Permissions updated successfully for this sender.");
            return "redirect:/admin/users";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/" + id + "/roles";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred.");
            return "redirect:/admin/users";
        }
    }
}