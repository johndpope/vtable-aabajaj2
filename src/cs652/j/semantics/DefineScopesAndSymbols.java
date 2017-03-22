package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;

import java.sql.Time;

public class DefineScopesAndSymbols extends JBaseListener {
	protected Scope currentScope;

	public DefineScopesAndSymbols(GlobalScope globals) {
		currentScope = globals;
		currentScope.define((Symbol) ComputeTypes.JINT_TYPE);
		currentScope.define((Symbol) ComputeTypes.JFLOAT_TYPE);
		currentScope.define((Symbol) ComputeTypes.JSTRING_TYPE);
		currentScope.define((Symbol) ComputeTypes.JVOID_TYPE);
	}

	@Override
	public void enterLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {
		VariableSymbol variables = new VariableSymbol(ctx.ID().getText());
		Type variableType = (Type) currentScope.resolve(ctx.jType().getText());
		variables.setType(variableType);
		currentScope.define(variables);
	}

	@Override
	public void enterFile(JParser.FileContext ctx) {
		ctx.scope = (GlobalScope) currentScope;
	}

	@Override
	public void exitFile(JParser.FileContext ctx) {
		popScope();
	}

	private void popScope() {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterClassDeclaration(JParser.ClassDeclarationContext ctx) {
		String className = ctx.name.getText();
		JClass c = new JClass(className,ctx);
		c.setEnclosingScope(currentScope);
		if(ctx.getChild(2).getText().startsWith("extends")) {
			c.setSuperClass(ctx.superClass.getText());
		}
		currentScope.define(c);
		ctx.scope = c;
		currentScope = c;
	}

	@Override
	public void exitClassDeclaration(JParser.ClassDeclarationContext ctx) {
		popScope();
	}

	@Override
	public void enterMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		JMethod method = new JMethod(ctx.ID().getText(),ctx);
		currentScope.define(method);
		Type fpType;
		if(ctx.jType()!=null){
			fpType = (Type) currentScope.resolve(ctx.jType().getText());
		}else {
			fpType = (Type) currentScope.resolve("void");
		}
		method.setType(fpType);
		currentScope = method;
		VariableSymbol fp = new VariableSymbol("this");
		fp.setType((Type) currentScope.getEnclosingScope());
		currentScope.define(fp);
		ctx.scope = method;
	}

	@Override
	public void enterFieldDeclaration(JParser.FieldDeclarationContext ctx) {
		JField fields = new JField(ctx.ID().getText());
		Type jtype = (Type) currentScope.resolve(ctx.jType().getText());
		fields.setType(jtype);
		currentScope.define(fields);
	}

	@Override
	public void enterFormalParameter(JParser.FormalParameterContext ctx) {
		JVar formalParameters = new JVar(ctx.ID().getText());
		Type jtype = (Type) currentScope.resolve(ctx.jType().getText());
		formalParameters.setType(jtype);
		currentScope.define(formalParameters);
	}

	@Override
	public void exitMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		popScope();
	}

	@Override
	public void enterBlock(JParser.BlockContext ctx) {
		LocalScope l = new LocalScope(currentScope);
		currentScope.nest(l);
		currentScope = l;
		ctx.scope = l;
	}

	@Override
	public void exitBlock(JParser.BlockContext ctx) {
		popScope();
	}

	@Override
	public void enterMain(JParser.MainContext ctx) {
		JMethod method = new JMethod("main",ctx);
		method.setType(ComputeTypes.JVOID_TYPE);
		currentScope.define(method);
		ctx.scope = method;
		currentScope = method;
	}
}
