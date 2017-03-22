package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;

public class ComputeTypes extends JBaseListener {
	private Scope currentScope;
	protected StringBuilder buf = new StringBuilder();

	public static final Type JINT_TYPE = new JPrimitiveType("int");
	public static final Type JFLOAT_TYPE = new JPrimitiveType("float");
	public static final Type JSTRING_TYPE = new JPrimitiveType("string");
	public static final Type JVOID_TYPE = new JPrimitiveType("void");

	public ComputeTypes(GlobalScope globals) {
		this.currentScope = globals;
	}
	@Override
	public void enterFile(JParser.FileContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitFile(JParser.FileContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterBlock(JParser.BlockContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitBlock(JParser.BlockContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterClassDeclaration(JParser.ClassDeclarationContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void enterMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterMain(JParser.MainContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitMain(JParser.MainContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void exitClassDeclaration(JParser.ClassDeclarationContext ctx) {
		currentScope= currentScope.getEnclosingScope();
	}

	@Override
	public void enterLiteralRef(JParser.LiteralRefContext ctx) {
		if(ctx.INT() != null) {
			ctx.type = JINT_TYPE;
			buf.append(ctx.getText()+" is "+ JINT_TYPE.getName()+"\n");
		}else {
			ctx.type = JFLOAT_TYPE;
			buf.append(ctx.getText()+" is "+ JFLOAT_TYPE.getName()+"\n");
		}
	}

	@Override
	public void enterNullRef(JParser.NullRefContext ctx) {
		ctx.type = JVOID_TYPE;
		buf.append(ctx.getText()+" is "+ ctx.type.getName()+"\n");
	}

	@Override
	public void enterIdRef(JParser.IdRefContext ctx) {
		VariableSymbol vs = (VariableSymbol) currentScope.resolve(ctx.ID().getText());
		ctx.type = vs.getType();
		buf.append(ctx.getText()+" is "+ ctx.type.getName()+"\n");
	}

	@Override
	public void exitFieldRef(JParser.FieldRefContext ctx) {
		JClass t = (JClass) ctx.expression().type;
		TypedSymbol ts = (TypedSymbol) t.resolveMember(ctx.ID().getText());
		ctx.type = ts.getType();
		buf.append(ctx.getText()+" is "+ctx.type.getName()+"\n");
	}

	@Override
	public void enterThisRef(JParser.ThisRefContext ctx) {
		VariableSymbol vs = (VariableSymbol) currentScope.resolve(ctx.getText());
		ctx.type = vs.getType();
		buf.append(ctx.getText()+" is "+ctx.type.getName()+"\n");
	}

	@Override
	public void exitQMethodCall(JParser.QMethodCallContext ctx) {
		JClass t = (JClass) ctx.expression().type;
		TypedSymbol ts = (TypedSymbol) t.resolveMember(ctx.ID().getText());
		ctx.type = ts.getType();
		buf.append(ctx.getText()+" is "+ctx.type.getName()+"\n");
	}

	@Override
	public void enterCtorCall(JParser.CtorCallContext ctx) {
		JClass c = (JClass) currentScope.resolve(ctx.ID().getText());
		ctx.type = c;
		buf.append(ctx.getText()+" is "+c.getName()+"\n");
	}

	@Override
	public void exitMethodCall(JParser.MethodCallContext ctx) {
		if (ctx.ID().getText()!=null){
			ctx.type = ((TypedSymbol) currentScope.resolve(ctx.ID().getText())).getType();
			buf.append(ctx.getText()+" is "+ctx.type.getName()+"\n");
		}
	}

	public String getRefOutput() {
		return buf.toString();
	}
}
