import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class Driver implements TestHarness {
    public static void main(String[] args) {
        new Driver().step2(System.in);
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