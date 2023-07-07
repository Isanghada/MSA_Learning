package com.example.usersservice.controller;

import com.example.usersservice.dto.UserDto;
import com.example.usersservice.service.UserService;
import com.example.usersservice.vo.RequestUser;
import com.example.usersservice.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {
    private final Environment env;
    private final UserService userService;
    @GetMapping("/health_check")
    public String status(){
        return "It's Working in User Service.";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return env.getProperty("greeting.message");
    }
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        return new ResponseEntity(responseUser, HttpStatus.CREATED);
//        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }
}
