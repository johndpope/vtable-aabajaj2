package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class PrintStringStat extends Stat {
    public @ModelElement String print;

    public PrintStringStat(String text) {
        print = text;
    }

}