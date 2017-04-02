package cs652.j.codegen.model;

import cs652.j.parser.JParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class PrintStat extends Stat {
    public @ModelElement String print;
    public @ModelElement
    List<OutputModelObject> args = new ArrayList<>();
    public PrintStat(String text) {
        print = text;
    }
}
