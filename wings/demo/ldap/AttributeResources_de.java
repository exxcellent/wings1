package ldap;

import java.util.ListResourceBundle;

public class AttributeResources_de extends ListResourceBundle {

  public Object[][] getContents() {
    return contents;
  }

  private Object[][] contents = {
    { "cn", new String("Vorname") },
    { "Vorname", new String("cn") },
    { "sn", new String("Name") },
    { "Name", new String("sn") },
    { "Telefonnr.", new String("telephoneNumber") }, 
    { "telephoneNumber", new String("Telefonnr.") }, 
    { "Objektklasse", new String("objectClass") }, 
    { "objectClass", new String("Objektklasse") }, 
    { "Passwort", new String("userPassword") }, 
    { "userPassword", new String("Passwort") }, 
    { "jpegPhoto", new String("Foto") }, 
    { "Foto", new String("jpegPhoto") }, 
    { "street", new String("Strasse") }, 
    { "Strasse", new String("street") }, 
    { "l", new String("Stadt") }, 
    { "Stadt", new String("l") }, 
  };
}


