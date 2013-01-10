package jsf;

import ejb.Candidature0Facade;
import entite.Candidature0;
import entite.Ecole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import org.cimpa.util.export.ExportEcoleVersExcel;
import org.cimpa.util.export.ExportExcel;

@Named("candidature0Controller")
@SessionScoped
public class Candidature0Controller implements Serializable {

    /**
     * Désigne la ligne courante. La valeur est à null sauf si on le met
     * explicitement, par exemple quand on veut faire afficher les détails d'une
     * candidature.
     */
    private Candidature0 current;
    private DataModel items = null; // new ListDataModel<Candidature0>(new ArrayList());
    @EJB
    private ejb.Candidature0Facade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    // Pour le choix dans la liste des écoles
    // Il vaudrait mieux que ce soit un instance de Ecole
    // en modifiant la valeur pour un choix dans la liste
    // (voir nouvelles possibilités de <f:selectItems> en JSF 2.0).
    private String titreEcole;
    /**
     * Ecole choisie par l'utilisateur (pour voir ses candidats).
     */
    private Ecole ecole;
    /**
     * Candidats de l'école courante qui ont candidaté à une autre école. La clé
     * est l'id du candidat et la valeur est la liste des titres des écoles
     * auxquelles le candidat a candidaté (autres que l'école en cours
     * représenté par la variable d'instance ecole).
     */
    private Map<Integer, List<String>> autresCandidatures;
//  private boolean nouvelleEcole = false;
    /**
     * Bundle en cours pour les traductions. Problème : il semble que
     * ResourceBundle n'est pas sérialisable !! A VOIR !!
     */
    private ResourceBundle bundle;
    private static final String BASE_CHEMIN_BUNDLE = "/Bundle";
    private static final String PREFIXE_TABLEUR = "tableur_";
    private boolean listeChoixEcoleVisible = true;

    /**
     * Retourne l'école choisie par l'utilisateur.
     *
     * @return
     */
    public Ecole getEcole() {
        return ecole;
    }

    public void setEcole(Ecole ecole) {
        this.ecole = ecole;
    }

    public String getTitreEcole() {
//    System.out.println("***********Titre ecole : " + titreEcole + "***");
        return titreEcole;
    }

    public void setTitreEcole(String titreEcole) {
        this.titreEcole = titreEcole;
    }

    public Candidature0Controller() {
        System.out.println("Création candidature0controller");
    }

    public Candidature0 getSelected() {
        if (current == null) {
            current = new Candidature0();
            selectedItemIndex = -1;
        }
        return current;
    }

    public Candidature0Facade getFacade() {
        return ejbFacade;
    }

