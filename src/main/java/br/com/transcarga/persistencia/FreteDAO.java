package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import java.util.List;

public class FreteDAO {

    private static EntityManagerFactory emf() {
        return JpaUtil.getEmf();
    }

    public void cadastrarFrete(Frete frete) {
        EntityManager em = emf().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(frete);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public List<Frete> listarFretes() {
        EntityManager em = emf().createEntityManager();
        try {
            return em.createQuery("SELECT f FROM Frete f", Frete.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // retorna lista vazia em caso de erro
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
    
    public boolean atualizarFrete(Long id, String cep, String cidade, String estado, double peso, String transportadora) {
        EntityManager em = emf().createEntityManager();
        try {
            em.getTransaction().begin();
            Frete frete = em.find(Frete.class, id);
            if (frete == null) {
                em.getTransaction().rollback();
                return false;
            }
            frete.setCep(cep);
            frete.setCidade(cidade);
            frete.setEstado(estado);
            frete.setPeso(peso);
            frete.setTransportadora(transportadora);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deletarFrete(Long id) {
        EntityManager em = emf().createEntityManager();
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
