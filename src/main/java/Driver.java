import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class Driver implements TestHarness {

    public static void main(String[] args) {
        new Driver().step3(System.in);
    }

    public String step1(InputStream is) {

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
            return "";
        }
        return displayResult(is,computedResult.toString());
    }


    @Override
    public String step2(InputStream is) {
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
            return "Fatal Error";
        }
        return displayResult(is,result);
    }

    public String step3(InputStream is) {
        try {
            CommonTokenStream cts = new CommonTokenStream(new LittleLexer(new ANTLRInputStream(is)));
            LittleParser parse = new LittleParser(cts);
            parse.removeErrorListeners();
            parse.removeParseListeners();
            parse.addErrorListener(new LittleErrorListener());


            ParseTreeWalker walker = new ParseTreeWalker();
            CustomLittleListener listener = new CustomLittleListener();
            walker.walk(listener, parse.program());

            listener.printSymbolTable();
            return "Accepted";
        }
        catch (RuntimeException ex) {
            return "Not accepted";
        } catch (IOException ex) {
            return "Fatal Error";
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

class CustomLittleListener extends LittleBaseListener {
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
    }

    // Keeps track of order the variables have seen
    private Stack<Map<String, SymbolEntry>> symbolTableStack = new Stack<>();
    // Stores current symbols as they are seen (LinkedHashMap to store order)
    private Map<String, SymbolEntry> currentSymbolTable = new LinkedHashMap<>();
    // Saves what the stack has done as it pops scopes, scopSymbolTables is the resulting structure
    private Map<String, Map<String, SymbolEntry>> scopeSymbolTables = new LinkedHashMap<>();
    // Number that increments when a new scope has been entered
    private int blockNum = 1;

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
        currentSymbolTable.put(name, new SymbolEntry(name, type, value));
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type = ctx.var_type().getText();
        String[] variableNames = ctx.id_list().getText().split(",");
        for (String variableName : variableNames) {
            String name = variableName.trim();
            String value = null;
            currentSymbolTable.put(name, new SymbolEntry(name, type, value));
        }
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {
        String value = ctx.str().getText();
        String name = ctx.id().getText();
        String type = "STRING";
        currentSymbolTable.put(name, new SymbolEntry(name, type, value));
    }

    public void printSymbolTable() {
        String strTable = "Symbol table ";
        for (String scope : scopeSymbolTables.keySet()) {
            System.out.println(strTable + scope);
            Map<String, SymbolEntry> symbolTable = scopeSymbolTables.get(scope);

            for (SymbolEntry entry : symbolTable.values()) {

                // This will be changed to appropriate return output
                if (entry.getValue() == null)
                    System.out.println("name " + entry.getName() + " type " + entry.getType());
                else
                    System.out.println("name " + entry.getName() + " type " + entry.getType() + " value " + entry.getValue());
            }
            System.out.println();
        }
    }

}
