package com.kheyma.dto;

import com.kheyma.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String email;
    private String name;
    private Integer age;
    private String phoneNumber;
    private Set<User.Role> roles;
    private boolean active;
    
    public static UserDto fromUser(com.kheyma.model.User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAge(user.getAge());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());
        return dto;
    }
}

