package spring.security.impl.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenResreshRequestDTO {

    String expiredAccessToken ;
    String refreshToken ;
}
