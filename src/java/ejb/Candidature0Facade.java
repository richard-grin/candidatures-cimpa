/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entite.Candidature0;
import entite.Ecole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author richard
 */
@Stateless
public class Candidature0Facade extends AbstractFacade<Candidature0> {

  @PersistenceContext(unitName = "candidaturesPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public Candidature0Facade() {
    super(Candidature0.class);
  }

  /**
   * Retourne la liste de tous les noms des écoles.
   * @return
   */
  public List<String> findAllTitresEcoles() {
    Query query = em.createNamedQuery("Candidature0.findAllNomsEcoles");
    return (List<String>) query.getResultList();
    // Quand j'aurai le temps il faudra que j'essaie d'utiliser l'API query....
//        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
//        cq.select(cq.from(Candidature0.class));
//        return getEntityManager().createQuery(cq).getResultList();
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
   * Vider le cache de l'entity manager.
   */
  public void viderCache() {
//    System.out.println("em.clear()");
//    em.clear();
    // Vider aussi le cache de 2ème niveau
    em.getEntityManagerFactory().getCache().evictAll();
  }

  /**
   * Renvoie la liste des écoles visibles par le login
   * passé en paramètre.
   * @param login
   * @return
   */
  public List<Ecole> findEcolesVisibles(String login) {
//    Query query = em.createNamedQuery("Candidature0.findNomsEcolesVisiblesPar");
    Query query = em.createNamedQuery("Personne.findEcolesVisiblesPar");
    query.setParameter("login", login);
    List<Ecole> listeEcoles = (List<Ecole>) query.getResultList();
    return listeEcoles;
  }

  /**
   * Retourne la liste des candidats à une école.
   * @param codeEcole
   * @return
   */
  public List<Candidature0> findByCodeEcole(String codeEcole) {
    System.out.println("code école = " + codeEcole);
    Query query = em.createNamedQuery("Candidature0.findByCodeEcole");
    query.setParameter("codeEcole", codeEcole);
    return (List<Candidature0>) query.getResultList();
  }

  /**
   * Renvoie la liste des titres des écoles visibles par le login
   * passé en paramètre.
   * @param login
   * @return
   */
  public List<String> findTitresEcolesVisibles(String login) {
//    Query query = em.createNamedQuery("Candidature0.findNomsEcolesVisiblesPar");
    Query query = em.createNamedQuery("Personne.findEcolesVisiblesPar");
    query.setParameter("login", login);
    List<Ecole> listeEcoles = (List<Ecole>) query.getResultList();
    List<String> listeTitresEcole = new ArrayList<String>();
    for (Ecole ecole : listeEcoles) {
      listeTitresEcole.add(ecole.getCodeEcole() + ":" + ecole.getNomFrAbrege());
    }
    return listeTitresEcole;
  }

  /**
   * Relit les propriétés d'une candidature.
   * @param candidature la candidature qu'il faut relire.
   */
  public Candidature0 refreshCandidature(Candidature0 candidature) {
    Candidature0 candidature2 = em.merge(candidature);
    em.refresh(candidature2);
    return candidature2;
  }
  
  /**
   * Renvoie les candidatures à une autre école qu'une certaine école
   * passée en paramètre.
   * @param idEcole id de l'école
   * @return map dont une clé est l'identificateurs d'une candidature
   * à cette "certaine" école et dont la valeur est la liste des titres des
   * autres écoles concernées par les autres candidatures de la personne
   * concernée par la candidature identifiée par la clé.
   * Par exemple, la clé peut être 9870 et la valeur la liste des noms des
   * autres écoles auxquelles la personne qui a fait la candidature 9870
   * a candidaté, la même année.
   * Question : faut-il passer codeEcole ou idEcole ?? Réponse : actuellement
   * idEcole n'est pas initialisé dans la table des candidatures (!!).
   * C'est dû à des raisons historiques ; il faudrait un refactoring...
   * TODO
   */
  public Map<Integer,List<String>> findAutresCandidatures(Ecole ecole) {
    Query query = em.createNamedQuery("Candidature0.findAutresCandidatures");
    query.setParameter("codeEcole", ecole.getCodeEcole());
    List<Object[]> listeAutresCandidatures = 
            (List<Object[]>) query.getResultList();
    Map<Integer,List<String>> resultat = new HashMap<Integer, List<String>>();
    List<String> listeTitresEcoles = null;
    // Parcourt la liste et remplit la map
    int idCourant = -1;
    for (Object[] donnees : listeAutresCandidatures) {
      int id = (Integer)donnees[0];
      if (id != idCourant) {
        // changement de candidature
        // On commence par ranger l'entrée courante de la map
        if (idCourant != -1) {
          resultat.put(idCourant, listeTitresEcoles);
        }
        // Puis on crée la nouvelle liste des écoles pour la prochaine entrée
        listeTitresEcoles = new ArrayList<String>();
        // et on change l'id courant
        idCourant = id;
      }
      // On abrège pour que ça ne tienne pas trop de place sur l'écran
      // Attention aux noms très courts
      int tailleMaxiPourNom = 10;
      String nomEcole = (String)donnees[2];
      if (nomEcole.length() > tailleMaxiPourNom) {
        nomEcole = nomEcole.substring(0, tailleMaxiPourNom) + "...";
      }
      String descriptionEcole = donnees[1] + " - " 
              + nomEcole + ", "
              + donnees[3];
      listeTitresEcoles.add(descriptionEcole);
    } // for
    // Rangement du dernier id traité
    if (idCourant != -1) {
      resultat.put(idCourant, listeTitresEcoles);
    }
    return resultat;
  }
}
