import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
public class Driver {
    public static void main(String[] args) {
        new Driver().step3(System.in);
    }
    public ResultContext step1(InputStream is) {

        final String DISPLAY = "Token Type: %s\nValue: %s\n";
        final HashSet<String> keyword_text = new HashSet<String>() {{
            add("PROGRAM"); add("BEGIN"); add("END"); add("FUNCTION"); add("READ"); add("WRITE");
            add("IF"); add("ELSE"); add("ENDIF"); add("WHILE"); add("ENDWHILE"); add("CONTINUE"); add("BREAK");
            add("RETURN"); add("INT"); add("VOID"); add("STRING"); add("FLOAT");
        }};
        final HashSet<String> op_text = new HashSet<String>() {{
            add("ASSIGN"); add("PLUS"); add("MINUS"); add("MULTIPLY"); add("DIVIDE");
            add("EQUAL"); add("NOTEQUAL"); add("LESS"); add("GREATER"); add("LPAREN");
            add("RPAREN"); add("SEMICOLON"); add("COMMA"); add("LESSEQUAL"); add("GREATEREQUAL");
        }};

        StringBuilder computedResult = new StringBuilder();
        try {
            LittleLexer lexer = new LittleLexer(new ANTLRInputStream(is));
            Token t;
            while ((t = lexer.nextToken()) != null && !lexer._hitEOF) {
                String name = lexer.getVocabulary().getSymbolicName(t.getType());
                if (keyword_text.contains(name))
                    name = "KEYWORD";
                else if (op_text.contains(name))
                    name = "OPERATOR";
               computedResult.append(String.format(DISPLAY, name, t.getText()));
            }
        }
        catch (Exception ex) {
            return new ResultContext(is.getClass()).withSuccess(false);
        }
        return new ResultContext(is.getClass()).withContent(computedResult.toString()).displayIfApplicable();
    }
    public ResultContext step2(InputStream is) {
        String result = "";
        try {
            CommonTokenStream cts = new CommonTokenStream(new LittleLexer(new ANTLRInputStream(is)));
            LittleParser parse = new LittleParser(cts);
            parse.removeErrorListeners();
            parse.removeParseListeners();
            parse.addErrorListener(new LittleErrorListener());

            parse.program();
            result = "Accepted";

        } catch (RuntimeException ex) {
            //Place failure message here.
            result = "Not accepted";
        } catch (IOException ex) {
            return new ResultContext(is.getClass()).withSuccess(false);
        }

        return new ResultContext(is.getClass()).withContent(result).displayIfApplicable();
    }
    public ResultContext step3(InputStream is) {
        try {
            LittleParser parse = new LittleParser(new CommonTokenStream(new LittleLexer(new ANTLRInputStream(is))));
            parse.removeErrorListeners();
            parse.removeParseListeners();
            parse.addErrorListener(new LittleErrorListener());

            ParseTreeWalker walker = new ParseTreeWalker();
            CustomLittleListener listener = new CustomLittleListener();
            walker.walk(listener, parse.program());

            if(Objects.nonNull(listener.getDeclError())) {
                return new ResultContext(is.getClass()).withContent(String.format("DECLARATION ERROR %s",listener.getDeclError())).displayIfApplicable();
            }
            else return new ResultContext(is.getClass()).withContent(listener.getSymbolTableString()).displayIfApplicable();
        }
        catch (RuntimeException | IOException ex) {
            return new ResultContext(is.getClass()).withSuccess(false);
        }
    }
}
class LittleErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        try {
            throw new Exception("Parser Error at symbol: "+offendingSymbol.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {

    }
    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {

    }
    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {

    }
}
class SymbolEntry {
    String name;
    String type;
    String value;
    public SymbolEntry(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }
    public String getName() { return name; }
    public String getValue() { return value; }
    @Override
    public String toString() {
        if(Objects.isNull(value)) return String.format("name %s type %s%n",getName(),getType());
        else return String.format("name %s type %s value %s%n",getName(),getType(),getValue());
    }
}
class CustomLittleListener extends LittleBaseListener {
    // Keeps track of order the variables have seen
    private Stack<Map<String, SymbolEntry>> symbolTableStack = new Stack<>();
    // Stores current symbols as they are seen (LinkedHashMap to store order)
    private Map<String, SymbolEntry> currentSymbolTable = new LinkedHashMap<>();
    // Saves what the stack has done as it pops scopes, scopSymbolTables is the resulting structure
    private Map<String, Map<String, SymbolEntry>> scopeSymbolTables = new LinkedHashMap<>();
    // Number that increments when a new scope has been entered
    private int blockNum = 1;
    private List<String> declError = new ArrayList<>();

