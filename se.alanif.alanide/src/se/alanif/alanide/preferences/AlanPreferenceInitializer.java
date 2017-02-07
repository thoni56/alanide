/*
 * Created on 2006-jan-03
 *
 */
package se.alanif.alanide.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.StringConverter;
import se.alanif.alanide.editors.IAlanAppearance;

public class AlanPreferenceInitializer extends AbstractPreferenceInitializer {

    public AlanPreferenceInitializer() {
        super();
    }

    public void initializeDefaultPreferences() {
        IEclipsePreferences node = DefaultScope.INSTANCE.getNode("se.alanif.alanide");
        node.put(IAlanPreferences.COMMENT_COLOR_PREFERENCE, StringConverter.asString(IAlanAppearance.DEFAULT_COMMENT_COLOR));
        node.put(IAlanPreferences.STRING_COLOR_PREFERENCE, StringConverter.asString(IAlanAppearance.DEFAULT_STRING_COLOR));
        node.put(IAlanPreferences.KEYWORD_COLOR_PREFERENCE, StringConverter.asString(IAlanAppearance.DEFAULT_KEYWORD_COLOR));
        node.put(IAlanPreferences.ID_COLOR_PREFERENCE, StringConverter.asString(IAlanAppearance.DEFAULT_ID_COLOR));
        node.put(IAlanPreferences.DEFAULT_COLOR_PREFERENCE, StringConverter.asString(IAlanAppearance.DEFAULT_DEFAULT_COLOR));
    }

}
