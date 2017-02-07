/*
 * Created on 2004-okt-09
 *
 */
package se.alanif.alanide.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.MarkerUtilities;

import se.alanif.alanide.AlanIDEPlugin;
import se.alanif.alanide.preferences.IAlanPreferences;

/**
 * @author Thomas Nilsson
 *
 */
public class AlanBuilder extends IncrementalProjectBuilder {

	private Runtime rt;
	private boolean useIdeSwitch = true;
	private ArrayList<String> compileArgs;

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map buildArgs, IProgressMonitor monitor) throws CoreException {
		final IPreferencesService preferences = Platform.getPreferencesService();
		final String compilerPath = preferences.getString(AlanIDEPlugin.PLUGIN_ID, IAlanPreferences.COMPILER_PATH_PREFERENCE, "bin/alan.exe", null);
		File compiler = new File(compilerPath);
		rt = Runtime.getRuntime();  
		if (compiler.exists()) {
			try {
				monitor.beginTask("Compiling...", 2);
				monitor.subTask("Setting up...");
				Thread.sleep(1000);
				setupArguments(preferences, compilerPath);
				monitor.worked(1);
				monitor.subTask("Running compiler...");
				Thread.sleep(1000);
				compile();
				monitor.worked(1);
			} catch (InterruptedException e) {
			} finally {
				monitor.done();
			}
		}
		return null;
	}

	private void setupArguments(IPreferencesService preferences, String compilerPath) {
		compileArgs = new ArrayList<String>();
		compileArgs.add(compilerPath);
		compileArgs.add("-info");
		compileArgs.add(useIdeSwitch?"-ide":"-cc");
		String importPath = preferences.getString(AlanIDEPlugin.PLUGIN_ID, IAlanPreferences.LIBRARY_PATH_PREFERENCE, "", null);
		if (importPath != "") {
			compileArgs.add("-import");
			compileArgs.add(importPath);
		}
		boolean debug = preferences.getBoolean("se.alanif.alanide", "debugPreference", true, null);
		if (debug)
			compileArgs.add("-debug");
		compileArgs.add(null);
	}

	private void compile() {
		deleteAllMarkers(getProject());
		try {
			getProject().accept(new AlanBuildVisitor());
		} catch (CoreException e) { }
	}

	class AlanBuildVisitor implements IResourceVisitor {

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
		 */
		public boolean visit(IResource resource) throws CoreException {
			String fileExtension = resource.getFileExtension();
			if (fileExtension != null && fileExtension.equals("alan")) {
				runCompiler(resource);
			}
			return true;
		}
	}

	private void runCompiler(IResource resource) {
		try {
			File currentDirectory = getCurrentDirectoryFromResource(resource);
			addFilenameToArgs(resource);
			String[] argsArray = compileArgs.toArray(new String[compileArgs.size()]);
			
			Process proc = rt.exec(argsArray, null, currentDirectory);
			BufferedReader br = getOutputReader(proc);

			parseOutput(resource, br);
			proc.waitFor();
			
			refreshForNewFiles(resource);
			updateDecorations();

		} catch (Exception e) {
			System.out.println("Could not execute compiler:");
			e.printStackTrace();
		}
	}

	private void parseOutput(IResource resource, BufferedReader br)
			throws IOException {
		String line = null;
		while ( (line = br.readLine()) != null) {
			if (useIdeSwitch)
				parseMessageUsingIDESwitch(resource, line);
			else
				parseMessageWithoutIDESwitch(resource, line);

		}
	}

	private BufferedReader getOutputReader(Process proc) {
		return new BufferedReader(new InputStreamReader(proc.getInputStream()));
	}

	private void refreshForNewFiles(IResource resource) throws CoreException {
		resource.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	private void updateDecorations() {
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
			IDecoratorManager idm = PlatformUI.getWorkbench().getDecoratorManager();
			idm.update("se.alanif.alanide.problemdecorator"); 
								       }
								});
	}


	private File getCurrentDirectoryFromResource(IResource resource) {
		return resource.getLocation().removeLastSegments(1).toFile();
	}

	private void addFilenameToArgs(IResource resource) {
		String fileName = resource.getLocation().toOSString();
		compileArgs.set(compileArgs.size()-1, fileName);
	}

	/**
	 * @param resource
	 */
	private void deleteAllMarkers(IResource resource) {
		try {
			resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// something went wrong
		}
	}

	private void createMarker(IResource resource, Map<String, Object> map) throws CoreException {
		MarkerUtilities.createMarker(resource, map, IMarker.PROBLEM);
	}

	private void parseMessageWithoutIDESwitch(IResource resource, String line) {
		if (line.equals("")) return;
		String[] file = line.split(",",2);
		if (file[0].charAt(0) == '"') file[0] = file[0].substring(1,file[0].length()-1);
		Path path = new Path(file[0]);
		IPath projectPath = getProject().getLocation();
		int matchingSegments = path.matchingFirstSegments(projectPath);
		IPath memberPath = path.removeFirstSegments(matchingSegments);
		IResource container = getProject().findMember(memberPath);
		String[] parts = file[1].split("line ", 2);
		String[] lineNumber = parts[1].split("\\(", 2);
		String[] column = lineNumber[1].split("\\)", 2);
		String[] message = column[1].split(": ", 2);
		String[] severity = message[1].split(" ", 3);
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			MarkerUtilities.setLineNumber(map, Integer.parseInt(lineNumber[0]));
			MarkerUtilities.setMessage(map, message[1]);
			switch (severity[1].charAt(0)) {
			case 'F':
			case 'S': 
			case 'E': map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR)); break;
			case 'W': map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING)); break;
			case 'I': map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO)); break;
			}
			if (container != null)
				createMarker(container, map); 
		} catch (CoreException e) {}
	}

	private void parseMessageUsingIDESwitch(IResource mainFileResource, String line) {
		String[] filename = line.split(",",2);
		if (filename[0].charAt(0) == '"')
			filename[0] = filename[0].substring(1,filename[0].length()-1);
		Path path = new Path(filename[0]);
		IPath projectPath = getProject().getLocation();
		int matchingSegments = path.matchingFirstSegments(projectPath);
		IPath memberPath = path.removeFirstSegments(matchingSegments);
		IResource fileResource = getProject().findMember(memberPath);
		try {
			String[] ignore = filename[1].split("line ", 2);
			String[] linenumber = ignore[1].split(" ", 2);
			String[] startPosition = linenumber[1].split("-", 2);
			String[] endPosition = startPosition[1].split(": ", 2);
			String message = endPosition[1];
			String[] severity = message.split(" ", 3);
			// System.out.println("start = "+startPosition[0]+", end = "+endPosition[0]);
			IMarker m;
			if (fileResource != null)
				m = fileResource.createMarker(IMarker.PROBLEM);
			else
				m = mainFileResource.createMarker(IMarker.PROBLEM);
			m.setAttribute(IMarker.CHAR_START, Integer.parseInt(startPosition[0]));
			m.setAttribute(IMarker.CHAR_END, Integer.parseInt(endPosition[0]));
			m.setAttribute(IMarker.LINE_NUMBER, Math.max(Integer.parseInt(linenumber[0]),1));
			m.setAttribute(IMarker.MESSAGE, message);
			switch (severity[1].charAt(0)) {
			case 'F':
			case 'S':
			case 'E':
				m.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
				break;
			case 'W':
				m.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
				break;
			case 'I':
				m.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
				break;
			}
		} catch (CoreException e) {}
	}
}
