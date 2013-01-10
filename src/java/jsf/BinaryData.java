package jsf;

import java.io.OutputStream;

/**
 * Met les données binaires dans un flot, avec leur type MIME.
 * Sera utilisé pour envoyer les données binaires dans le flot
 * envoyé vers le client Web.
 * @author richard
 */
public interface BinaryData {

  public boolean write(OutputStream out) throws Exception;

  public String getContentType();

}
