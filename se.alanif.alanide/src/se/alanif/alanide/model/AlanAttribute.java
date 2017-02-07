/*
 * Created on 2005-jul-02
 *
 */
package se.alanif.alanide.model;

public class AlanAttribute extends AlanModel {

    public AlanAttribute(String name, String kind, int start, int length) {
        this.name = name;
        this.start = start;
        this.length = length;
        this.kind = kind;
    }
    
}
