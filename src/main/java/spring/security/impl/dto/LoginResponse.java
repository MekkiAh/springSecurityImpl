package spring.security.impl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.security.impl.entities.User;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    String jwt ;
    User user ;
}
