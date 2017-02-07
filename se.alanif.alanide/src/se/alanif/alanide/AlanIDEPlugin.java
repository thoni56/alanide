package se.alanif.alanide;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.*;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.net.URL;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class AlanIDEPlugin extends AbstractUIPlugin {

	static BundleContext bundleContext;

    //The shared instance.
	private static AlanIDEPlugin plugin;
	
	//Resource bundle.
	private ResourceBundle resourceBundle = null;
    private IPreferenceStore preferenceStore = null;

	private ImageRegistry registry;

    public static final String PLUGIN_ID = "se.alanif.alanide";
    public static final String ICON_PREFIX = PLUGIN_ID + ".icon.";
    
    private static final String icons[] = {"alanide16x16", "alan_doc"};
    
    private static final String overlays[] = {"error_ovr", "warning_ovr"};

    private static final String outlineIcons[] = {
    	"class", "addition", "instance", "prompt", "event", "rule", "message", "verb", "syntax",
    	"synonym", "import", "start", "boolean", "integer", "string", "reference", "set"};

	/**
	 * The constructor.
	 */
	public AlanIDEPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle(PLUGIN_ID);
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

    public IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, AlanIDEPlugin.PLUGIN_ID);
        }
        return preferenceStore;
    }


    protected void initializeImageRegistry(ImageRegistry registry) {
    	this.registry = registry;
    	registerIcons(registry, icons, null, ".png");
    	registerIcons(registry, overlays, "ovr16", ".png");
        registerIcons(registry, outlineIcons, "outline", ".png");
    }

	private void registerIcons(ImageRegistry registry, String[] iconNames, String subdirectory, String format) {
		for (int i = 0; i < iconNames.length; i++)
            registerIcon(registry, iconNames[i], subdirectory, format);
	}

    private void registerIcon(ImageRegistry registry, String icon, String subdirectory, String format) {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        IPath path = new Path("icons/" + asPath(subdirectory) + icon + format);
        URL url = FileLocator.find(bundle, path, null);
        ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
        registry.put(ICON_PREFIX + asId(subdirectory) + icon, imageDescriptor);
    }

    private String asPath(String subdirectory) {
		return subdirectory != null ? subdirectory + "/" : "";
	}

    private String asId(String subdirectory) {
		return subdirectory !=null ? subdirectory + "." : "";
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
		plugin = this;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
	    plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static AlanIDEPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = AlanIDEPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return plugin.registry.getDescriptor(PLUGIN_ID+".icon."+key);
	}
}
