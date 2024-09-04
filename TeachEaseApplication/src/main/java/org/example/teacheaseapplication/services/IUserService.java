package org.example.teacheaseapplication.services;


import org.example.teacheaseapplication.dto.requests.ProfileInformationRequest;
import org.example.teacheaseapplication.dto.requests.UpdatePasswordRequest;
import org.example.teacheaseapplication.dto.responses.NameLastnameResponse;
import org.example.teacheaseapplication.dto.responses.PaginatedUsersResponse;
import org.example.teacheaseapplication.dto.responses.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface IUserService {

UserDetails loadUserByEmail(String email);

 boolean ValidUser(String email);

 ResponseEntity<HttpStatus> updateUserProfile(ProfileInformationRequest profileInformationRequest, Principal principal);


    ResponseEntity<HttpStatus> uploadProfileImage(MultipartFile file, Principal principal);

    ResponseEntity<byte[]> getProfileImage(Principal principal, String email);

    ResponseEntity<UserResponse> getUserProfile(String email);

    ResponseEntity<HttpStatus> updatePassword(UpdatePasswordRequest updatePasswordRequest, Principal principal);
    ResponseEntity<HttpStatus> resetPassword(UpdatePasswordRequest updatePasswordRequest, String code);
    ResponseEntity<UserResponse> getUserProfileByEmail(Principal principal,String email);
    ResponseEntity<HttpStatus> setRole(String email, String role);
    ResponseEntity<PaginatedUsersResponse> getUsers(int page, int size, String keyword);

    ResponseEntity<NameLastnameResponse> getUserNameAndLastName(String email);
}
