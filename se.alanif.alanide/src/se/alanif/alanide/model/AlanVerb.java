/*
 * Created on 2005-jul-02
 *
 */
package se.alanif.alanide.model;

public class AlanVerb extends AlanModel {

    public AlanVerb(String name, int start, int length) {
        this.name = name;
        this.start = start;
        this.length = length;
        this.kind = "verb";
    }
    
}
