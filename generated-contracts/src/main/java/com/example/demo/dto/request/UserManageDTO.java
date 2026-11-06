package com.example.demo.dto.request;

import java.util.Set;

public class UserManageDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private boolean locked;
    private Set<String> roles;

    public UserManageDTO(Long id, String name, String username, String email,
                         boolean locked, Set<String> roles) {
        this.id = id; this.name = name; this.username = username;
        this.email = email; this.locked = locked; this.roles = roles;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isLocked() { return locked; }
    public Set<String> getRoles() { return roles; }
}
