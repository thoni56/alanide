package se.alanif.alanide;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

public class AlanIDEWorkbenchAdvisor extends WorkbenchAdvisor {

    private static final String PERSPECTIVE_ID = "se.alanif.alanide.alanPerspective";

    public void preStartup() {
    	org.eclipse.ui.ide.IDE.registerAdapters();
    }

    public String getInitialWindowPerspectiveId() {
        return PERSPECTIVE_ID;
    }
    
    public IAdaptable getDefaultPageInput() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}
    
    public void initialize(IWorkbenchConfigurer configurer) {
    	 
		final String ICONS_PATH = "icons/full/";
		final String OBJ16 = "obj16/";
		final String EVIEW16 = "eview16/";
		
		// Icons for navigator view
		declareWorkbenchImage(configurer, IDE.SharedImages.IMG_OBJ_PROJECT, ICONS_PATH + OBJ16 + "prj_obj.gif");	//$NON-NLS-1$
		declareWorkbenchImage(configurer, IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, ICONS_PATH + OBJ16 + "cprj_obj.gif");	//$NON-NLS-1$
		
		// Icons for markers
		// TODO Get our own images
        declareWorkbenchImage(configurer, IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH, ICONS_PATH + OBJ16 + "error_tsk.gif"); //$NON-NLS-1$
        declareWorkbenchImage(configurer, IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH, ICONS_PATH + OBJ16 + "warn_tsk.gif"); //$NON-NLS-1$
        declareWorkbenchImage(configurer, IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH, ICONS_PATH + OBJ16 + "info_tsk.gif"); //$NON-NLS-1$

        // Icons for ProblemView title icon for the states
		declareWorkbenchImage(configurer, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW, ICONS_PATH + EVIEW16 + "problems_view.gif"); //$NON-NLS-1$
        declareWorkbenchImage(configurer, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_WARNING, ICONS_PATH + EVIEW16 + "problems_view_warning.gif"); //$NON-NLS-1$
        declareWorkbenchImage(configurer, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_ERROR, ICONS_PATH + EVIEW16 + "problems_view_error.gif"); //$NON-NLS-1$

	}
 
	private void declareWorkbenchImage(IWorkbenchConfigurer configurer_p,
			String symbolicName, String path) {
		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);
        URL url = ideBundle.getEntry(path);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		configurer_p.declareImage(symbolicName, desc, true);
	}
	
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new AlanWorkbenchWindowAdvisor(configurer);
	}
}
