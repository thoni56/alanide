/*
 * Created on 2005-jul-02
 *
 */
package se.alanif.alanide.model;

public class AlanMessage extends AlanModel {

    public AlanMessage(String name, int start, int length) {
        this.name = name;
        this.start = start;
        this.length = length;
        this.kind = "message";
    }
    
}
