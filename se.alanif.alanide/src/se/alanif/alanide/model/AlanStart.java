/*
 * Created on 2005-jul-04
 *
 */
package se.alanif.alanide.model;

public class AlanStart extends AlanModel {
    
    public AlanStart(int start, int length) {
        this.name = "Start";
        this.start = start;
        this.length = length;
        this.kind = "start";
    }
}
