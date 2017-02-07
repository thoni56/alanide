/*
 * Created on 2004-okt-09
 *
 */
package se.alanif.alanide.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.resources.ICommand;

/**
 * @author Thomas Nilsson
 *
 */
public class AlanNature implements IProjectNature {

	private static final String BUILDER_ID = "se.alanif.alanide.AlanBuilder";
	private IProject project;
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		   IProjectDescription desc = project.getDescription();
		   ICommand[] commands = desc.getBuildSpec();
		   boolean found = false;

		   for (int i = 0; i < commands.length; ++i) {
		      if (commands[i].getBuilderName().equals(BUILDER_ID)) {
		         found = true;
		         break;
		      }
		   }
		   if (!found) { 
		      //add builder to project
		      ICommand command = desc.newCommand();
		      command.setBuilderName(BUILDER_ID);
		      ICommand[] newCommands = new ICommand[commands.length + 1];

		      // Add it before other builders.
		      System.arraycopy(commands, 0, newCommands, 1, commands.length);
		      newCommands[0] = command;
		      desc.setBuildSpec(newCommands);
		      project.setDescription(desc, null);
		   }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return this.project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
