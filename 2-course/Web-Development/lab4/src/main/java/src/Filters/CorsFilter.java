package src.Filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация фильтра, если требуется
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Установка CORS-заголовков
            httpResponse.setHeader("Access-Control-Allow-Origin", "*"); // Разрешить запросы с любого домена
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Разрешённые HTTP-методы
            httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization"); // Разрешённые заголовки
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true"); // Разрешить использование cookies

            // Если это preflight-запрос, можно завершить обработку сразу
            if (((HttpServletRequest) request).getMethod().equalsIgnoreCase("OPTIONS")) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        // Продолжение выполнения фильтрации
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Очистка ресурсов, если требуется
    }
}