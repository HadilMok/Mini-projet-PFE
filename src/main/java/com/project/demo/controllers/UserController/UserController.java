package com.project.demo.controllers.UserController;

import com.project.demo.abstractServices.UserAccountAbstractService;
import com.project.demo.models.PasswordUpdater;
import com.project.demo.models.UserAccount;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@Data
@RestController
public class UserController implements UserAccountAbstractService {

    @Override
    public ResponseEntity deleteAccount(UserAccount mainAccountModel) {
        return null;
    }

    @Override
    public ResponseEntity createUserAccount(UserAccount mainAccountModel) {
        return null;
    }

    @Override
    public ResponseEntity createCoachAccount(UserAccount mainAccountModel) {
        return null;
    }

    @Override
    public ResponseEntity updateAccountPassword(PasswordUpdater passwordUpdater, UserAccount account) {
        return null;
    }

    @Override
    public ResponseEntity updateAccountEnableStatus(Long id, String status, UserAccount account) {
        return null;
    }

    @Override
    public ResponseEntity updateAccountExpirationStatus(Long id, boolean status, UserAccount account) {
        return null;
    }

    @Override
    public ResponseEntity updateAccountLockingStatus(Long id, boolean status, UserAccount account) {
        return null;
    }

    @Override
    public ResponseEntity updateAccountTokenExpirationStatus(Long id, boolean status, UserAccount account) {
        return null;
    }

    @Override
    public List<UserAccount> getAllUsers(UserAccount account) {
        return List.of();
    }

    @Override
    public ResponseEntity updateUserByAdmin(UserAccount user, UserAccount account) {
        return null;
    }

    @Override
    public ResponseEntity<UserAccount> getUserById(String Long, UserAccount account) {
        return null;
    }

    @Override
    public ResponseEntity updateLastSeen(UserAccount account) {
        return null;
    }

    @Override
    public ResponseEntity updateUser(UserAccount user, UserAccount account) {
        return null;
    }
}


