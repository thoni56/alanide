/*
 * Created on 2005-jul-03
 *
 */
package se.alanif.alanide.model;

public class AlanInstance extends AlanModel {

    public AlanInstance(String name, int start, int length) {
        this.name = name;
        this.start = start;
        this.length = length;
        this.kind = "instance";
    }
}
