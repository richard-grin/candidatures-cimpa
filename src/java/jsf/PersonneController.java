package jsf;

import entite.Personne;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import ejb.PersonneFacade;
import entite.Ecole;
import java.io.Serializable;
import java.util.List;

import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import jsf.util.Md5;

@ManagedBean(name = "personneController")
@SessionScoped
public class PersonneController implements Serializable {

    private Personne current;
    private DataModel items = null;
    @EJB
    private ejb.PersonneFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private String motDePasse1;
    private String motDePasse2;

    public String getMotDePasse1() {
        return motDePasse1;
    }

    public void setMotDePasse1(String motDePasse1) {
        this.motDePasse1 = motDePasse1;
    }

    public String getMotDePasse2() {
        return motDePasse2;
    }

    public void setMotDePasse2(String motDePasse2) {
        this.motDePasse2 = motDePasse2;
    }

    /**
     * Les écoles choisies : celles que la personne courante
     * pourra consulter.
     */
//  private List<Ecole> ecolesChoisies;
    public List<Ecole> getEcolesChoisies() {
        return this.current.getEcolesConsultables();
    }

    public void setEcolesChoisies(List<Ecole> ecolesChoisies) {
//        System.out.println("++++setEcolesChoisies(" + ecolesChoisies + ")");
        this.current.setEcolesConsultables(ecolesChoisies);
    }

    public boolean isUserIsAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
//        String loginUser = request.getRemoteUser();
        return request.isUserInRole("admin");
    }

    public PersonneController() {
    }

    public Personne getSelected() {
        if (current == null) {
            current = new Personne();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PersonneFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Personne) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Personne();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PersonneCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Personne) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PersonneUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Personne) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PersonneDeleted"));
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

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    /**
     * Ecoute un changement de mot de passe.
     * @param event
     */
    public void motDePasseChange(ValueChangeEvent event) {
//    try {
        // Met le mot de passe en MD5
//      MessageDigest md = MessageDigest.getInstance("MD5");

//      current.setMotDePasse(new String(md.digest(current.getMotDePasse().getBytes())));
//      current.setMotDePasse(Md5.encryptMD5(current.getMotDePasse()));
        UIInput component = (UIInput) event.getComponent();
        component.setValue(Md5.encryptMD5((String) event.getNewValue()));

//      current.setMotDePasse(Md5.encryptMD5((String) event.getNewValue()));
        System.out.println("++++++++++++Nouveau mot de passe : " + current.getMotDePasse());
//    } catch (NoSuchAlgorithmException ex) {
//      Logger.getLogger(PersonneController.class.getName()).log(Level.SEVERE, null, ex);
//    }
    }

    @FacesConverter(forClass = Personne.class)
    public static class PersonneControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonneController controller = (PersonneController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personneController");
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
            if (object instanceof Personne) {
                Personne o = (Personne) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PersonneController.class.getName());
            }
        }
    }
}
