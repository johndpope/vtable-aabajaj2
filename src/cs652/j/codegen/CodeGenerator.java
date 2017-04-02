package cs652.j.codegen;

import cs652.j.codegen.model.CFile;
import cs652.j.codegen.model.MainMethod;
import cs652.j.codegen.model.Block;
import cs652.j.codegen.model.OutputModelObject;
import cs652.j.parser.JBaseVisitor;
import cs652.j.parser.JParser;
import cs652.j.semantics.JClass;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import sun.applet.Main;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator extends JBaseVisitor<OutputModelObject> {
	public STGroup templates;
	public String fileName;

	public Scope currentScope;
	public JClass currentClass;

	public CodeGenerator(String fileName) {
		this.fileName = fileName;
		templates = new STGroupFile("cs652/j/templates/C.stg");
	}

	public CFile generate(ParserRuleContext tree) {
		CFile file = (CFile)visit(tree);
		return file;
	}

	@Override
	public OutputModelObject visitFile(JParser.FileContext ctx) {
		MainMethod main = (MainMethod) visit(ctx.main());
		CFile file = new CFile(fileName);
		file.main = main;
		return file;
	}

	@Override
	public OutputModelObject visitMain(JParser.MainContext ctx) {
		MainMethod mainMethod = (MainMethod) visit(ctx.block());
//        mainMethod. = visit(ctx.block());
		return mainMethod;
	}

	@Override
	public OutputModelObject visitBlock(JParser.BlockContext ctx) {
        Block block = new Block();
        for (JParser.StatementContext stat : ctx.statement()) {
            OutputModelObject smt = visit(stat);

        }
        return block;
    }
}