    public CustomLittleListener() {
        symbolTableStack.push(new LinkedHashMap<>());
        currentSymbolTable = symbolTableStack.peek();
        scopeSymbolTables.put("GLOBAL", currentSymbolTable);
    }
    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx) {

        currentSymbolTable = new LinkedHashMap<>();
        symbolTableStack.push(currentSymbolTable);
        scopeSymbolTables.put(ctx.id().getText(), currentSymbolTable);
    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        symbolTableStack.pop();
        currentSymbolTable = symbolTableStack.peek();
    }


    @Override
    public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        currentSymbolTable = new LinkedHashMap<>();
        symbolTableStack.push(currentSymbolTable);
        scopeSymbolTables.put("BLOCK " + blockNum, currentSymbolTable);
        blockNum++;

        LittleParser.Else_partContext elsePart = ctx.else_part();
        if (elsePart != null && elsePart.stmt_list() != null && !elsePart.stmt_list().isEmpty()) {
            currentSymbolTable = new LinkedHashMap<>();
            symbolTableStack.push(currentSymbolTable);
            scopeSymbolTables.put("BLOCK " + blockNum, currentSymbolTable);
            blockNum++;
        }
    }

    @Override
    public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
        symbolTableStack.pop();
        currentSymbolTable = symbolTableStack.peek();
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        currentSymbolTable = new LinkedHashMap<>();
        symbolTableStack.push(currentSymbolTable);
        scopeSymbolTables.put("BLOCK " + blockNum, currentSymbolTable);
        blockNum++;
    }

    @Override
    public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        symbolTableStack.pop();
        currentSymbolTable = symbolTableStack.peek();
    }

    @Override
    public void enterParam_decl(LittleParser.Param_declContext ctx) {
        String type = ctx.var_type().getText();
        String name = ctx.id().getText();
        String value = null;
        if(searchForKeyName(name)) {
            declError.add(name);
            return;
        }
        currentSymbolTable.put(name, new SymbolEntry(name, type, value));
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type = ctx.var_type().getText();
        String[] variableNames = ctx.id_list().getText().split(",");
        for (String variableName : variableNames) {
            String name = variableName.trim();
            String value = null;
            if(searchForKeyName(name)) {
                declError.add(name);
                return;
            }
            currentSymbolTable.put(name, new SymbolEntry(name, type, value));
        }
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {
        String value = ctx.str().getText();
        String name = ctx.id().getText();
        String type = "STRING";
        if(searchForKeyName(name)) {
            declError.add(name);
            return;
        }
        currentSymbolTable.put(name, new SymbolEntry(name, type, value));
    }
    public String getSymbolTableString() {
        final String SYMBOL_TABLE = "Symbol table ";
        StringBuilder result = new StringBuilder();
        for (String scopeName : scopeSymbolTables.keySet()) {
            result.append(SYMBOL_TABLE).append(scopeName).append("\n");
            Map<String, SymbolEntry> symbolTable = scopeSymbolTables.get(scopeName);
            symbolTable.values().forEach(symbol -> result.append(symbol.toString()));
            result.append("\n");
        }
        return result.toString();
    }
    public Boolean searchForKeyName(String newKey) {
        return currentSymbolTable.containsKey(newKey);
    }
    public String getDeclError() {
        if(declError.isEmpty()) {
            return null;
        } else return declError.get(0);
    }
}
class ResultContext {
    Boolean success = true;
    String content = null;
    Class<? extends InputStream> cl;

    public ResultContext(Class<? extends InputStream> isC) {
        cl = isC;
    }
    public ResultContext withSuccess(Boolean outcome) {
        this.success = outcome;
        return this;
    }
    public ResultContext withContent(String sb) {
        content = sb;
        return this;
    }
    public ResultContext displayIfApplicable() {
        if(cl.equals(BufferedInputStream.class)) {
            System.out.print(content);
        }
        return this;
    }
}