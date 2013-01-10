package org.cimpa.util.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Exporte des données dans un fichier excel. Classe utilitaire pour faciliter
 * l'enregistrement de données dans une feuille Excel. Cette classe n'est qu'une
 * fine couche au-dessus d'une API Excel pour, par exemple, créer une feuille
 * Excel, enregistrer dans la feuille une ligne, écrire un titre et les en-têtes
 * de colonnes pour un tableau, enregistrer la feuille Excel dans un fichier.
 *
 * Une instance de cette classe correspond à un fichier Excel dans lequel on
 * peut insérer des tableaux.
 *
 * Les numéro de lignes et de colonnes commencent à 0.
 *
 * @author richard
 *
 */
public class ExportExcel {

  private static Logger logger = Logger.getLogger("org.cimpa.export.ExportExcel");
  private File fichierExcel;
  private HSSFWorkbook wb;
  private HSSFSheet feuille;
  /**
   * Style de cellule par défaut (pour titre de colonne ou autre)
   */
  private HSSFCellStyle cellStyle;
  // Nombre de colonnes
  private int numColonnes;
  // Style par défaut pour les valeurs;
  HSSFCellStyle styleParDefautTexte;
  HSSFCellStyle styleParDefautNombre;

  /**
   *
   * @param fichierExcel
   *          fichier où les données sont exportées
   * @param nomFeuille
   *          nom de la feuille du tableau Excel.
   */
  public ExportExcel(File fichierExcel, String nomFeuille) {
    this.fichierExcel = fichierExcel;
    wb = new HSSFWorkbook();
    feuille = wb.createSheet(nomFeuille);
  }

  public ExportExcel(File fichierExcel) {
    this(fichierExcel, "Sélection");
  }

  /**
   * Feuille Excel. Le fichier n'est pas précisé. Peut être utilisé pour envoyer
   * la feuille dans un flot plutôt que dans un fichier, en utilisant la méthode
   * envoyerDansFlot.
   *
   * @param nomFeuille
   *          nom de la feuille du tableau Excel.
   */
  public ExportExcel(String nomFeuille) {
    wb = new HSSFWorkbook();
    feuille = wb.createSheet(nomFeuille);
  }

  /**
   * Crée la feuille Excel et écrit son titre et sa date de création. Initialise
   * le style par défaut de la feuille.
   *
   * @param nomFeuille
   *          nom de la feuille excel. PAS UTILISE finalement !!!
   * @param titreFeuille
   *          titre de la feuille (écrit en haut de la feuille sur la 1ère
   *          ligne)
   */
  public void initialiseFeuilleExcel(String titreFeuille) {
    // Style de cellule par défaut
    cellStyle = wb.createCellStyle();
    cellStyle.setWrapText(true);
    // Titre en gros caractères (14) et en gras
    HSSFRow ligne = feuille.createRow(0);
    HSSFCell cell = ligne.createCell(0);
    HSSFFont font14 = wb.createFont();
    font14.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    font14.setFontHeightInPoints((short) 14);
    HSSFRichTextString titre2 = new HSSFRichTextString(titreFeuille);
    titre2.applyFont(font14);
    cell.setCellValue(titre2);
    // Date de la version de la feuille
    ligne = feuille.createRow(1);
    cell = ligne.createCell(0);
    HSSFCellStyle cellStyleDate = wb.createCellStyle();
    cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
    cell.setCellStyle(cellStyleDate);
    cell.setCellValue(new Date());
    // Le style des cellules qui contiennent des valeurs
    styleParDefautTexte = wb.createCellStyle();
    styleParDefautTexte.setWrapText(true);
    styleParDefautTexte.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    styleParDefautTexte.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    styleParDefautTexte.setBorderRight(HSSFCellStyle.BORDER_THIN);
    styleParDefautTexte.setBorderTop(HSSFCellStyle.BORDER_THIN);
    styleParDefautNombre = wb.createCellStyle();
    styleParDefautNombre.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    styleParDefautNombre.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    styleParDefautNombre.setBorderRight(HSSFCellStyle.BORDER_THIN);
    styleParDefautNombre.setBorderTop(HSSFCellStyle.BORDER_THIN);
  }

