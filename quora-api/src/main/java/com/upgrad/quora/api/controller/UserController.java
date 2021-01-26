package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    /*
    *  This Method is used to hit the signUp Endpoint
     * RequestMethod:POST
     * Exception: SignUpRestrictedException
     * path = "/user/signup"
     * Status : User Successfully Registered
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UsersEntity userEntity = new UsersEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        signupUserRequest.setAboutMe(userEntity.getAboutme());
        userEntity.setContactnumber(signupUserRequest.getContactNumber());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setRole("nonadmin");
        final UsersEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid())
                .status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    //for signin endpoint
   /* @RequestMapping(method = RequestMethod.POST, path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> Signin(@RequestHeader("Authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decoder = Base64.getDecoder().decode(authorization.split("Basic")[1]);
        String decorderText = new String(decoder);
        String decodedArray[] = decorderText.split(":");

        UserAuthEntity userAuthEntity = new UserAuthEntity();
        UserAuthEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UsersEntity user = userAuthToken.getUser();

        SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid())
                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);


    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout")
    public ResponseEntity<SignoutResponse> Signout(@RequestHeader("Authorization") final String Authorization) throws SignOutRestrictedException {
        UserAuthEntity userAuthToken = authenticationService.authenticateAndLogout(Authorization); // todo: see if user has
        // already signed out.
        UsersEntity user = userAuthToken.getUser();
        SignoutResponse authorizedUserResponse = new SignoutResponse().id(user.getUuid())
                .message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(authorizedUserResponse, HttpStatus.OK);


    }*/
}