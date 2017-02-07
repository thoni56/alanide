package se.alanif.alanide.modelbuilder;
/*
 * Created on 2005-jul-02
 *
 */

public class AlanParser {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String fileName = args[0];
        for (int i = 1; i < args.length; i++) fileName = fileName + " " + args[i];
        Scanner scanner = new Scanner(fileName);
        Parser parser = new Parser(scanner);
        parser.Parse();
        System.out.println(parser.errors.count + " errors detected");
    }

}
