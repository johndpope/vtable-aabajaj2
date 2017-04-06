package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 4/3/2017.
 */
public class WhileStat extends Stat {

    public @ModelElement String condition;
    public @ModelElement String stat;
}
