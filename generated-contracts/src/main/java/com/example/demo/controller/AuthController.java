package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Role;
import com.example.demo.model.RoleName;
import com.example.demo.model.User;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.jwt.JwtTokenFilter;
import com.example.demo.security.userprincal.UserPrinciple;
import com.example.demo.service.role.RoleServiceImpl;
import com.example.demo.service.user.FileStorageService;
import com.example.demo.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    JwtTokenFilter jwtTokenFilter;
    @Autowired
    private FileStorageService fileStorageService;
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpForm signUpForm) {
        if (userService.existsByUsername(signUpForm.getUsername())) {
            return new ResponseEntity<>(new ResponMessage("no_user"), HttpStatus.OK);
        }
        if (userService.existsByEmail(signUpForm.getEmail())) {
            return new ResponseEntity<>(new ResponMessage("no_email"), HttpStatus.OK);
        }

        User user = new User(
                signUpForm.getName(),
                signUpForm.getUsername(),
                signUpForm.getEmail(),
                passwordEncoder.encode(signUpForm.getPassword())
        );

        Set<String> strRoles = signUpForm.getRoles();
        Set<Role> roles = new HashSet<>();

        // Nếu không có roles từ client, gán mặc định là USER
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleService.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleService.findByName(RoleName.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
                        roles.add(adminRole);
                        break;
                    case "pm":
                        Role pmRole = roleService.findByName(RoleName.PM)
                                .orElseThrow(() -> new RuntimeException("Role PM not found"));
                        roles.add(pmRole);
                        break;
                    default:
                        Role userRole = roleService.findByName(RoleName.USER)
                                .orElseThrow(() -> new RuntimeException("Role USER not found"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userService.save(user);
        return new ResponseEntity<>(new ResponMessage("yes"), HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SignInForm signInForm) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.createToken(authentication);
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            return ResponseEntity.ok(new JwtResponse(token, userPrinciple.getName(), userPrinciple.getEmail(),
                    userPrinciple.getAvatar(), userPrinciple.getAuthorities()));
        } catch (LockedException e) {
            return new ResponseEntity<>(new ResponMessage("locked"), HttpStatus.FORBIDDEN);
        }
    }
    @PutMapping(value = "/change-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> changeAvatar(HttpServletRequest request,
                                          @RequestParam("file") MultipartFile file) {
        String token = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUerNameFromToken(token);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        try {
            String newUrl = fileStorageService.saveAvatar(file);
            String oldUrl = user.getAvatar();

            user.setAvatar(newUrl);
            userService.save(user);

            fileStorageService.deleteAvatar(oldUrl); // xóa ảnh cũ sau khi lưu DB thành công

            return ResponseEntity.ok(Map.of("avatar", newUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request,
                                            @Valid @RequestBody ChangePasswordRequest form) {
        String token = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUerNameFromToken(token);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        // Mật khẩu cũ không đúng
        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            return new ResponseEntity<>(new ResponMessage("wrong_old_password"), HttpStatus.OK);
        }

        // Mật khẩu mới trùng mật khẩu cũ
        if (passwordEncoder.matches(form.getNewPassword(), user.getPassword())) {
            return new ResponseEntity<>(new ResponMessage("same_password"), HttpStatus.OK);
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userService.save(user);
        return new ResponseEntity<>(new ResponMessage("yes"), HttpStatus.OK);
    }
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
        // Lấy token từ request giống như các hàm change-avatar / change-password
        String token = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUerNameFromToken(token);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        // Kiểm tra nếu người dùng thay đổi email mới thì check xem email đó đã tồn tại trong DB chưa
        if (!user.getEmail().equals(updateProfileRequest.getEmail())) {
            if (userService.existsByEmail(updateProfileRequest.getEmail())) {
                return new ResponseEntity<>(new ResponMessage("no_email"), HttpStatus.OK);
                // Hoặc trả về message báo trùng email tùy ý bạn (ở đây dùng "no_email" tương tự lúc đăng ký)
            }
            user.setEmail(updateProfileRequest.getEmail());
        }

        // Cập nhật tên mới
        user.setName(updateProfileRequest.getName());

        userService.save(user);

        return new ResponseEntity<>(new ResponMessage("yes"), HttpStatus.OK);
    }
}
