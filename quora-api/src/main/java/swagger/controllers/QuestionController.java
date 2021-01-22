package swagger.controllers;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserBusinessService userBusinessService;

    //For create Question method that is implemented below
    @RequestMapping(method = RequestMethod.POST, path = "/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        QuestionEntity question = new QuestionEntity();
        question.setContent(questionRequest.getContent());
        question.setDate(ZonedDateTime.now());
        question.setUuid(userAuthTokenEntity.getUuid());
        question.setUser(userAuthTokenEntity.getUser());

        final QuestionEntity createdQuestion = questionService.createQuestion(question, userAuthTokenEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid())
                .status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") String Authorization, @PathVariable("UserId") String UserUuid) throws AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> listOfQuestions = questionService.getAllQuestionsByUser(Authorization, userBusinessService.getUser(Authorization).getId());
        List<QuestionDetailsResponse> questionDetailsResponse = getCustomizedQuestionResponse(listOfQuestions);

        return new ResponseEntity<>(questionDetailsResponse, HttpStatus.OK);
    }

    private List<QuestionDetailsResponse> getCustomizedQuestionResponse(List<QuestionEntity> listOfQuestions) {
        List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntity each : listOfQuestions) {
            questionDetailsResponse.add(new QuestionDetailsResponse().id(each.getUuid()).content(each.getContent()));
        }
        return questionDetailsResponse;
    }





}