/*
 * Created on 2006-jan-10
 *
 */
package se.alanif.alanide.editors;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class AlanEditorMessages {

    private static final String RESOURCE_BUNDLE= "se.alanif.alanide.editors.AlanEditorMessages";//$NON-NLS-1$

    private static ResourceBundle fgResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

    private AlanEditorMessages() {
    }

    public static String getString(String key) {
        try {
            return fgResourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
        }
    }
    
    public static ResourceBundle getResourceBundle() {
        return fgResourceBundle;
    }

}
