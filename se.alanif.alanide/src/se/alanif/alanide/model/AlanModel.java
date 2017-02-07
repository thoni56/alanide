package se.alanif.alanide.model;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import se.alanif.alanide.modelbuilder.Parser;
import se.alanif.alanide.modelbuilder.Scanner;

public class AlanModel implements ITreeContentProvider {

    private ArrayList<AlanModel> children = new ArrayList<AlanModel>();
    private AlanModel parent = null;
    protected String kind = "";
    protected String name;
    public int start, length;
    
    public AlanModel(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        Parser parser = new Parser(scanner);
        parser.Parse();
        children = parser.model.children;
    }

    public AlanModel() {
    }
    
    public AlanModel(String name) {
        this.name = name;
    }

    public void add(AlanModel child) {
        children.add(child);
    }
    
    public void add(ArrayList<AlanModel> children) {
        if (children != null)
            for (int i = 0; i < children.size(); i++)
                if (children.get(i) != null)
                    this.children.add(children.get(i));
    }
    
    public String toString() {
        return getName();
    }
    
    public String getName() {
        return name;
    }

    public String getKind() { return kind; }


    public Object[] getChildren(Object parentElement) {
        for (int i = 0; i < children.size(); i++)
            if (children.get(i) == parentElement)
                return ((AlanModel)children.get(i)).children.toArray();
        return null;
    }

    public Object getParent(Object element) {
        return parent;
    }

    public boolean hasChildren(Object element) {
        return children.size() > 0;
    }

    public Object[] getElements(Object inputElement) {
        return children.toArray();
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

}
