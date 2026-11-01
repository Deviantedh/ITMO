# Веб-программирование (ИТМО, 2 курс)

Комплекс лабораторных работ по дисциплине «Веб-программирование» (факультет ПИиКТ / ФИТиП, Университет ИТМО). Курс демонстрирует эволюцию веб-разработки на Java: от низкоуровневых протоколов FastCGI/HTTP до современных Single Page Applications (SPA) с REST API на Spring Boot и JWT-авторизацией.

---

## 🗂 Структура лабораторных работ

| Лабораторная | Архитектура и стек | Ключевые концепции |
| :--- | :--- | :--- |
| [**Лабораторная №1**](./lab1) | **Низкоуровневый HTTP / FastCGI**<br>`Java`, `FastCGI Server`, `HTML/CSS` | Разбор HTTP-запросов, проверка попадания точки в заданную геометрическую область, валидация входных данных, формирование отклика. |
| [**Лабораторная №2**](./lab2) | **Архитектура MVC на сервлетах**<br>`Java Servlets`, `JSP`, `HTML5 Canvas` | Диспетчеризация (`ControllerServlet`, `AreaCheckServlet`), передача данных через сессии (`HttpSession`), интерактивный кликабельный график. |
| [**Лабораторная №3**](./lab3) | **Компонентный подход на JSF**<br>`JSF 2.x`, `PrimeFaces`, `CDI`, `Hibernate/JPA`, `PostgreSQL` | Жизненный цикл JSF-запроса, валидация через аннотации, персистентность данных через ORM (Hibernate) в базу данных PostgreSQL. |
| [**Лабораторная №4**](./lab4) | **Single Page Application (SPA) & REST**<br>`Spring Boot`, `Spring Security`, `JWT`, `REST API`, `PostgreSQL` | Разделение фронтенда и бэкенда: REST-контроллеры, DTO, защита эндпоинтов по протоколу JWT, stateless сессии, интеграция с СУБД. |

---

## 🛠 Технический стек

* **Бэкенд:** Java 17+, Spring Boot, Java Servlets, JSF / PrimeFaces
* **Базы данных & ORM:** PostgreSQL, Hibernate JPA, Flyway
* **Безопасность:** Spring Security, JWT (JSON Web Tokens), сессионный контекст
* **Фронтенд:** HTML5 Canvas, PrimeFaces UI, JavaScript
