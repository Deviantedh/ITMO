package src.DAOServices;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import src.entities.User;

public class UserDAO {
    public void saveUser(User user) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        }catch (Exception e) {
            if(transaction != null) transaction.rollback();
            System.out.println(e);
        }
    }

    public User getUserById(int id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return user;
        }
    }

    public void saveToken(int userId, String token) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setToken(token);
                session.update(user);
            } else {
                System.out.println("failed find user");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println(e);
        }
    }

    public boolean validateAuthorizedUser(int id, String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            System.out.println(user);
            if (user != null) {
                return user.getToken().equals(token);
            }else{
                return false;
            }
        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public int getUserID(String name){
        User user = getUserByName(name);
        if(user != null)return user.getId();
        return -1;
    }

    public boolean userExists(String username) {
        return getUserByName(username) != null;
    }

    private User getUserByName(String name) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User where username = :name", User.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        }
    }

    public boolean userPasswordMatches(String username, String password) {
        User user = getUserByName(username);
        return user.getPassword().equals(password);
    }
    public void deleteUserById(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                Query query = session.createQuery("DELETE FROM User u WHERE u.id = :userId");
                query.setParameter("userId", userId);

                int deletedCount = query.executeUpdate();

                transaction.commit();
                System.out.println("Deleted " + deletedCount + " results for user ID: " + userId);
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
