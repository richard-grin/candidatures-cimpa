package jsf;

/**
 * Appelé juste avant l'affichage des pages JSF dont l'URL commence par
 * /binary.
 * D'après le livre Core JSF, pages 559 et suivantes.
 * @author richard
 */
import java.io.OutputStream;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

public class BinaryPhaseListener implements PhaseListener {
   public static final String BINARY_PREFIX = "/binary";

   public static final String DATA_ID_PARAM = "id";

  @Override
   public PhaseId getPhaseId() {
      return PhaseId.RESTORE_VIEW;
   }

  @Override
   public void beforePhase(PhaseEvent event) {
   }

  @Override
   public void afterPhase(PhaseEvent event) {
      if (!event.getFacesContext().getViewRoot().getViewId().startsWith(
            BINARY_PREFIX))
         return;

      FacesContext context = event.getFacesContext();
      ExternalContext external = context.getExternalContext();

      String id = (String) external.getRequestParameterMap().get(DATA_ID_PARAM);
      HttpServletResponse servletResponse =
            (HttpServletResponse) external.getResponse();
      try {
         // Récupère l'objet déposé par le renderer dans la session
         Map<String, Object> session = external.getSessionMap();
         BinaryData data = (BinaryData) session.get(id);
         if (data != null) {
            servletResponse.setContentType(data.getContentType());
            OutputStream out = servletResponse.getOutputStream();
            // data écrit les données binaires pour les envoyer au client
            System.out.println("**************Envoi des données avec " + data);
            boolean continuer = data.write(out);
            if (! continuer) {
              return;
            }
         }
      } catch (Exception ex) {
         throw new FacesException(ex);
      }
      // Indique qu'il faut passer directement à la phase de rendu de la réponse
      // en sautant donc les phases intermédiaires "normales" (on est ici
      // à la phase de restauration de la vue qui est au début du cycle
      // de vie de JSF ; on saute donc directement à la dernière phase
      // du cycle de vie sans passer par les phases de validations,
      // traitement des événements, etc.).
      context.responseComplete();
   }
}
