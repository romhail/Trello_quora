package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import model.UserDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {
    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Method to delete a particular user-->getUser
     * Request Method is of DELETE Type
     * Exception : UserNotFoundException
     *
     * @return Response Entity <UserDetailsResponse>
     * Status: HttpStatus.OK
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}")
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        UsersEntity userEntity = new UsersEntity();
        userEntity = userBusinessService.getUser(userId);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        if (userEntity == null) {
            return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.NO_CONTENT);
        }
        userDetailsResponse.setAboutMe(userEntity.getAboutme());
        userDetailsResponse.setUserName(userEntity.getUsername());
        userDetailsResponse.setContactNumber(userEntity.getContactnumber());
        userDetailsResponse.setCountry(userEntity.getCountry());
        userDetailsResponse.setDob(userEntity.getDob());
        userDetailsResponse.setEmailAddress(userEntity.getEmail());
        userDetailsResponse.setFirstName(userEntity.getFirstName());
        userDetailsResponse.setLastName(userEntity.getLastName());
        userEntity.setRole("nonadmin");
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

}