  /**
   * Initialise un tableau excel ; écrit son titre et les en-têtes des colonnes.
   *
   * @param titre
   *          titre du tableau (écrit juste avant le tableau)
   * @param titreColonnes
   *          titres des colonnes de valeurs
   * @param largeurColonnes
   *          largeur des colonnes excel
   * @param numeroLigne
   *          numéro de la ligne où le tableau commence à s'afficher.
   * @param freeze
   *          si vrai, il faut "freezer" les lignes des titres de ce tableau
   *          (restent affichées même si on se déplace loin vers le bas). Le
   *          plus souvent, on ne le met à vrai que pour le 1er tableau de la
   *          feuille.
   * @return numéro de la dernière ligne où les titres des colonnes ont été
   *         écrits.
   */
  public int initialiseTableauExcel(String titreTableau,
          String[] titreColonnes, int[] largeurColonnes, int numeroLigne,
          boolean freeze) {
    // int ligneFreeze = numeroLigne;
    if (titreTableau != null && !titreTableau.equals("")) {
      // Titre du tableau en gras et grands caractères
      HSSFRow ligneTitre = feuille.createRow(numeroLigne++);
      HSSFCell cell = ligneTitre.createCell(0);
      HSSFFont fontTitre = wb.createFont();
      fontTitre.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
      fontTitre.setFontHeightInPoints((short) 12);
      HSSFRichTextString titreTableauRich = new HSSFRichTextString(titreTableau);
      titreTableauRich.applyFont(fontTitre);
      cell.setCellValue(titreTableauRich);
      // Saute une ligne après le titre du tableau
      numeroLigne++;
      // ligneFreeze = numeroLigne;
    }
    // Titres des colonnes en gras
    int numColonne = 0;
    HSSFFont font = wb.createFont();
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    cellStyle.setWrapText(true);
    // Fixe la largeur des colonnes
    if (largeurColonnes != null) {
      for (int i = 0; i < largeurColonnes.length; i++) {
        System.out.println("Largeur colonne " + i + " = " + largeurColonnes[i]);
        feuille.setColumnWidth(i, largeurColonnes[i] * 256);
      }
    }
    // Ajoute les titres des colonnes
    if (freeze) {
      // Figer les volets : les 3 colonnes de gauche (1er paramètre) et les
      // titres des colonnes.
      // Le numéro de ligne commençant à 0, il faut ajouter 1 pour donner le
      // nombre de ligne à figer.
      feuille.createFreezePane(3, numeroLigne + 1);
    }
    HSSFRow ligneTitresColonnes = feuille.createRow(numeroLigne);
    for (String titreColonne : titreColonnes) {
      HSSFRichTextString titreColonne2 = new HSSFRichTextString(titreColonne);
      titreColonne2.applyFont(font);
      HSSFCell cell = ligneTitresColonnes.createCell(numColonne);
      cell.setCellStyle(cellStyle);
      cell.setCellValue(titreColonne2);
      numColonne++;
    }
    if (numColonne > numColonnes) {
      this.numColonnes = numColonne;
    }
    return numeroLigne;
  }

  /**
   * Initialise un tableau excel ; écrit son titre et les en-têtes des colonnes.
   * Fige des volets (ce qu'il faut pour les tableaux placés en premier).
   *
   * @param titre
   *          titre du tableau (écrit juste avant le tableau)
   * @param titreColonnes
   *          titres des colonnes de valeurs
   * @param largeurColonnes
   *          largeur des colonnes excel
   * @param numeroLigne
   *          numéro de la ligne où le tableau commence à s'afficher.
   * @return numéro de la dernière ligne où les titres des colonnes ont été
   *         écrits.
   */
  public int initialiseTableauExcel(String titreTableau,
          String[] titreColonnes, int[] largeursColonnes, int numeroLigne) {
    return initialiseTableauExcel(titreTableau, titreColonnes,
            largeursColonnes, numeroLigne, true);
  }

