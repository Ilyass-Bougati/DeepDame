package com.deepdame.controller.admin;

import com.deepdame.dto.role.RoleDto;
import com.deepdame.service.role.RoleService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('SUPER-ADMIN')")
@AllArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "admin/roles/list";
    }

    @PostMapping("/create")
    public String createRole(@RequestParam String roleName, RedirectAttributes redirectAttributes) {
        try {
            String formattedName = roleName.toUpperCase();

            roleService.createRole(formattedName);
            redirectAttributes.addFlashAttribute("success", "Role created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    @PostMapping("/delete/{id}")
    public String deleteRole(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            roleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Role deleted successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Security Warning: " + e.getMessage());
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        RoleDto role = roleService.findById(id);
        model.addAttribute("role", role);
        return "admin/roles/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateRole(@PathVariable UUID id, @ModelAttribute RoleDto roleDto, RedirectAttributes redirectAttributes) {
        try {
            roleDto.setId(id);
            roleService.update(roleDto);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully!");
            return "redirect:/admin/roles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating role: " + e.getMessage());
            return "redirect:/admin/roles/edit/" + id;
        }
    }
}
