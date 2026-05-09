package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Usuario;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login.html", "/cadastro.html",
            "/AuthServlet");

    private static final Set<String> ADMIN_ONLY_PAGES = Set.of(
            "/home.html");

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();
        if (path == null)
            path = "";

        if (isPublic(path)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(false);
        Usuario u = (session == null) ? null : (Usuario) session.getAttribute("usuario");

        if (u == null) {
            if ("/".equals(path) || path.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/login.html");
                return;
            }
            if (path.endsWith(".html")) {
                response.sendRedirect(request.getContextPath() + "/login.html");
                return;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\":false,\"message\":\"Não autenticado\"}");
            return;
        }

        if (ADMIN_ONLY_PAGES.contains(path) && !u.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/listarFretes.html");
            return;
        }

        chain.doFilter(req, resp);
    }

    private boolean isPublic(String path) {
        if (PUBLIC_PATHS.contains(path))
            return true;
        if (path.startsWith("/assets/"))
            return true;
        return path.endsWith(".css");
    }
}
