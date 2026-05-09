package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import java.util.List;

public class UsuarioDAO {

    private EntityManagerFactory emf() {
        return JpaUtil.getEmf();
    }

    public Usuario buscarPorLogin(String login) {
        EntityManager em = emf().createEntityManager();
        try {
            List<Usuario> r = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.login = :l", Usuario.class)
                .setParameter("l", login)
                .setMaxResults(1)
                .getResultList();
            return r.isEmpty() ? null : r.get(0);
        } finally {
            em.close();
        }
    }

    public void salvar(Usuario u) {
        EntityManager em = emf().createEntityManager();
        try {
            em.getTransaction().begin();
            if (u.getId() == null) em.persist(u);
            else em.merge(u);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
