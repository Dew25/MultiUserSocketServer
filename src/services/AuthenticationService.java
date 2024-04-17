/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.GregorianCalendar;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import user.User;


/**
 *
 * @author Melnikov
 */
public class AuthenticationService {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean authenticateUser(String username, String password) {
        try {
            User user = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (user.getPassword().equals(password)) {
                // Аутентификация успешна
                user.setDate(new GregorianCalendar().getTime()); // Обновляем дату последней авторизации
                entityManager.merge(user);
                return true;
            } else {
                // Неверный пароль
                return false;
            }
        } catch (Exception e) {
            // Пользователь с таким именем не найден
            return false;
        }
    }
}
