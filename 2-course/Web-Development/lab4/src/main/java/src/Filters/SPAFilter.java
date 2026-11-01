package src.Filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

@WebFilter("/*")
public class SPAFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        System.out.println("Request URI: " + uri);
        System.out.println("Context Path: " + contextPath);

        if (uri.startsWith(contextPath + "/rest/") || uri.startsWith(contextPath + "/static/") ||
                uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/web_4th_lab-1.0/");
        dispatcher.forward(request, response);
    }
}