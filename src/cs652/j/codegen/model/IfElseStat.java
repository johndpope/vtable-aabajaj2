package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 4/3/2017.
 */
public class IfElseStat extends IfStat {

    public @ModelElement
    List<OutputModelObject> elseStat = new ArrayList<>();

}
