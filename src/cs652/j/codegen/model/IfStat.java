package cs652.j.codegen.model;

import cs652.j.parser.JParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class IfStat extends Stat {
    public @ModelElement String condition;
    public @ModelElement List<OutputModelObject> stat = new ArrayList<>();

}

