package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
            json.append("\"destino\":\"").append(escaparJson(f.getDestino())).append("\",");
            json.append("\"peso\":").append(f.getPeso()).append(",");
            json.append("\"transportadora\":\"").append(escaparJson(f.getTransportadora())).append("\"");
            json.append("}");
            if (i < fretes.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        
        PrintWriter out = response.getWriter();
        out.print(json.toString());
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
        
        request.setCharacterEncoding("UTF-8");
        
        String destino = request.getParameter("destino");
        double peso = Double.parseDouble(request.getParameter("peso"));
        String transportadora = request.getParameter("transportadora");

        Frete frete = new Frete();
        frete.setDestino(destino);
        frete.setPeso(peso);
        frete.setTransportadora(transportadora);

        FreteDAO dao = new FreteDAO();
        dao.cadastrarFrete(frete);     
        response.sendRedirect("listarFretes.html");
    }
    
    // DELETE: Remove um frete
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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