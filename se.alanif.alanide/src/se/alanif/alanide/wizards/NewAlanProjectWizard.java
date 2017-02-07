package se.alanif.alanide.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 */

public class NewAlanProjectWizard extends Wizard implements INewWizard, IExecutableExtension {
	private WizardNewProjectCreationPage page;
	private IConfigurationElement configuration;

	/**
	 * Constructor for NewAlanFileWizard.
	 */
	public NewAlanProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new WizardNewProjectCreationPage("New Alan Project");
		page.setTitle("Create a new Alan Project");
		// TODO Fix our own image
		page.setImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/newprj_wiz.png"));
		page.setDescription("Give the project a name, which will also be its subdirectory either at the default location or in a folder of your choice.");
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String projectName = page.getProjectName();
		final IPath projectLocation = page.useDefaults()?null:page.getLocationPath();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(projectName, monitor, projectLocation);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 * @param projectLocation 
	 */

	private void doFinish(String projectName, IProgressMonitor monitor, IPath projectLocation) throws CoreException {
		// Create the project
		monitor.beginTask("Creating " + projectName, 5);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		monitor.worked(1);

		IProjectDescription projectDescription = workspace.newProjectDescription(projectName);
		if (projectLocation != null)
			projectDescription.setLocation(projectLocation.append(projectName));
		String natures[] = {"se.alanif.alanide.AlanNature"};
		projectDescription.setNatureIds(natures);
		monitor.worked(1);

		ICommand[] builders = {null};
		builders[0] = projectDescription.newCommand();
		builders[0].setBuilderName("se.alanif.alanide.AlanBuilder");
		projectDescription.setBuildSpec(builders);
		monitor.worked(1);
		
		IProject project = root.getProject(projectName);
		try {
			project.create(projectDescription, monitor);
			monitor.worked(1);
		} catch (Exception e) {
			throwCoreException("Could not create project");
		}
		BasicNewProjectResourceWizard.updatePerspective(configuration);
		project.open(monitor);
		monitor.done();
	}
	

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "se.alanif.alanide", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configuration = config;
	}
}