package se.alanif.alanide.model;

public class AlanPrompt extends AlanModel {

    public AlanPrompt(int start, int length) {
        this.start = start;
        this.length = length;
        this.kind = "prompt";
    }
    
}
