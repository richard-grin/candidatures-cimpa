/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb;

import entite.Ecole;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 *
 * @author richard
 */
@Stateless
public class EcoleFacade extends AbstractFacade<Ecole> {
  @PersistenceContext(unitName = "candidaturesPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public EcoleFacade() {
    super(Ecole.class);
  }

    /**
     * Retourne la liste de toutes les écoles dont on peut consulter
     * les candidatures.
     * @return
     */
    public List<Ecole> findAllEcolesConsultables() {
        Query query = em.createNamedQuery("Ecole.findAllEcolesConsultables");
        return (List<Ecole>) query.getResultList();
    }

    /**
     * Redéfinit pour avoir un certain ordre :
     * ordre inverse des années (pour avoir les dernière écoles d'abord)
     * et ordre croissant des code dans l'année.
     * @param range
     * @return
     */
    @Override
    public List<Ecole> findRange(int[] range) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<Ecole> ecole = cq.from(Ecole.class);
        cq.select(ecole);
        // Ajoute ordre inverse
        cq.orderBy(cb.desc(ecole.get("anCimpa")), cb.asc(ecole.get("anCimpa")));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

}
