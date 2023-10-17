import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.fail;

public class Tester {
    private static final String logMessage = "\t%s \t\t%s \t\t%s \t\t%s%n";
    private static Integer[] columnLengths = {20,20,12,5};
    private static Boolean failed = false;
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\033[0m";
    public static final String YELLOW = "\033[0;33m";


    @Test
    public void testStep() throws Exception, InvocationTargetException, IllegalAccessException {
        testStepX(2);
        if(failed) {
            System.out.println("\n"+RED+"One or more test cases have failed to validate. Please check your work. ");
            fail();
        }
    }

    public void testStepX(Integer stepNumber) throws Exception, InvocationTargetException, IllegalAccessException {
        Method stepMethod = null;

        try {
            System.out.printf("Starting Tests for Step %s..%n",stepNumber);
            stepMethod = Driver.class.getMethod(String.format("step%s",stepNumber), InputStream.class);
        }
        catch (NoSuchMethodException ex) {
            System.out.printf("Step %s is not defined in class %s%n, or does not implement TestHarness.java",stepNumber,Driver.class);
            System.exit(1);
        }

        Map<String,ImmutablePair<String,String>> testCases = getInputOutputMatches(stepNumber);

        if(testCases.isEmpty()) {
            System.out.println("\tNo Test cases found, ensure inputs are present in /src/main/resources/stepX/inputs/... ");
            return;
        }

        ArrayList<String> results = new ArrayList<>(testCases.size());

        for(String testCase : testCases.keySet()) {
            try {
                String outputContents = testCases.get(testCase).getRight();
                if(Objects.isNull(outputContents)) {
                    //New Exception, IOException since cannot read the output.
                    throw new IOException("Unable to read file contents of: %s");
                }
                else {

                    //Continue with Test Case, store result in testCaseResult.
                    outputContents = FileUtils.readFileToString(new File(outputContents), StandardCharsets.UTF_8).trim();
                    String testCaseResult = ((String) stepMethod.invoke(new Driver(),
                            FileUtils.openInputStream(FileUtils.getFile(testCases.get(testCase).getLeft())))).trim();

                    String[] formattedColumns = formatColumnOutputs(' ',testCase,testCases.get(testCase).getLeft(),testCaseResult,"");
                    boolean passed = outputContents.equalsIgnoreCase(testCaseResult);
                    if(!passed) {
                        failed = true;
                        formattedColumns[3] = RED + Boolean.toString(passed) + RESET;
                    } else formattedColumns[3] = Boolean.toString(passed);
                    results.add(String.format(logMessage, formattedColumns));

                }
            }
            catch (NotImplementedException | IOException ex) {
                results.add(YELLOW + String.format(logMessage,formatColumnOutputs(' ',testCase,"Not found.. ","......","......"))+RESET);
            }
        }

        System.out.printf(logMessage,formatColumnOutputs(' ',"Test Name","Path","Match","Result"));
        System.out.printf(logMessage,formatColumnOutputs('-',"","","",""));
        for(String s : results) {
            System.out.print(s);
        }
    }

    static Map<String, ImmutablePair<String,String>> getInputOutputMatches(Integer stepNumber) {
        final String discoveredDirectory = String.format(System.getProperty("user.dir")+"/src/main/resources/step%s/%s",stepNumber,"%s");
        final String inputDirectory = String.format(discoveredDirectory,"inputs");
        final String outputDirectory = String.format(discoveredDirectory,"outputs");

        TreeMap<String, ImmutablePair<String,String>> files = new TreeMap<>(
                (o1, o2) -> {
                    Integer a = Integer.parseInt(o1.replaceAll("\\D",""));
                    Integer b = Integer.parseInt(o2.replaceAll("\\D",""));
                    return a.compareTo(b);
                }
        );

        for (Iterator<File> it = FileUtils.iterateFiles(new File(inputDirectory), null, false); it.hasNext(); ) {
            File f = it.next();

            //Attempt to locate a matching file output.
            int testCaseNumber = Integer.parseInt(f.getName().replaceAll("\\D", ""));
            try {
                File output = FileUtils.streamFiles(new File(outputDirectory),false, null)
                        .filter(file -> testCaseNumber == Integer.parseInt(file.getName().replaceAll("\\D","")))
                        .findFirst().orElseThrow(Exception::new);
                files.put(f.getName(),new ImmutablePair<>(f.getAbsolutePath(), output.getAbsolutePath()));
            }
            catch (Exception ex) {
                files.put(f.getName(),new ImmutablePair<>(f.getAbsolutePath(),null));
            }
        }
        System.out.printf("\tIdentified %s test cases..%n",files.size());
        return files;
    }

    static String[] formatColumnOutputs(Character filler, String... args) {
        String[] transformedArguments = new String[args.length];
        for(int i=0;i<args.length;i++) {
            StringBuilder s = new StringBuilder(args[i]);

            while(s.length()<columnLengths[i]) {
                s.append(filler);
            }

            //If difference is greater than 10, then set the balance value to the size
            if((s.length()-columnLengths[i] > 10)) { columnLengths[i] = s.length(); }
            transformedArguments[i]=s.toString();
        }
        return transformedArguments;
    }
}
