/*
 * Created on 2006 jun 26
 *
 */
package se.alanif.alanide.model;

public class AlanHeritage extends AlanModel {

    public AlanHeritage(String name, int start, int length) {
        this.name = name;
        this.start = start;
        this.length = length;
        this.kind = "heritage";
    }

}
