package com.example.demo.controller;

import com.example.demo.dto.request.UpdateRolesRequest;
import com.example.demo.dto.request.UserManageDTO;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Role;
import com.example.demo.model.RoleName;
import com.example.demo.model.User;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.jwt.JwtTokenFilter;
import com.example.demo.service.role.RoleServiceImpl;
import com.example.demo.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserManageController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    JwtTokenFilter jwtTokenFilter;

    @GetMapping
    public ResponseEntity<List<UserManageDTO>> getAll() {
        List<UserManageDTO> list = userService.findAll().stream()
                .map(u -> new UserManageDTO(
                        u.getId(), u.getName(), u.getUsername(), u.getEmail(), u.isLocked(),
                        u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<?> setLock(@PathVariable Long id,
                                     @RequestParam boolean locked,
                                     HttpServletRequest request) {
        User target = userService.findById(id).orElse(null);
        if (target == null) {
            return new ResponseEntity<>(new ResponMessage("no_user"), HttpStatus.OK);
        }
        if (target.getUsername().equals(currentUsername(request))) {
            return new ResponseEntity<>(new ResponMessage("self_action"), HttpStatus.OK);
        }
        target.setLocked(locked);
        userService.save(target);
        return new ResponseEntity<>(new ResponMessage("yes"), HttpStatus.OK);
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<?> updateRoles(@PathVariable Long id,
                                         @RequestBody UpdateRolesRequest form,
                                         HttpServletRequest request) {
        User target = userService.findById(id).orElse(null);
        if (target == null) {
            return new ResponseEntity<>(new ResponMessage("no_user"), HttpStatus.OK);
        }

        Set<Role> roles = new HashSet<>();
        if (form.getRoles() != null) {
            for (String r : form.getRoles()) {
                RoleName roleName;
                try {
                    roleName = RoleName.valueOf(r.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(new ResponMessage("invalid_role"), HttpStatus.OK);
                }
                roles.add(roleService.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role " + r + " not found")));
            }
        }
        // Luôn có ít nhất 1 role
        if (roles.isEmpty()) {
            roles.add(roleService.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Role USER not found")));
        }

        // Admin không được tự bỏ quyền ADMIN của chính mình
        boolean hasAdmin = roles.stream().anyMatch(r -> r.getName() == RoleName.ADMIN);
        if (target.getUsername().equals(currentUsername(request)) && !hasAdmin) {
            return new ResponseEntity<>(new ResponMessage("self_action"), HttpStatus.OK);
        }

        target.setRoles(roles);
        userService.save(target);
        return new ResponseEntity<>(new ResponMessage("yes"), HttpStatus.OK);
    }

    private String currentUsername(HttpServletRequest request) {
        return jwtProvider.getUerNameFromToken(jwtTokenFilter.getJwt(request));
    }
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = roleService.findAll().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
