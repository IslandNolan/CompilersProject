import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.HashSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class Driver implements TestHarness {
    public static void main(String[] args) {
        new Driver().step1(System.in);
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