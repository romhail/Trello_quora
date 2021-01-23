package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;
@Autowired
private UserDao userDao;



    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity question, UserAuthEntity userAuthTokenEntity) {
        return questionDao.createQuestion(question);
    }

    public List<QuestionEntity> getAllQuestionsByUser(String Authorization, int userid)
            throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(Authorization);
        validateAuthFailure(userAuthTokenEntity);
        List<QuestionEntity> listOfQuestions = questionDao.getQuestionsByUser(userid);
        if (listOfQuestions == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return listOfQuestions;
    }
    private void validateAuthFailure(UserAuthEntity userAuthTokenEntity) throws AuthorizationFailedException {
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
    }


//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String Token,String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity=userDao.getUserAuthToken(Token);

        if (userAuthEntity!=null){ throw new AuthorizationFailedException("ATHR-001","User has not signed In"); }

//If the UserAuthEntity is logged out and also we check the Authentication Parameters of the Particular user Entity
        if (userAuthEntity.getLogoutAt()!=null && userAuthEntity.getLogoutAt().isAfter(userAuthEntity.getLogoutAt())){ throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete a question"); }


        QuestionEntity questionEntity=questionDao.getQuestionById(questionUuid);
        if (questionEntity==null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (!(userAuthEntity.getUser().getUuid().equals(userAuthEntity.getUser().getUuid()))){
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

          return questionDao.deleteQuestion(questionEntity);

    }





}
