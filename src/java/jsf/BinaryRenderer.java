package jsf;

/**
 * Renderer pour un composant qui correspond à un lien vers
 * des données binaires.
 * @author richard
 */
import java.io.IOException;
import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

/**
 * Renderer qui correspond à un lien HTML vers un flot de données binaires.
 * Tag : <cimpa:binaryData texte="Exporter vers Excel" type="excel"/>
 * Sera traduit en HTML par :
 * <a href="/binary/****">Exporter vers Excel</a>
 * @author richard
 */
@FacesRenderer(componentFamily = "javax.faces.Output",
rendererType = "org.cimpa.BinaryRenderer")
public class BinaryRenderer extends Renderer {

  /**
   * Indique de quel type de données binaire il s'agit.
   * Ca peut être le CV en PDF, les lettres de recommandation en PDF
   * ou un document Excel contenant tous les candidats à une école donnée.
   */
  private static String DEFAULT_TYPE = "excel";

  @Override
  public void encodeBegin(FacesContext context, UIComponent component)
          throws IOException {
    if (!component.isRendered()) {
      return;
    }

    Map<String, Object> attributes = component.getAttributes();
    String type = (String) attributes.get("type");
    if (type == null) {
      type = DEFAULT_TYPE;
    }
    BinaryData data = null;
    if (type.equals("excel")) {
      data = new ExcelData();
    }

    // Met le BinaryData dans la session avec comme identifiant
    // l'identifiant côté client du composant.
    String id = component.getClientId(context);
    ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
    Map<String, Object> session = external.getSessionMap();
    session.put(id, data);

    // Ecrit le code HTML du composant côté client
    ResponseWriter writer = context.getResponseWriter();
    writer.startElement("a", component);
    // Pour avoir un URL de type JSF car il faut que le phase listener
    // intervienne avant l'affichage de la vue JSF.
    // L'URL sera de la forme ******
    ViewHandler handler = context.getApplication().getViewHandler();
    String url = handler.getActionURL(context, BinaryPhaseListener.BINARY_PREFIX);

    writer.writeAttribute("href", url
            + "?" + BinaryPhaseListener.DATA_ID_PARAM + "=" + id, null);
    
    String texte = "Exporter vers Excel";// (String) attributes.get("texte");
    writer.write(texte);
    writer.endElement("a");

    context.responseComplete();
  }

}
