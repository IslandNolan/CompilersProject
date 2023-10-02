import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Tester {
    @Test
    public void startTestStep2() throws IOException {

        final String inputFileName = "src/main/java/step2/%s/test%s.micro";
        final String INPUTS = "inputs";
        final String OUTPUTS = "outputs";

        for(int i=1;i<22;i++) {
            File input = new File(String.format(inputFileName,INPUTS,i));
            File result = new File(input.getAbsolutePath().replace(INPUTS,OUTPUTS).replace(".micro",".out"));
            String res = new Scanner(result).nextLine();

            if(input.exists() && result.exists()) {
                FileInputStream fis = new FileInputStream(input);
                CommonTokenStream cts = new CommonTokenStream(new LittleLexer(new ANTLRInputStream(fis)));
                cts.fill();
                LittleParser parse = new LittleParser(cts);
                parse.removeErrorListeners();
                parse.removeParseListeners();
                parse.addErrorListener(new LittleErrorListener());

                try {
                    parse.program();
                    System.out.print(input.getAbsolutePath()+": \tAccepted     \t");
                    if(res.equalsIgnoreCase("Accepted")) {
                        System.out.println("[✓]");
                    } else System.out.println("[x]");
                }
                catch (Exception ex) {
                    System.out.print(input.getAbsolutePath()+": \tNot accepted\t");
                    if(res.equalsIgnoreCase("Not accepted")) {
                        System.out.println("[✓]");
                    } else System.out.println("[x]");
                }
            }
        }
    }
}
