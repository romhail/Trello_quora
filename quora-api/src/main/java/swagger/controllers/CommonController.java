package swagger.controllers;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import model.UserDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
public class CommonController {
    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    //for signup endpoint
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}")
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

    //for signin endpoint
    @RequestMapping(method = RequestMethod.POST, path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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


    }
}
