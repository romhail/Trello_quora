package swagger.controllers;

import com.upgrad.quora.api.model.*;

import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    /**
     * This api endpoint is used to post a new question
     *
     * @param QuestionRequest   question details for adding new question in QuestionRequest model
     *        authorization string authorisation token
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     */

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
      // create question entity
        final QuestionEntity question = new QuestionEntity();
        question.setContent(questionRequest.getContent());
        question.setDate(ZonedDateTime.now());
        question.setUuid(userAuthTokenEntity.getUuid());
        question.setUser(userAuthTokenEntity.getUser());

        /* Return response with created question entity */
        final QuestionEntity createdQuestion = questionService.createQuestion(question, userAuthTokenEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid())
                .status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }


    /**
     * This api endpoint is used to edit question
     *
     * @param questionEditRequest question details for editing exiting question
     * authorization string authorisation token
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        // Creating question entity for further update
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setUuid(questionId);

        // Return response with updated question entity
        QuestionEntity updatedQuestionEntity = questionService.editQuestionContent(questionEntity, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //for getAllQuestions endpoint.
    /**
     * This api endpoint is used to show all posted questions
     *
     * @param  Authorization string authorisation token
     *
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     */

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") String Authorization, @PathVariable("UserId") String UserUuid) throws AuthorizationFailedException, UserNotFoundException {

        // get all questions
        List<QuestionEntity> listOfQuestions = questionService.getAllQuestionsByUser(Authorization, userBusinessService.getUser(Authorization).getId());

        //create response
        List<QuestionDetailsResponse> questionDetailsResponse = getResponse(listOfQuestions);

        //return response
        return new ResponseEntity<>(questionDetailsResponse, HttpStatus.OK);
    }

    private List<QuestionDetailsResponse> getResponse(List<QuestionEntity> listOfQuestions) {
        List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntity each : listOfQuestions) {
            questionDetailsResponse.add(new QuestionDetailsResponse().id(each.getUuid()).content(each.getContent()));
        }
        return questionDetailsResponse;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * This api endpoint is used to Delete  question
     *
     * @param questionId string questionId details for deleting exiting question
     *        authorization string authererisation token
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     * @throws    InvalidQuestionException if question doesn`t exist
     */

    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("questionId") final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Deleted requested question
        QuestionEntity questionEntity = questionService.deleteQuestion(questionId, accessToken);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionDeleteResponse.setId(questionEntity.getUuid());

        // Return Response
        questionDeleteResponse.setStatus("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET, path ="/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final Integer authorization) throws AuthorizationFailedException, UserNotFoundException {

        // Get all questions for requested user
        List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUser(userId, authorization);

        // Create response
        List<QuestionDetailsResponse> allQuestionDetailsResponse = new ArrayList<QuestionDetailsResponse>();

        for (int i = 0; i < allQuestions.size(); i++) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .content(allQuestions.get(i).getContent())
                    .id(allQuestions.get(i).getUuid());
            allQuestionDetailsResponse.add(questionDetailsResponse);
        }

        // Return response
        return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponse, HttpStatus.FOUND);
    }
}