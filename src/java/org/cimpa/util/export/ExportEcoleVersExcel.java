package org.cimpa.util.export;

import entite.Candidature0;
import entite.Ecole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.DataModel;
import jsf.Candidature0Controller;

/**
 * Exporte les données d'une école d'une datatable
 * vers Excel.
 * @author richard
 */
public class ExportEcoleVersExcel {
  private static final String PREFIXE_TABLEUR = "tableur_";
//  private String titreEcole;
  private Ecole ecole;
  private ResourceBundle bundle;
  private DataModel items;

//  /**
//   *
//   * @param titreEcole titre de l'école à exporter
//   * @param items données de la datatable
//   * @param bundle pour la traduction dans la locale
//   */
//  public ExportEcoleVersExcel(String titreEcole, DataModel items, ResourceBundle bundle) {
//    this.titreEcole = titreEcole;
//    this.items = items;
//    this.bundle = bundle;
//  }
  /**
   *
   * @param titreEcole titre de l'école à exporter
   * @param items données de la datatable
   * @param bundle pour la traduction dans la locale
   */
  public ExportEcoleVersExcel(Ecole ecole, DataModel items, ResourceBundle bundle) {
    this.ecole = ecole;
    this.items = items;
    this.bundle = bundle;
  }

  /**
   * Renvoie les données Excel à renvoyer sur l'école dont le titre est dans
   * la variable d'instance titreEcole.
   * @return tableau d'octets qui contient le document Excel.
   */
  public byte[] getExcelData() {
//    System.out.println("bunble = " + bundle + "; titreEcole = " + titreEcole);
    // Utilise la classe ExportExcel pour créer un document Excel
    String titreEcole =
            ecole.getCodeEcole() + " " + ecole.getNomEn().replaceAll(":", "") + " - " + ecole.getPaysEn();
    ExportExcel exportExcel =
            new ExportExcel(titreEcole);
//    new ExportExcel(bundle.getString("tableur_titre") + " " + titreEcole);
//            + extractCodeEcole(titreEcole));
//    System.out.println("++++ Juste après création exportExcel");
    exportExcel.initialiseFeuilleExcel(bundle.getString("tableur_titre") + " " + titreEcole.replaceAll(":", ""));

    // TODO: Figer les 2 premières colonnes et les 2 premières lignes
    // et écrire la date en 2ème colonne

    // Clés des noms des colonnes dans le bundle
    String[] titresColonnes = {
      "id", "nom", "prenoms", "sexe", "email", "dateNaissance",
      "nationalite", "fonction", "adresseInstitution", "paysInstitution",
      "domaineRecherche", "demandeAide",
      "diplome", "diplomePrepare",
      "montantDemandeVoyage", "fraisInscriptionPeutPayer",
      "typeDemandeSejour", "montantDejaEuPourSejour",
      "typeDispenseFraisInscription", "fraisInscriptionAPayer",
      "montantAideVoyage", "typeAideSejour"
    };
    traduire(titresColonnes, bundle);

    exportExcel.ajouterTitresColonnes(titresColonnes);
    // TODO: Ecrit les données sur l'école en parcourant items
    int numeroLigne = 3;
    for (Object o : items) {
      Candidature0 candidature = (Candidature0) o;
      // Met les valeurs
      // Tout d'abord quelques valeurs particulières :
      // Oui à la place de o par exemple.
      String demandeAide =
              bundle.getString(candidature.getDemandeAide().toString());
      String typeDemandeSejour =
              bundle.getString(candidature.getTypeDemandeSejour().toString());
      String peutPayer = candidature.getFraisInscriptionPeutPayer();
      if (peutPayer.equals("Peut payer")) {
        peutPayer = bundle.getString("peutPayer");
      }
      String[] valeurs = {
        candidature.getId().toString(), // 1
        candidature.getNom(),
        candidature.getPrenoms(),
        candidature.getSexe().toString(),
        candidature.getEmail(), // 5
        candidature.getDateNaissance().toString(),
        candidature.getNationalite(),
        candidature.getFonction(),
        candidature.getAdresseInstitution(),
        candidature.getPaysInstitution(),
        candidature.getDomaineRecherche(),
        demandeAide, // 10
        candidature.getDiplomeAcquisNom(),
        candidature.getDiplomePrepareNom(),
        candidature.getMontantDemandeVoyage(),
        peutPayer,
        typeDemandeSejour, // 15
        candidature.getMontantDejaEuPourSejour() // 16
      };
      String[] typesColonnes = {
        "Numeric",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String",
        "String"
      };
      exportExcel.remplirLigne(valeurs, numeroLigne++, typesColonnes);
    }

//    System.out.println("++++ Juste avant envoi dans flot");
    // Le flot vers lequel le document Excel va être envoyé
    ByteArrayOutputStream out = null;
    try {
      out = new ByteArrayOutputStream();
      exportExcel.envoyerDansFlot(out, false);
      return out.toByteArray();
    } catch (IOException ex) {
      Logger.getLogger(Candidature0Controller.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException ex) {
        Logger.getLogger(Candidature0Controller.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Méthode utilitaire pour traduire dans la bonne locale
   * un tableau de textes.
   * @param string
   * @return
   */
  private void traduire(String[] tab, ResourceBundle bundle) {
    for (int i = 0; i < tab.length; i++) {
      tab[i] = bundle.getString(PREFIXE_TABLEUR + tab[i]);
    }
//    System.out.println("Tableau traduit :" + Arrays.toString(tab));
  }

//  private String extractCodeEcole(String titreEcole) {
//    int positionSeparateur = titreEcole.indexOf(":");
//    return titreEcole.substring(0, positionSeparateur);
//  }
}
