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
            stepMethod = Driver.class.getMethod(String.format("step%s",stepNumber), InputStream.class);
        }
        catch (NoSuchMethodException ex) {
            System.out.println("Unable to locate method step%s in class "+Driver.class.toString());
            System.exit(1);
        }
        for(int i=1;i<22;i++) {
            File input = new File(String.format(inputFileName,stepNumber,INPUTS,i));
            File expectedOutput = new File(input.getAbsolutePath().replace(INPUTS,OUTPUTS).replace(".micro",".out"));
            if(input.exists() && expectedOutput.exists()) {
                FileInputStream fis = new FileInputStream(input);
                String expectedResult = new Scanner(expectedOutput).nextLine();
                String actualResult = stepMethod.invoke(null,fis).toString();
                while(actualResult.length()<20) { actualResult+=' '; }
                System.out.println(String.format("%s \t-> %s  \t: %s", input.getAbsolutePath()
                        , actualResult, expectedResult.equalsIgnoreCase(actualResult.trim())));
            }
            else {
                System.out.println(input.getAbsolutePath() + " has no matching output.. ");
            }
        }
    }
}