  /**
   * Crée le tableau excel et écrit son titre et les en-têtes des colonnes. Ne
   * donne pas la largeur des colonnes. TODO: préciser si les lignes et colonnes
   * commencent à 0 (plutôt non) et modifier le code en conséquence. Pour le
   * moment c'est n'importe quoi !!!
   *
   * @param titre
   *          titre du tableau (écrit juste avant le tableau)
   * @param titreColonnes
   *          titres des colonnes de valeurs
   * @param numeroLigne
   *          numéro de la ligne où les titres des colonnes seront mis.
   * @return numéro de la dernière ligne où les titres des colonnes ont été
   *         écrits.
   */
  public int initialiseTableauExcel(String titreTableau,
          String[] titreColonnes, int numeroLigne) {
    return initialiseTableauExcel(titreTableau, titreColonnes, null,
            numeroLigne, true);
  }

  /**
   * Ajouter une ligne au fichier Excel.
   *
   * @param valeurs
   *          valeurs des colonnes à mettre dans la ligne.
   * @param numeroLigne
   *          numéro de la ligne.
   * @param typesColonnes
   *          types des colonnes (soit String, soit Numeric, soit Formula).
   */
  public void remplirLigne(String[] valeurs, int numeroLigne,
          String[] typesColonnes) {
    HSSFRow ligne = feuille.createRow(numeroLigne);
    int numColonne = 0;
    HSSFCell cell;
    for (int i = 0; i < valeurs.length; i++) {
      String valeur = valeurs[i];
      cell = ligne.createCell(numColonne);
      // cell.setCellValue(valeurCellule);
      // System.out.println("type colonne = " + typesColonnes[i]);
      if (typesColonnes[i].equals("String")) {
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellStyle(styleParDefautTexte);
        HSSFRichTextString valeurCellule = new HSSFRichTextString(valeur);
        cell.setCellValue(valeurCellule);
      }
      else if (typesColonnes[i].equals("Numeric")) {
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(styleParDefautNombre);
        try {
          int valeurCellule = Integer.parseInt(valeur);
          cell.setCellValue(valeurCellule);
        } catch (NumberFormatException e) {
          // TODO: faire ça d'une façon un peu plus élégante !!
          // si ça n'est pas un entier, c'est un double....
          double valeurCellule = Double.parseDouble(valeur);
          cell.setCellValue(valeurCellule);
        }
      }
      else if (typesColonnes[i].equals("Formule")) {
        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        cell.setCellFormula(valeur);
        // Faut-il lancer l'évaluation de la formule ?
      }
      else {
        String message = "Type de cellule " + typesColonnes[i]
                + " inconnu pour " + fichierExcel + " !";
        logger.severe(message);
      }
      numColonne++;
    }
    if (numColonne > numColonnes) {
      this.numColonnes = numColonne;
    }
  }

  /**
   * Exporte une ligne ne contenant que des valeurs de type String.
   *
   * @param valeurs
   * @param numeroLigne
   */
  public void remplirLigne(String[] valeurs, int numeroLigne) {
    // Met tous les types à String
    String[] types = new String[valeurs.length];
    Arrays.fill(types, "String");
    remplirLigne(valeurs, numeroLigne, types);
  }

