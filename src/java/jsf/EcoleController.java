package jsf;

import entite.Ecole;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import ejb.EcoleFacade;
import java.io.Serializable;
import java.util.List;

import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean (name="ecoleController")
@SessionScoped
public class EcoleController implements Serializable {

    private Ecole current;
    private DataModel items = null;
    @EJB private ejb.EcoleFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    /**
     * Utilisé dans les listes.
     */
    private SelectItem[] ecolesConsultables2;
    private List<Ecole> ecolesConsultables;

    public EcoleController() {
    }

    public Ecole getSelected() {
        if (current == null) {
            current = new Ecole();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EcoleFacade getFacade() {
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
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem()+getPageSize()}));
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
        current = (Ecole)getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Ecole();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EcoleCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Ecole)getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EcoleUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Ecole)getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EcoleDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count-1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex+1}).get(0);
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
    
    /**
     * Retourne toutes les écoles consultables,
     * sous la forme d'un tableau de SelectItem
     * (pour être utilisé par un pickList).
     * @return
     */
    public SelectItem[] getAllEcolesConsultables2() {
        if (ecolesConsultables2 == null) {
            List<Ecole> listeEcoles = getAllEcolesConsultables();
//            // Remplit la map
//            mapEcoles = new HashMap(listeEcoles.size());
//            for (Ecole ecole : listeEcoles) {
//                mapEcoles.put(ecole.getCodeEcole(), ecole);
//            }
            // envelopper chaque école par un SelectItem
            ecolesConsultables2 =
                    JsfUtil.getSelectItems(listeEcoles, false);
        }
        return ecolesConsultables2;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    /**
     * Retourne toutes les écoles consultables,
     * sous la forme d'une liste.
     * @return
     */
    public List<Ecole> getAllEcolesConsultables() {
        if (ecolesConsultables == null) {
            ecolesConsultables =
                    ejbFacade.findAllEcolesConsultables();
        }
        return ecolesConsultables;
    }

    /*
     * On est obligé de nommer le convertisseur car <o:twoListSelection>
     * de OpenFaces ne reconnait pas forClass !
     */
    @FacesConverter(value="ecoleControllerConverter", forClass=Ecole.class)
    public static class EcoleControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
//            System.out.println("***********getAsObject pour convertir en Ecole");
            if (value == null || value.length() == 0) {
                return null;
            }
            EcoleController controller = (EcoleController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ecoleController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Ecole) {
                Ecole o = (Ecole) object;
                return getStringKey(o.getCodeEcole());
            } else {
                if (object instanceof String && object.equals("")) {
                    return null;
                }
                throw new IllegalArgumentException("object " + object 
                        + " is of type " + object.getClass().getName()
                        + "; expected type: "+Ecole.class.getName());
            }
        }

    }

}
