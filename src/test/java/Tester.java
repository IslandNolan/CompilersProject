import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class Tester {
    @Test
    public void test() throws IOException, InvocationTargetException, IllegalAccessException {
        testStepX(2);
    }

    public void testStepX(Integer stepNumber) throws IOException, InvocationTargetException, IllegalAccessException {
        final String inputFileName = "src/main/java/step%s/%s/test%s.micro";
        final String INPUTS = "inputs";
        final String OUTPUTS = "outputs";
        Method stepMethod = null;

        try {
            System.out.printf("Starting Tests for Step %s..%n",stepNumber);
            stepMethod = Driver.class.getMethod(String.format("step%s",stepNumber), InputStream.class);
        }
        catch (NoSuchMethodException ex) {
            System.out.printf("Step %s is not defined in class %s%n",stepNumber,Driver.class);
            System.exit(1);
        }
        for(int i=1;i<22;i++) {
            File input = new File(String.format(inputFileName,stepNumber,INPUTS,i));
            File expectedOutput = new File(input.getAbsolutePath().replace(INPUTS,OUTPUTS).replace(".micro",".out"));
            if(input.exists() && expectedOutput.exists()) {
                FileInputStream fis = new FileInputStream(input);
                String expectedResult = new Scanner(expectedOutput).nextLine();
                StringBuilder actualResult = new StringBuilder(stepMethod.invoke(null, fis).toString());
                while(actualResult.length()<20) { actualResult.append(' '); }
                System.out.printf("%s \t-> %s  \t: %s%n", input.getAbsolutePath()
                        , actualResult.toString(), expectedResult.equalsIgnoreCase(actualResult.toString().trim()));
            }
            else {
                System.out.println(input.getAbsolutePath() + " has no matching output.. ");
            }
        }
    }
}
