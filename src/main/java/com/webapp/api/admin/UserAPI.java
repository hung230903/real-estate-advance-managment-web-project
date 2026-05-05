package com.webapp.api.admin;

import com.webapp.components.JwtTokenUtils;
import com.webapp.constant.SystemConstant;
import com.webapp.models.dtos.LoginDTO;
import com.webapp.models.dtos.PasswordDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.dtos.UserDTO;
import com.webapp.security.MyUser;
import com.webapp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api/users")
@RequiredArgsConstructor
public class UserAPI {


    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            MyUser myUser = (MyUser) authentication.getPrincipal();

            String token = jwtTokenUtils.generateToken(myUser);

            responseDTO.setMessage("Login successful");
            responseDTO.setData(token); // Trả về Token trong field data
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body(responseDTO);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
                responseDTO.setMessage("Validation failed");
                responseDTO.setErrorDetails(errorMessages);
                return ResponseEntity.badRequest().body(responseDTO);
            }

            userDTO.setRoleCode(SystemConstant.USER_ROLE);
            userDTO.setStatus(1); // Active mặc định

            userService.save(userDTO);
            responseDTO.setMessage("Registration Successfully");
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @ModelAttribute UserDTO user, BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

                responseDTO.setMessage("Validation failed");
                responseDTO.setErrorDetails(errorMessages);
                return ResponseEntity.badRequest().body(responseDTO);
            }
            userService.save(user);
            responseDTO.setMessage("Create Successfully");
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @ModelAttribute UserDTO userDTO, BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

                responseDTO.setMessage("Validation failed");
                responseDTO.setErrorDetails(errorMessages);
                return ResponseEntity.badRequest().body(responseDTO);
            }
            userService.update(userDTO);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof MyUser principal) {
                if (principal.getUsername().equals(userDTO.getUserName())) {
                    principal.setFullName(userDTO.getFullName());
                    Authentication newAuth = new UsernamePasswordAuthenticationToken(principal, auth.getCredentials(), auth.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(newAuth);
                }
            }

            responseDTO.setMessage("Update Successfully");
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUsers(@RequestBody List<Long> idList) {
        if (!idList.isEmpty()) {
            userService.delete(idList);
        }
        return ResponseEntity.ok().body("{ \"message\": \"Delete Successfully\" }");
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody PasswordDTO passwordDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        return ResponseEntity.ok().body(responseDTO);
    }
}
