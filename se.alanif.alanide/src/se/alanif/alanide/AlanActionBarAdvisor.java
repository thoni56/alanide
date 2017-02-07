package se.alanif.alanide;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class AlanActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction newWindowAction;
    private IWorkbenchAction preferenceAction;
    private IWorkbenchAction newWizardAction;
    private IWorkbenchAction cutAction;
    private IWorkbenchAction pasteAction;
    private IAction copyAction;
	private IWorkbenchAction introAction;

    public AlanActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        saveAction = ActionFactory.SAVE.create(window);
        register(saveAction);

        saveAllAction = ActionFactory.SAVE_ALL.create(window);
        register(saveAllAction);

        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        newWizardAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);
        register(newWizardAction);

        newWindowAction = ActionFactory.OPEN_NEW_WINDOW.create(window);
        register(newWindowAction);

        copyAction = ActionFactory.COPY.create(window);
        register(copyAction);

        cutAction = ActionFactory.CUT.create(window);
        register(cutAction);

        pasteAction = ActionFactory.PASTE.create(window);
        register(pasteAction);

        preferenceAction = ActionFactory.PREFERENCES.create(window);
        register(preferenceAction);
        
        introAction = ActionFactory.INTRO.create(window);
        register(introAction);

    }

    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager editMenu = new MenuManager("&Edit", "alanEditMenu");//IWorkbenchActionConstants.M_EDIT);
        MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
        MenuManager helpMenu = new MenuManager("&Help", "alanHelpMenu");//IWorkbenchActionConstants.M_HELP);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);

        // File
        fileMenu.add(newWizardAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveAction);
        fileMenu.add(saveAllAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        // Edit
        editMenu.add(copyAction);
        editMenu.add(cutAction);
        editMenu.add(pasteAction);
        editMenu.add(new Separator());
        editMenu.add(preferenceAction);
        
        // Window
        windowMenu.add(newWindowAction);

        // Help
        helpMenu.add(new GroupMarker("alanHelpStart"));
        helpMenu.add(introAction);
        helpMenu.add(aboutAction);
        helpMenu.add(new GroupMarker("alanHelpEnd"));
    }

    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "main"));
        toolbar.add(newWizardAction);
        toolbar.add(saveAction);
        toolbar.add(saveAllAction);
    }
}