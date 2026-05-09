package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Usuario;
import br.com.transcarga.persistencia.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("me".equals(action)) {
            HttpSession session = req.getSession(false);
            Usuario u = (session == null) ? null : (Usuario) session.getAttribute("usuario");
            PrintWriter out = resp.getWriter();
            if (u == null) {
                out.print("{\"logged\":false}");
            } else {
                out.print("{\"logged\":true,\"login\":\"" + escaparJson(u.getLogin())
                        + "\",\"admin\":" + u.isAdmin() + "}");
            }
        } else if ("logout".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect("login.html");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"success\":false,\"message\":\"Ação inválida\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        String login = req.getParameter("login");
        String senha = req.getParameter("senha");

        if (login == null || senha == null || login.isBlank() || senha.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"success\":false,\"message\":\"Login e senha são obrigatórios\"}");
            return;
        }

        if ("login".equals(action)) {
            Usuario u = dao.buscarPorLogin(login.trim());
            if (u == null || !BCrypt.checkpw(senha, u.getSenhaHash())) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().print("{\"success\":false,\"message\":\"Credenciais inválidas\"}");
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("usuario", u);
            resp.getWriter().print("{\"success\":true,\"admin\":" + u.isAdmin() + "}");
        } else if ("cadastrar".equals(action)) {
            String loginTrim = login.trim();
            if (dao.buscarPorLogin(loginTrim) != null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().print("{\"success\":false,\"message\":\"Login já existe\"}");
                return;
            }
            Usuario novo = new Usuario();
            novo.setLogin(loginTrim);
            novo.setSenhaHash(BCrypt.hashpw(senha, BCrypt.gensalt()));
            novo.setAdmin(false);
            dao.salvar(novo);
            resp.getWriter().print("{\"success\":true}");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"success\":false,\"message\":\"Ação inválida\"}");
        }
    }

    private String escaparJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
