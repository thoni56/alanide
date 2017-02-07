package se.alanif.alanide.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import se.alanif.alanide.AlanIDEPlugin;
import se.alanif.alanide.preferences.IAlanPreferences;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class AlanPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
    
  
	public AlanPreferencePage() {
		super(GRID);
		setPreferenceStore(AlanIDEPlugin.getDefault().getPreferenceStore());
		setDescription("General preference settings for Alan IF development");
		initializeDefaults();
	}

	/**
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults() {
	    IPreferenceStore store = getPreferenceStore();
	    store.setDefault(IAlanPreferences.DEBUG_PREFERENCE, true);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
        FileFieldEditor f = new FileFieldEditor(IAlanPreferences.COMPILER_PATH_PREFERENCE, 
                "Selected &Compiler :", getFieldEditorParent());
        final String extensions[] = {"*.exe", "*"};
        f.setFileExtensions(extensions);
		addField(f);
		
		addField(new DirectoryFieldEditor(IAlanPreferences.LIBRARY_PATH_PREFERENCE, 
				"Path to standard &Library :", getFieldEditorParent()));
		addField(new BooleanFieldEditor(IAlanPreferences.DEBUG_PREFERENCE,
				"Generate &Debug information", getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench) {
	}
}