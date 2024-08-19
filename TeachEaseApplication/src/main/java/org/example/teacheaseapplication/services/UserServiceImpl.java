package org.example.teacheaseapplication.services;


import lombok.extern.slf4j.Slf4j;
import org.example.teacheaseapplication.dto.requests.ProfileInformationRequest;
import org.example.teacheaseapplication.dto.requests.UpdatePasswordRequest;
import org.example.teacheaseapplication.dto.responses.UserResponse;
import org.example.teacheaseapplication.models.CodeType;
import org.example.teacheaseapplication.models.CodeVerification;
import org.example.teacheaseapplication.models.User;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;

import java.util.*;


@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final CodeVerificationService codeVerificationService;
    private final PasswordEncoder encoder;
    public UserServiceImpl(UserRepository userRepository, CodeVerificationService codeVerificationService, @Lazy PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.codeVerificationService = codeVerificationService;
        this.encoder = encoder;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found"));
    }

    @Override
    public UserDetails loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found"));
    }

    @Override
    public boolean ValidUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found"));
        return
                user != null
                && user.isAccountNonLocked()
                && user.isAccountNonExpired()
                && user.isCredentialsNonExpired()
                        && user.isEnabled();
    }

    @Override
    public ResponseEntity<HttpStatus> updateUserProfile(ProfileInformationRequest profileInformationRequest, Principal principal) {
        log.info("Updating profile for user: " + principal.getName());
        log.info("Profile information: " + profileInformationRequest.toString());
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(()-> new NoSuchElementException("User not found"));
        String name = profileInformationRequest.getName().toLowerCase();
        String lastName = profileInformationRequest.getLastname().toLowerCase();

        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        lastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);
        user.setName(name);
        user.setLastname(lastName);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<HttpStatus> uploadProfileImage(MultipartFile file, Principal principal) {
        try {
            // Define the path where you want to save the image
            String baseDir = "upload" + File.separator + principal.getName() + File.separator + "profile-image" + File.separator;

            // Create the directory if it doesn't exist
            File dir = new File(baseDir);
            if (!dir.exists()) {
                boolean dirsCreated = dir.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("Failed to create directories");
                }
            }

            // Get the original file name
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            // Generate a random filename
            String newFileName = UUID.randomUUID() + extension;
            // Define the path to the new file
            String filePath = baseDir + newFileName;
            log.info("File path: " + filePath);
            Files.copy(file.getInputStream(), new File(filePath).toPath());
            // Save the file to the server
            //file.transferTo(new File(filePath));

            // Get the user
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(()-> new NoSuchElementException("User not found"));
            //delete old image
            if(user.getImage()!= null)
            {
                File oldImage = new File(user.getImage());
                if(oldImage.exists())
                {
                    oldImage.delete();
                }
            }
            // Save the file path and name in the user's profile
            user.setImage(filePath);
            // Save the user
            userRepository.save(user);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error uploading image: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<byte[]> getProfileImage(Principal principal, String email) {
        try {
            // Get the user
            User user = userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found"));
            if(user.getImage() == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            String filePath = user.getImage();
            // Read the file
            byte[] image = Files.readAllBytes(new File(filePath).toPath());
            return ResponseEntity.ok(image);
        } catch (Exception e) {
            log.error("Error getting image: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<UserResponse> getUserProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found"));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(UserResponse.builder()
                .email(user.getEmail())
                        .name(user.getName())
                        .lastname(user.getLastname())
                        .image(user.getImage())
                .build());
    }

    @Override
    public ResponseEntity<HttpStatus> updatePassword(UpdatePasswordRequest updatePasswordRequest, Principal principal) {
        log.info("Updating password for user: " + principal.getName());
        log.info("Password information: " + updatePasswordRequest.toString());
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(()-> new NoSuchElementException("User not found"));
        if (!encoder.matches(updatePasswordRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        user.setPassword(encoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<HttpStatus> resetPassword(UpdatePasswordRequest updatePasswordRequest, String code) {
        CodeVerification codeVerification = codeVerificationService.verifyCode(code);
        if (codeVerification.getEmail()==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userRepository.findByEmail(codeVerification.getEmail()).orElseThrow(()-> new NoSuchElementException("User not found"));
        user.setPassword(encoder.encode(updatePasswordRequest.getNewPassword()));
        codeVerificationService.deleteCode(codeVerification.getEmail(), CodeType.PASSWORD_RESET);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<UserResponse> getUserProfileByEmail(Principal principal, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found"));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .lastname(user.getLastname())
                .image(user.getImage())
                .build());
    }


}
