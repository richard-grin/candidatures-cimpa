/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package personne;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * Test requêtes nommmées.
 * @author richard
 */
public class TestPersonne {

  public static void main(String[] args) {
    EntityManagerFactory emf = null;
    EntityManager em = null;
    try {
     emf = Persistence.createEntityManagerFactory("candidaturesPU2");
     em = emf.createEntityManager();
    Query q = em.createNamedQuery("Personne.findEcolesVisibles");
    q.setParameter("login", "toto");
    List<String> nomsEcoles = q.getResultList();
    for (String nomEcole : nomsEcoles) {
      System.out.println(nomEcole);
    }
    }
    finally {
      if (emf != null) {
        emf.close();
        if (em != null) {
          em.close();
        }
      }
    }

  }

}