    /**
     * Renvoie la liste des noms des écoles. Si l'utilisateur est un
     * administrateur ou un RSR, c'est toutes les écoles qui sont en ce moment
     * consultables, sinon, c'est seulement les écoles qu'il peut voir.
     *
     * @return
     */
    public List<Ecole> getEcoles() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String loginUser = request.getRemoteUser();
        // Récupère le nom de login de l'utilisateur
        if (request.isUserInRole("admin") || request.isUserInRole("rsr")) {
            // L'administrateur et les RSR peuvent voir toutes les écoles
            return ((Candidature0Facade) getFacade()).findAllEcolesConsultables();
        } else {
            // Les responsables d'école peuvent voir leur(s) école(s) seulement.
            List<Ecole> listeEcoles =
                    ((Candidature0Facade) getFacade()).findEcolesVisibles(loginUser);
            if (listeEcoles.size() == 1) {
                // Une seule école ; pas besoin d'afficher la liste pour choisir
                ecole = listeEcoles.get(0);
                listeChoixEcoleVisible = false;
            }
            return listeEcoles;
        }
    }

    /**
     * Renvoie les titres des autres écoles où le candidat courant a candidaté.
     *
     * @return
     */
    public List<String> getAutresEcoles() {
        current = (Candidature0) getItems().getRowData();
        System.out.println("current = " + current);
        System.out.println("autresCandidatures = " + autresCandidatures);
        if (autresCandidatures != null && current != null) {
            return autresCandidatures.get(getSelected().getId());
        } else {
            return null;
        }
    }

    /**
     * Retourne vrai ssi il faut afficher la liste pour choisir les écoles.
     *
     * @return
     */
    public boolean isListeChoixEcoleVisible() {
        return listeChoixEcoleVisible;
    }

    /**
     * Relire les candidats dans la base. Pour cela il faut mettre idtems à null
     * et vider le cache de l'entity manager.
     *
     * @return
     */
    public String relire() {
//    System.out.println("*********Vidage du cache");
        getFacade().viderCache();
        items = null;
        autresCandidatures = null;
        return null;
    }

    /**
     * Retourne le bundle qui correspond à la locale actuelle.
     *
     * @return
     */
    private ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BASE_CHEMIN_BUNDLE);
        }
        return bundle;
    }

    /**
     * Changer la locale pour la page en cours. Peut servir à tester
     * l'application avec une certaine locale.
     *
     * @param locale la locale imposée.
     */
    private void changeLocale(Locale locale) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot().setLocale(locale);
    }

    public String prepareList() {
//    changeLocale(Locale.ENGLISH);
        recreateModel();
        return "List";
    }

    public String prepareView() {
//      ClassLoader cl1 = getItems().getRowData().getClass().getClassLoader();
//      ClassLoader cl2 = Candidature0.class.getClassLoader();
//      System.out.println("classloader de getrowdata : " + cl1);
//      System.out.println("classloader de Candidature0 : " + cl2);
//      System.out.println("2 classloaders égaux ? " + (cl1 == cl2));
        // Pour tester
//    changeLocale(Locale.ENGLISH);

        current = (Candidature0) getItems().getRowData();
//        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Candidature0();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("Candidature0Created"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    /**
     * Retourne les candidats à une école sous la forme d'un DataModel pour être
     * utilisé par une datatable.
     *
     * @return
     */
    public DataModel getItems() {
//    System.out.println("Appel getItems");
//    System.out.println("Valeur de items =" + items);
//    System.out.println("Valeur de titreEcole=" + titreEcole);

        /*
         * Code avec sélection d'une école
         */
        if (items != null) {
            return items;
        }
        // items est null. 2 cas possibles :
        //   - école déjà choisie ;
        //   - école pas choisie.
        if (ecole != null) {
            // Commencer par extraire le code de l'école
            // (format 11-E12: Titre école).
            String codeEcole = ecole.getCodeEcole();
//      System.out.println("====Code école = " + codeEcole);
            items = new ListDataModel(getFacade().findByCodeEcole(codeEcole));
            // Récupère aussi les candidatures multiples
            autresCandidatures = getAutresCandidatures();
        }

//    if (items == null && titreEcole != null) {
//      if (titreEcole.equals("")) {
//        items = new ListDataModel(getFacade().findAll());
//      }
//      else {
//        // Commencer par extraire le code de l'école
//        // (format 11-E12: Titre école).
//        int positionSeparateur = titreEcole.indexOf(":");
//        String codeEcole = titreEcole.substring(0, positionSeparateur);
//        items = new ListDataModel(getFacade().findByCodeEcole(codeEcole));
//      }
//    }
//    System.out.println("Valeur de items à la fin="
//            + items);

//    if (items != null) {
//      System.out.println("Taille de items=" + items.getRowCount());
//    }
//            items = new ListDataModel(getFacade().findAll());
//            items = getPagination().createPageDataModel();
        return items;
    }

    private Map<Integer, List<String>> getAutresCandidatures() {
        if (autresCandidatures != null) {
            return autresCandidatures;
        }
        return ejbFacade.findAutresCandidatures(ecole);

    }

    private String extractCodeEcole(String titreEcole) {
        int positionSeparateur = titreEcole.indexOf(":");
        return titreEcole.substring(0, positionSeparateur);
    }

    private void recreateModel() {
        items = null;
        autresCandidatures = null;
    }

    public String prepareEdit() {
        current = (Candidature0) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("Candidature0Updated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Candidature0) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("Candidature0Deleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    /**
     * Provoque une nouvelle recherche des candidats de l'école choisie. Utile
     * si une nouvelle école vient d'être choisie.
     *
     * @param event
     */
    public void changementEcole(ValueChangeEvent event) {
//    System.out.println("=====Appel de changementEcole");
        // items sera recalculé dans getItems, avec la nouvelle école choisie
        items = null;
        autresCandidatures = null;
    }

    /**
     * Exportation Excel. Version vraiment utilisée par List.xhtml (les autres
     * méthodes ne sont plus utilisées pour l'exportation vers Excel).
     *
     * @param event
     */
    public void exporterExcel3(ActionEvent event) {
//    System.out.println("++++++++Début de exporterExcel3 !!!");
        // Remarque : le titre de l'école est forcément donné, sinon le bouton pour
        // exporter n'aurait pas été affiché.
        HttpServletResponse response =
                (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        OutputStream out = null;

        ExportEcoleVersExcel exporteur = new ExportEcoleVersExcel(ecole, items, getBundle());
        try {
//      byte[] excelData = getExcelData();
            // Récupère les octets de la feuille Excel
            byte[] excelData = exporteur.getExcelData();
//      System.out.println("*******Taille document Excel : " + excelData.length);
            // Envoie ces octets vers le client
            response.setContentType("application/vnd.ms-excel");
            response.setContentLength(excelData.length);

            response.setHeader("Content-Disposition",
                    "attachment; filename=\"candidatures_" + ecole.getCodeEcole() + ".xls\"");

            // Envoi le document Excel vers le client
            out = response.getOutputStream();
            out.write(excelData);
            out.flush();

            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(Candidature0Controller.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    /**
     * Renvoie les données Excel à renvoyer sur l'école dont le titre est dans
     * la variable d'instance titreEcole. Plus utilisé car délégué à la classe
     * ExportEcoleVersExcel.
     *
     * @return tableau d'octets qui contient le document Excel.
     */
    private byte[] getExcelData() {
//    String titreEcole = "11-E04:Tunisie";
//    System.out.println("Juste après appel controlleur");
        // On ne fait rien si l'école n'a pas déjà été choisie
        // Remarque : il faudra mettre un rendered sur le composant
        // pour qu'il ne s'affiche que si l'école a été choisie.
//    System.out.println("++++ Juste avant if");
//    if (titreEcole == null || titreEcole.equals("")) {
//      return false;
//    }
//    System.out.println("++++ Juste après if");
        // L'école a bien été choisie
        // Commencer par extraire le code de l'école
        // (format 11-E12: Titre école).
//    int positionSeparateur = titreEcole.indexOf(":");
//    String codeEcole = titreEcole.substring(0, positionSeparateur);

//    List<Candidature0> listeCandidats =
//            candidature0Controller.getFacade().findByCodeEcole(codeEcole);
//    System.out.println("++++ Juste avant création exportExcel");
        // Exportation des candidatures vers le flot Excel
//    ExportExcel exportExcel = new ExportExcel("Sélection école " + titreEcole);

        // Utilise la classe ExportExcel pour créer un document Excel
        ExportExcel exportExcel =
                new ExportExcel(getBundle().getString("tableur_titre") + " "
                + extractCodeEcole(titreEcole));
//    System.out.println("++++ Juste après création exportExcel");
        exportExcel.initialiseFeuilleExcel("Données pour sélection candidats de l'école " + titreEcole);

        // TODO: Figer les 2 premières colonnes et les 2 premières lignes
        // et écrire la date en 2ème colonne


        // Ecrit les titres des colonnes
//    String[] titresColonnes = {
//      "id", "nom", "prénoms", "sexe", "email", "date de naissance",
//      "nationalité", "fonction", "domaine recherche", "demande aide",
//      "diplôme", "diplôme préparé",
//      "montant demande voyage", "frais inscription peut payer",
//      "type demande séjour", "montant déjà eu pour séjour",
//      "type dispense frais inscription", "frais inscription à payer",
//      "montant aide voyage"
//    };
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
        // TODO: Ecrit les données sur l'école en parcourant items
        int numeroLigne = 3;
        for (Object o : items) {
            Candidature0 candidature = (Candidature0) o;
            // Met les valeurs
            String[] valeurs = {
                candidature.getId().toString(), // 1
                candidature.getNom(),
                candidature.getPrenoms(),
                candidature.getSexe().toString(),
                candidature.getEmail(), // 5
                candidature.getDateNaissance().toString(),
                candidature.getNationalite(),
                candidature.getFonction(),
                candidature.getDomaineRecherche(),
                candidature.getDemandeAide().toString(), // 10
                candidature.getDiplomeAcquisNom(),
                candidature.getDiplomePrepareNom(),
                candidature.getMontantDemandeVoyage(),
                candidature.getFraisInscriptionPeutPayer(),
                candidature.getTypeDemandeSejour().toString(), // 15
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
     * Méthode utilitaire pour traduire dans la bonne locale un tableau de
     * textes.
     *
     * @param string
     * @return
     */
    private void traduire(String[] tab, ResourceBundle bundle) {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = bundle.getString(PREFIXE_TABLEUR + tab[i]);
        }
//    System.out.println("Tableau traduit :" + Arrays.toString(tab));
    }

    /**
     * Pour voir les CV en PDF.
     *
     * @param event
     */
    public void voirCv(ActionEvent event) {
//    System.out.println("******* ENTREE dans voirCv*********");
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//      System.out.println("response =" + response);
            OutputStream out = null;
            Candidature0 candidature = this.getSelected();
            // Récupère le flot
            byte[] tCv = null;
            tCv = candidature.getCvPdf();
//      System.out.println("Valeur de t : " + tCv);
            if (tCv == null || tCv.length == 0) {
                // Cas où le paramètre n'a pas une bonne valeur
                // ou la propriété "PDF" n'a pas de valeur pour cette candidature
                return;
            }
            out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setContentLength(tCv.length);
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"cv" + candidature.getNom() + ".pdf\"");
            out.write(tCv);
            out.flush();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(Candidature0Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Pour voir les lettres de recommandation en PDF.
     *
     * @param event
     */
    public void voirLettresRecommandation(ActionEvent event) {
        try {
            HttpServletResponse response =
                    (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            OutputStream out = null;
            Candidature0 candidature = this.getSelected();
            // Récupère le flot
            byte[] tLettresRecommandation = null;
            tLettresRecommandation = candidature.getLettresRecommandationPdf();
//      System.out.println("Valeur de t : " + tLettresRecommandation);
            if (tLettresRecommandation == null || tLettresRecommandation.length == 0) {
                // Cas où le paramètre n'a pas une bonne valeur
                // ou la propriété "PDF" n'a pas de valeur pour cette candidature
                return;
            }
            out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setContentLength(tLettresRecommandation.length);
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"recommandation" + candidature.getNom() + ".pdf\"");
            out.write(tLettresRecommandation);
            out.flush();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(Candidature0Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshCandidature(Candidature0 candidature) {
        candidature = getFacade().refreshCandidature(candidature);
    }

    /**
     * Fait sortir de la session. L'utilisateur n'est plus enregistré.
     *
     * @throws IOException
     */
    public void logout() throws IOException {
        try {
            ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
        } catch (ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

//  /**
//   * Exporte la liste des candidats vers un fichier Excel
//   */
//  public void exporterExcel() {
//    // TODO: trop de métier ici ; ajouter une classe en dehors
//
//    // Récupération de toutes les candidatures sous la forme d'une liste
//    // - récupération du titre de l'école
//    // Si le titre n'est pas mis, ne rien faire
//    if (titreEcole == null || titreEcole.equals("")) {
//      return;
//    }
//    // Commencer par extraire le code de l'école
//    // (format 11-E12: Titre école).
//    int positionSeparateur = titreEcole.indexOf(":");
//    String codeEcole = titreEcole.substring(0, positionSeparateur);
//
//    List<Candidature0> listeCandidats = getFacade().findByCodeEcole(codeEcole);
//
//    // Exportation des candidatures vers le flot Excel
//    ExportExcel exportExcel = new ExportExcel("Sélection école " + codeEcole);
//    exportExcel.initialiseFeuilleExcel("Données pour sélection candidats de l'école " + titreEcole);
//    // Ecrit les titres des colonnes
//    String[] titresColonnes = {
//      "id", "nom", "prénoms", "sexe", "email", "date de naissance",
//      "nationalité", "fonction", "domaine recherche", "demande aide",
//      "diplôme", "diplême préparé",
//      "montant demande voyage", "frais inscription peut payer",
//      "type demande séjour", "montant déjà eu pour séjour",
//      "type dispense frais inscription", "frais inscription à payer",
//      "montant aide voyage"
//    };
//    exportExcel.ajouterTitresColonnes(titresColonnes);
//
//  }
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Candidature0.class)
    public static class Candidature0ControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            Candidature0Controller controller = (Candidature0Controller) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "candidature0Controller");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Candidature0) {
                Candidature0 o = (Candidature0) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Candidature0Controller.class.getName());
            }
        }
    }
}
