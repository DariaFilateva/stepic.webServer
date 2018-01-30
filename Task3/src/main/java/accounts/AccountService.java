package accounts;

import database.DBException;
import database.DBServiceImpl;

/**
 * @author v.chibrikov
 *         <p>
 *         Пример кода для курса на https://stepic.org/
 *         <p>
 *         Описание курса и лицензия: https://github.com/vitaly-chibrikov/stepic_java_webserver
 */
public class AccountService {
    //  private final Map<String, UserProfile> loginToProfile;
    //  private final Map<String, UserProfile> sessionIdToProfile;

    private final DBServiceImpl dbService;

    public AccountService(DBServiceImpl dbService) {
        this.dbService = dbService;
    }

    public void addNewUser(String login, String password) {
        try {
            dbService.addUser(new UserProfile(login,password));
        } catch (DBException e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
        }
    }

    public UserProfile getUserByLogin(String login, String password) {
        try {
            UserProfile userProfile = dbService.getUser(login);
            if (userProfile != null && userProfile.getPass().equals(password)) return userProfile;
            else return null;
        } catch (DBException e) {
            System.out.println("Невозможно войти: " + e.getMessage());
            return null;
        }
    }

  /*  public UserProfile getUserBySessionId(String sessionId) {
        return sessionIdToProfile.get(sessionId);
    }*/

    /*public void addSession(String sessionId, UserProfile userProfile) {
        sessionIdToProfile.put(sessionId, userProfile);
    }*/

 /*   public void deleteSession(String sessionId) {
        sessionIdToProfile.remove(sessionId);
    }*/
}
