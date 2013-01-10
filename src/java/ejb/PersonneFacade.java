/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb;

import entite.Ecole;
import entite.Personne;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author richard
 */
@Stateless
public class PersonneFacade extends AbstractFacade<Personne> {
  @PersistenceContext(unitName = "candidaturesPU")
  private EntityManager em;

  protected EntityManager getEntityManager() {
    return em;
  }

  public PersonneFacade() {
    super(Personne.class);
  }

      /**
     * Méthode qui insère une nouvelle personne.
     * Elle sert à insérer une ligne
     * dans la table groupe pour mettre la personne
     * dans le groupe "responsable_ecole"
     * et aussi à indiquer quelles écoles la personne
     * peut consulter.
     * @param personne
     */
    @Override
    public void create(Personne personne) {
        // Il faut faire un merge des écoles de la personne
        List<Ecole> ecoles = personne.getEcolesConsultables();
//        System.out.println("********Les " + ecoles.size() + " écoles de " + personne.getLogin());
        for (int i = 0; i < ecoles.size(); i++) {
            Ecole ecole = ecoles.get(i);
//            System.out.println(ecole);
            /*
             * Si l'école n'est pas gérée par
             * l'entity manager (em), on doit faire un merge et remplacer
             * l'ancienne école par la nouvelle renvoyée par le merge.
             * Attention, merge renvoie un objet différent de l'objet
             * passée en paramètre si celui-ci n'est pas gérée par l'em.
             */
            if (!em.contains(ecole)) {
                ecoles.set(i, em.merge(ecole));
            }
//            System.out.println(" contenue dans em ? " + em.contains(ecole));
        }
//        System.out.println("********");

        // Commence par ajouter la ligne dans la table personne
        super.create(personne);
        // Ajout d'une ligne dans la table groupe
        // (personne.login, "responsable_ecole")
        // avec une requête native (pas de mapping possible en JPA).
        String requete =
                "insert into groupe (nom_groupe, login) values("
                + "'responsable_ecole', '" + personne.getLogin() + "')";
//        System.out.println("requete native : " + requete);
        Query query = em.createNativeQuery(requete);
        int i = query.executeUpdate();
//        System.out.println("Nombre de lignes insérées : " + i);
    }

}
