package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Usuario;
import br.com.transcarga.persistencia.UsuarioDAO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.mindrot.jbcrypt.BCrypt;

@WebListener
public class SeedListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            UsuarioDAO dao = new UsuarioDAO();
            if (dao.buscarPorLogin("admin") == null) {
                Usuario admin = new Usuario();
                admin.setLogin("admin");
                admin.setSenhaHash(BCrypt.hashpw("admin", BCrypt.gensalt()));
                admin.setAdmin(true);
                dao.salvar(admin);
            }
        } catch (Exception e) {
            sce.getServletContext().log("Falha ao criar usuário admin: " + e.getMessage(), e);
        }
    }
}
