package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import java.util.List;

public class FreteDAO {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("TransCargaPU");

    public void cadastrarFrete(Frete frete) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(frete);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public List<Frete> listarFretes() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT f FROM Frete f", Frete.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
    
    public boolean deletarFrete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Frete frete = em.find(Frete.class, id);
            if (frete != null) {
                em.remove(frete);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}
