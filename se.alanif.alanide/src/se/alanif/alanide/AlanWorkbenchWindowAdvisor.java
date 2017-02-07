package se.alanif.alanide;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.core.helpers.LogHelper;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class AlanWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private static final String JUSTUPDATED = "justUpdated";

	public AlanWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		configurer.setShowProgressIndicator(true);
		configurer.setShowStatusLine(true);
	}
	
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new AlanActionBarAdvisor(configurer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
	 */
	public void postWindowOpen() {
		final IProvisioningAgent agent = (IProvisioningAgent) ServiceHelper
				.getService(AlanIDEPlugin.bundleContext,
						IProvisioningAgent.SERVICE_NAME);
		if (agent == null) {
			LogHelper.log(new Status(IStatus.ERROR, AlanIDEPlugin.PLUGIN_ID,
							"No provisioning agent found. This application is not set up for updates."));
		}
		// XXX if we're restarting after updating, don't check again.
		final IPreferenceStore prefStore = AlanIDEPlugin.getDefault().getPreferenceStore();
		if (prefStore.getBoolean(JUSTUPDATED)) {
			prefStore.setValue(JUSTUPDATED, false);
			return;
		}

		// Check for updates before starting up.
		// If an update is performed, restart. Otherwise log the status.
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				IStatus updateStatus = P2Util.checkForUpdates(agent, monitor);
				if (updateStatus.getCode() != UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
					if (updateStatus.getSeverity() != IStatus.ERROR) {
						prefStore.setValue(JUSTUPDATED, true);
						PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_INTRO, true);
						PlatformUI.getWorkbench().restart();
					} else {
						LogHelper.log(updateStatus);
					}
				}
			}
		};
		try {
			new ProgressMonitorDialog(null).run(true, true, runnable);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		}

	}

}
