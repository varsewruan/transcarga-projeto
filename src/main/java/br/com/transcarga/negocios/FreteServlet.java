package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import br.com.transcarga.persistencia.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/FreteServlet")
public class FreteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // GET: Retorna JSON para o front-end
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        FreteDAO dao = new FreteDAO();
        List<Frete> fretes = dao.listarFretes();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Construir JSON na mão (sem biblioteca externa)
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < fretes.size(); i++) {
            Frete f = fretes.get(i);
            json.append("{");
            json.append("\"id\":").append(f.getId()).append(",");
            json.append("\"cep\":\"").append(escaparJson(f.getCep())).append("\",");
            json.append("\"cidade\":\"").append(escaparJson(f.getCidade())).append("\",");
            json.append("\"estado\":\"").append(escaparJson(f.getEstado())).append("\",");
            json.append("\"peso\":").append(f.getPeso()).append(",");
            json.append("\"transportadora\":\"").append(escaparJson(f.getTransportadora())).append("\",");
            json.append("\"timestamp\":");
            if (f.getTimestamp() != null) {
                json.append("\"").append(f.getTimestamp().toString()).append("\"");
            } else {
                json.append("null");
            }
            json.append("}");
            if (i < fretes.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
    }
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Usuario u = (Usuario) session.getAttribute("usuario");
        return u != null && u.isAdmin();
    }

    // Método auxiliar para escapar caracteres especiais no JSON
    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    // POST: Cadastra frete
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\":false,\"message\":\"Apenas admin pode cadastrar fretes\"}");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String cep = request.getParameter("cep");
        String cidade = request.getParameter("cidade");
        String estado = request.getParameter("estado");
        double peso = Double.parseDouble(request.getParameter("peso"));
        String transportadora = request.getParameter("transportadora");

        Frete frete = new Frete();
        frete.setCep(cep);
        frete.setCidade(cidade);
        frete.setEstado(estado);
        frete.setPeso(peso);
        frete.setTransportadora(transportadora);
        frete.setTimestamp(LocalDateTime.now());

        FreteDAO dao = new FreteDAO();
        dao.cadastrarFrete(frete);
        response.sendRedirect("listarFretes.html");
    }
    
    // PUT: Atualiza um frete
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print("{\"success\":false,\"message\":\"Apenas admin pode editar fretes\"}");
            return;
        }

        Map<String, String> params = lerCorpoFormUrlEncoded(request);
        try {
            Long id = Long.parseLong(params.getOrDefault("id", ""));
            String cep = params.get("cep");
            String cidade = params.get("cidade");
            String estado = params.get("estado");
            double peso = Double.parseDouble(params.getOrDefault("peso", ""));
            String transportadora = params.get("transportadora");

            if (cep == null || cep.isBlank() || cidade == null || cidade.isBlank()
                    || estado == null || estado.isBlank()
                    || transportadora == null || transportadora.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"success\":false,\"message\":\"Campos obrigatórios faltando\"}");
                return;
            }

            FreteDAO dao = new FreteDAO();
            boolean ok = dao.atualizarFrete(id, cep, cidade, estado, peso, transportadora);
            if (ok) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print("{\"success\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"success\":false,\"message\":\"Frete não encontrado\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"success\":false,\"message\":\"ID ou peso inválido\"}");
        }
    }

    private Map<String, String> lerCorpoFormUrlEncoded(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = request.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        Map<String, String> map = new HashMap<>();
        if (sb.length() == 0) return map;
        for (String pair : sb.toString().split("&")) {
            int eq = pair.indexOf('=');
            if (eq <= 0) continue;
            String k = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
            String v = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            map.put(k, v);
        }
        return map;
    }

    // DELETE: Remove um frete
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().print("{\"success\":false,\"message\":\"Apenas admin pode excluir fretes\"}");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                Long id = Long.parseLong(idParam);
                FreteDAO dao = new FreteDAO();
                boolean removido = dao.deletarFrete(id);
                if (removido) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print("{\"success\":true}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print("{\"success\":false, \"message\":\"Frete não encontrado\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"success\":false, \"message\":\"ID inválido\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"success\":false, \"message\":\"ID não fornecido\"}");
        }
    }
}