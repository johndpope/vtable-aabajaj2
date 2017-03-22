package cs652.j.semantics;

import org.antlr.symtab.MemberSymbol;
import org.antlr.symtab.VariableSymbol;

/**
 * Created by Anjani Bajaj on 3/8/2017.
 */
public class JField extends VariableSymbol implements MemberSymbol {
    public JField(String name) {
        super(name);
    }

    @Override
    public int getSlotNumber() {
        return 0;
    }
}
