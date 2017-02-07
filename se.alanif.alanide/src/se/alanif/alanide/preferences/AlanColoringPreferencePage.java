/*
 * Created on 2006-jan-01
 *
 */
package se.alanif.alanide.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import se.alanif.alanide.AlanIDEPlugin;
import se.alanif.alanide.editors.IAlanAppearance;

public class AlanColoringPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public AlanColoringPreferencePage() {
        super(GRID);
        setPreferenceStore(AlanIDEPlugin.getDefault().getPreferenceStore());
        setDescription("Preferences for syntactic coloring of Alan source code in the editor.");
        initializeDefaults();
    }

    private void initializeDefaults() {
        IPreferenceStore store = getPreferenceStore();
        PreferenceConverter.setDefault(store, IAlanPreferences.COMMENT_COLOR_PREFERENCE, IAlanAppearance.DEFAULT_COMMENT_COLOR);
        PreferenceConverter.setDefault(store, IAlanPreferences.STRING_COLOR_PREFERENCE, IAlanAppearance.DEFAULT_STRING_COLOR);
        PreferenceConverter.setDefault(store, IAlanPreferences.KEYWORD_COLOR_PREFERENCE, IAlanAppearance.DEFAULT_KEYWORD_COLOR);
        PreferenceConverter.setDefault(store, IAlanPreferences.ID_COLOR_PREFERENCE, IAlanAppearance.DEFAULT_ID_COLOR);
        PreferenceConverter.setDefault(store, IAlanPreferences.DEFAULT_COLOR_PREFERENCE, IAlanAppearance.DEFAULT_DEFAULT_COLOR);
    }

    public void init(IWorkbench workbench) {
    }

    protected void createFieldEditors() {
        ColorFieldEditor commentColorEditor = new ColorFieldEditor(IAlanPreferences.COMMENT_COLOR_PREFERENCE, "Comments", getFieldEditorParent());
        addField(commentColorEditor);
        ColorFieldEditor stringColorEditor = new ColorFieldEditor(IAlanPreferences.STRING_COLOR_PREFERENCE, "Strings", getFieldEditorParent());
        addField(stringColorEditor);
        ColorFieldEditor keywordColorEditor = new ColorFieldEditor(IAlanPreferences.KEYWORD_COLOR_PREFERENCE, "Keywords", getFieldEditorParent());
        addField(keywordColorEditor);
        ColorFieldEditor idColorEditor = new ColorFieldEditor(IAlanPreferences.ID_COLOR_PREFERENCE, "Identifiers", getFieldEditorParent());
        addField(idColorEditor);
        ColorFieldEditor defaultColorEditor = new ColorFieldEditor(IAlanPreferences.DEFAULT_COLOR_PREFERENCE, "Other text", getFieldEditorParent());
        addField(defaultColorEditor);
    }

}
