package com.project.demo.abstractServices;

import com.project.demo.models.PasswordUpdater;
import com.project.demo.models.UserAccount;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserAccountAbstractService {

    abstract ResponseEntity deleteAccount(UserAccount mainAccountModel);
    abstract ResponseEntity createUserAccount(UserAccount mainAccountModel);
    abstract ResponseEntity createCoachAccount(UserAccount mainAccountModel);
    abstract ResponseEntity updateAccountPassword(PasswordUpdater passwordUpdater, UserAccount account);
    abstract ResponseEntity updateAccountEnableStatus(Long id,String status, UserAccount account);
    abstract ResponseEntity updateAccountExpirationStatus(Long id, boolean status, UserAccount account);
    abstract ResponseEntity updateAccountLockingStatus(Long id, boolean status, UserAccount account);
    abstract ResponseEntity updateAccountTokenExpirationStatus(Long id, boolean status, UserAccount account);
    abstract List<UserAccount> getAllUsers(UserAccount account);
    abstract ResponseEntity updateUserByAdmin(UserAccount user, UserAccount account);
    abstract ResponseEntity<UserAccount> getUserById(String Long, UserAccount account);
    abstract ResponseEntity updateLastSeen(UserAccount account);
    abstract ResponseEntity updateUser(UserAccount user, UserAccount account);
}
