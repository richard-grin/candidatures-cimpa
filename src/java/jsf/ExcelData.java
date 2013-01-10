package jsf;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.inject.Inject;
import org.cimpa.util.export.ExportExcel;

/**
 * Classe PAS UTILISEE ! utilitaire pour envoyer un document Excel dans un flot.
 * Utilisé pour envoyer un document Excel dans le flot de données
 * envoyé au client Web.
 * @author richard
 */
public class ExcelData implements BinaryData {
  /**
   * Le préfixe dans le fichier Bundle
   */
  private static final String PREFIXE_TABLEUR = "tableur_";
  private static final String BASE_CHEMIN_BUNDLE = "/Bundle";

  @Inject
  Candidature0Controller candidature0Controller;

  @Override
  public boolean write(OutputStream out) throws Exception {
    ResourceBundle bundle = ResourceBundle.getBundle(BASE_CHEMIN_BUNDLE);
    System.out.println("++++++++ Début méthode write");
    // Récupère le code de l'école dans la session
    // Provoque une erreur
    // java.lang.IllegalStateException: PWC3991: getOutputStream() has already been called for this response
    // Je n'ai pas compris pourquoi !!!
//    String titreEcole = candidature0Controller.getSelected().getTitreEcole();
    String titreEcole = "11-E04:Tunisie";
    System.out.println("Juste après appel controlleur");
    // On ne fait rien si l'école n'a pas déjà été choisie
    // Remarque : il faudra mettre un rendered sur le composant
    // pour qu'il ne s'affiche que si l'école a été choisie.
    System.out.println("++++ Juste avant if");
//    if (titreEcole == null || titreEcole.equals("")) {
//      return false;
//    }
    System.out.println("++++ Juste après if");
    // L'école a bien été choisie
    // Commencer par extraire le code de l'école
    // (format 11-E12: Titre école).
//    int positionSeparateur = titreEcole.indexOf(":");
//    String codeEcole = titreEcole.substring(0, positionSeparateur);

//    List<Candidature0> listeCandidats =
//            candidature0Controller.getFacade().findByCodeEcole(codeEcole);
    System.out.println("++++ Juste avant création exportExcel");
    // Exportation des candidatures vers le flot Excel
//    ExportExcel exportExcel = new ExportExcel("Sélection école " + titreEcole);
    ExportExcel exportExcel = new ExportExcel("Selection ecole ");
    System.out.println("++++ Juste après création exportExcel");
    exportExcel.initialiseFeuilleExcel(bundle.getString("tableur_titre") + " " + titreEcole);
    // Ecrit les titres des colonnes
    String[] titresColonnes = {
      "id", "nom", "prenoms", "sexe", "email", "dateNaissance",
      "nationalite", "fonction", "domaineRecherche", "demandeAide",
      "diplome", "diplomePrepare",
      "montantDemandeVoyage", "fraisInscriptionPeutPayer",
      "typeDemandeSejour", "montantDejaEuPourSejour",
      "typeDispenseFraisInscription", "fraisInscriptionAPayer",
      "montantAideVoyage"
    };
    traduire(titresColonnes, bundle);

    exportExcel.ajouterTitresColonnes(titresColonnes);
    System.out.println("++++ Juste avant envoi dans flot");
    exportExcel.envoyerDansFlot(out, false);
    System.out.println("++++++++++++ Fin ajout des données");
    return true;
  }

  @Override
  public String getContentType() {
    return "application/vnd.ms-excel";
  }

  /**
   * Méthode utilitaire pour traduire dans la bonne locale
   * un tableau de textes.
   * @param string
   * @return
   */
  private void traduire(String[] tab, ResourceBundle bundle) {
    for (int i=0; i < tab.length; i++) {
      tab[i] = bundle.getString(PREFIXE_TABLEUR + tab[i]);
    }
//    System.out.println("Tableau traduit :" + Arrays.toString(tab));
  }
}
