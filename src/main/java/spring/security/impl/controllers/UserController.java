package spring.security.impl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.impl.dto.TokenResreshRequestDTO;
import spring.security.impl.dto.UserLoginDTO;
import spring.security.impl.entities.User;
import spring.security.impl.services.UserService;

import javax.validation.Valid;

@RestController
@CrossOrigin("")
public class UserController {
    @Autowired
    UserService userService ;
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id)
    {
        return userService.getUserById(id);
    }
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody @Valid User user) throws Exception {
        return userService.signUp(user);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO userLoginDTO ){
        return userService.login(userLoginDTO);
    }
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refresh(@RequestBody TokenResreshRequestDTO tokenResreshRequestDTO)
    {
          return userService.refreshToken(tokenResreshRequestDTO);
    }
}
