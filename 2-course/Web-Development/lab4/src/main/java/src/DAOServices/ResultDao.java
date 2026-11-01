package src.DAOServices;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import src.entities.Result;

import java.util.ArrayList;
import java.util.List;

public class ResultDao {

    // DAO (Data Access Object) для сущности Result.
    // Отвечает за все операции взаимодействия с базой данных,
    // связанные с сохранением, получением и удалением результатов.

    // Сохраняет объект Result в базе данных.
    // Открывается новая Hibernate-сессия, начинается транзакция,
    // после чего объект сохраняется через session.save().
    public void saveResult(Result result) {
        // Открываем новую сессию Hibernate (подключение к БД).
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                // Начало транзакции — все операции будут выполнены атомарно.
                transaction = session.beginTransaction();
                // Сохраняем переданный объект Result в таблицу базы данных.
                session.save(result);
                // Фиксируем изменения в базе данных.
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }
    }

    // Возвращает список всех результатов, принадлежащих конкретному пользователю.
    // Поиск выполняется по user.id через HQL-запрос.
    public List<Result> getResultsByUserId(int userId) {
        List<Result> results = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL-запрос: выбираем все записи Result,
            // у которых id пользователя совпадает с переданным параметром.
            Query<Result> query = session.createQuery("FROM Result r WHERE r.user.id = :userId", Result.class);
            // Подставляем значение параметра userId в запрос.
            query.setParameter("userId", userId);
            // Выполняем запрос и получаем список результатов.
            results = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Удаляет все результаты, связанные с конкретным пользователем.
    // Используется HQL DELETE-запрос.
    public void deleteResultsByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                // Начинаем транзакцию перед выполнением операции удаления.
                transaction = session.beginTransaction();
                // HQL-запрос для удаления всех записей Result по user.id.
                Query query = session.createQuery("DELETE FROM Result r WHERE r.user.id = :userId");
                // Передаем id пользователя в параметр запроса.
                query.setParameter("userId", userId);
                // Выполняем DELETE-запрос и получаем количество удаленных строк.
                int deletedCount = query.executeUpdate();
                // Подтверждаем транзакцию после успешного удаления.
                transaction.commit();
                // Логируем информацию о количестве удаленных записей.
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
