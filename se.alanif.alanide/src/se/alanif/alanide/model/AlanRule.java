/*
 * Created on 2005-jul-02
 *
 */
package se.alanif.alanide.model;

public class AlanRule extends AlanModel {

    public AlanRule(int start, int length) {
        this.name = "When";
        this.start = start;
        this.length = length;
        this.kind = "rule";
    }
    
}
