package com.example.usersservice.controller;

import com.example.usersservice.dto.UserDto;
import com.example.usersservice.entity.UserEntity;
import com.example.usersservice.service.UserService;
import com.example.usersservice.vo.RequestUser;
import com.example.usersservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/")
public class UserController {
    private final Environment env;
    private final UserService userService;
    @GetMapping("/health_check")
    @Timed(value="users.status", longTask = true)
    public String status(){
        return String.format("It's Working in User Service"
                + ", port(local.server.port)="+ env.getProperty("local.server.port")
                + ", port(server.port)="+ env.getProperty("server.port")
                + ", with token secret="+ env.getProperty("token.secret")
                + ", with token time="+ env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    @Timed(value="users.welcome", longTask = true)
    public String welcome(){
        return "welcome page";
    }
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        UserDto returnUserDto = userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(returnUserDto, ResponseUser.class);
        return new ResponseEntity(responseUser, HttpStatus.CREATED);
//        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId")String userId){
        UserDto userDto = userService.getUserByUserId(userId);
        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
