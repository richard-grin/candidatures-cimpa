package jsf.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author richard
 */
public class Md5 {

  /**
   * Encrypte the given string according to MD5 encryption.
   *
   * @param s the string to encrypt
   * @return the encrypted string.
   */
  public static String encryptMD5(String s) {
    byte[] defaultBytes = s.getBytes();
    try {
      MessageDigest algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(defaultBytes);
      byte messageDigest[] = algorithm.digest();
      StringBuilder hexString = new StringBuilder();
      for (int i = 0; i < messageDigest.length; i++) {
        String str = Integer.toHexString(0xFF & messageDigest[i]);
        if (str.length() < 2) {
          hexString.append("0").append(str);
        }
        else {
          hexString.append(str);
        }
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException nsae) {
    }
    return null;
  }

  // Ma version
  /**
   * Renvoie le md5 d'un mot de passe.
   * Le mot de passe est rangé dans un tableau de char (comme dans Swing).
   * Pourquoi ne pas renvoyer plus simplement une String ????
   * La méthode digest renvoie un byte[] mais pour ranger dans la base
   * et comparer avec ce qui est dans la base, ça ne serait pas mieux de renvoyer une String ????
   * Ou alors, comment ranger un byte[] dans un SGBD relationnel (Oracle par exemple).
   */
  public static byte[] md5(char[] source) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      return md5.digest(charToByte(source));
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }

  /**
   * Transforme un tableau de char en un tableau d'octets.
   * Utilise le charset ISO-8859-1 ; pourquoi pas plutôt
   * utiliser le charset UTF-8 ?
   */
  public static byte[] charToByte(char[] source) {
//    Charset charset = Charset.forName("UTF-8");
    Charset charset = Charset.forName("ISO-8859-1");
    return charToByte(source, charset);
  }

  /**
   * Transforme un tableau de char en un tableau d'octets.
   * Utilise le charset passé en 2ème paramètre
   */
  public static byte[] charToByte(char[] source, Charset charset) {
    CharsetEncoder encoder = charset.newEncoder();
    ByteBuffer bbuf = null;
    try {
      bbuf = encoder.encode(CharBuffer.wrap(source));
    } catch (CharacterCodingException e) {
      e.printStackTrace();
    }
    return bbuf.array();
  }

  /**
   * Transforme un tableau de byte en une String.
   * @param bArray
   * @return
   */
  public static String byteArrayToHexString(byte[] bArray) {
    StringBuilder buffer = new StringBuilder();
    for (byte b : bArray) {
      buffer.append(Integer.toHexString(b));
//      buffer.append(" ");
    }
    return buffer.toString(); //.toUpperCase();
  }
}