  /**
   * Exporte les données décrites dans une String vers un fichier excel. Toutes
   * les données vont être exportées sur une seule ligne.
   *
   * @param valeurs
   *          contient les données à exporter.
   * @param ligne
   *          numéro de la ligne où seront placées les valeurs.
   */
  // public void exporteUneLigne(String[] valeurs, int numeroLigne) {
  // HSSFRow ligne = feuille.createRow(numeroLigne);
  // }
  /**
   * Enregistre le tableau dans le fichier excel. A chaque fois le fichier est
   * ouvert et fermé après l'écriture. TODO: on peut sans doute éviter ces
   * ouvertures/fermetures à chaque fois, mais est-ce que vaut la peine ?
   *
   * @throws IOException
   */
  public void enregistreFichierExcel() throws IOException {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(fichierExcel);
      envoyerDansFlot(out, true);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

//  public void envoyerDansFlot(ByteArrayOutputStream out, boolean autosize) {
//    // Taille de la 1ère colonne (pour 20 caractères)
//    // feuille.setColumnWidth((short)0, (short)(20 * 256));
//    // Adapte la largeur des colonnes à ce qu'elles contiennent
//    // On commence à 1 car sinon le titre de la feuille qui est
//    // dans la 1ère colonne (numéro 0) va fausser la taille.
//    for (short i = 1; i < numColonnes; i++) {
//      feuille.autoSizeColumn(i);
//
//    }
//    wb.write(out);
//  }


  /**
   * Envoie la feuille dans un flot binaire.
   * @param out Flot dans lequel la feuille est envoyée
   * @param autosize vrai si adaptation des largeurs des colonnes
   * à ce qu'elles contiennent
   * @throws IOException
   */
  public void envoyerDansFlot(OutputStream out, boolean autosize) throws IOException {
    // Taille de la 1ère colonne (pour 20 caractères)
    // feuille.setColumnWidth((short)0, (short)(20 * 256));
    // Adapte la largeur des colonnes à ce qu'elles contiennent
    // On commence à 1 car sinon le titre de la feuille qui est
    // dans la 1ère colonne (numéro 0) va fausser la taille.
    for (short i = 1; i < numColonnes; i++) {
      feuille.autoSizeColumn(i);

    }
    wb.write(out);
  }

  /**
   * @return the fichierExcel
   */
  public File getFichierExcel() {
    return fichierExcel;
  }

  /**
   * @param fichierExcel
   *          the fichierExcel to set
   */
  public void setFichierExcel(File fichierExcel) {
    this.fichierExcel = fichierExcel;
  }

  /**
   * Ajouter une formule dans la feuille Excel.
   *
   * @param numeroLigne
   *          numéro de la ligne
   * @param numeroColonne
   *          numéro de la colonne
   * @param formule
   *          formule à ajouter.
   */
  public void ajouterFormule(int numeroLigne, int numeroColonne, String formule) {
    HSSFRow ligne = feuille.createRow(numeroLigne);
    HSSFCell cell = ligne.createCell(numeroColonne);
    cell.setCellFormula(formule);
  }

  /**
   * Ajouter une cellule de texte.
   *
   * @param numeroLigne
   * @param numeroColonne
   * @param string
   */
  public void ajouterTexte(int numeroLigne, int numeroColonne, String valeur) {
    HSSFRow ligne = feuille.createRow(numeroLigne);
    HSSFCell cell = ligne.createCell(numeroColonne);
    HSSFRichTextString valeurRich = new HSSFRichTextString(valeur);
    cell.setCellValue(valeurRich);
  }

  /**
   * Ajoute les titres des colonnes dans la feuille.
   * @param titresColonnes
   */
  public void ajouterTitresColonnes(String[] titresColonnes) {

    int numeroLigne = 2;
    // TODO: Mettre en gras
    HSSFFont font = wb.createFont();
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    cellStyle.setWrapText(true);

    HSSFRow ligneTitresColonnes = feuille.createRow(numeroLigne);
    int numColonne = 0;
    for (String titreColonne : titresColonnes) {
      HSSFRichTextString titreColonne2 = new HSSFRichTextString(titreColonne);
      titreColonne2.applyFont(font);
      HSSFCell cell = ligneTitresColonnes.createCell(numColonne);
      cell.setCellStyle(cellStyle);
      cell.setCellValue(titreColonne2);
      numColonne++;
    }
  }
}
