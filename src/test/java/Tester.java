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
import java.util.function.Predicate;

import static org.junit.Assert.fail;

public class Tester {
    private static final String logMessage = "\t%s \t\t%s \t\t%s \t\t%s%n";
    private static Integer[] columnLengths = {20,20,20,20};
    private static Boolean failed = false;
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\033[0m";
    public static final String YELLOW = "\033[0;33m";
    private static final String errorMessage = "\n"+RED+"One or more test cases have failed to validate. Please check your work. ";
    @Test
    public void testStep1() throws Exception {
        testStepX(1);
        if(failed) {
            System.out.println(errorMessage);
            fail();
        }
    }
    @Test
    public void testStep2() throws Exception {
        testStepX(2);
        if(failed) {
            System.out.println(errorMessage);
            fail();
        }
    }
    @Test
    public void testStep3() throws Exception {
        testStepX(3);
        if(failed) {
            System.out.println(errorMessage);
            fail();
        }
    }
    @Test
    public void testStep4() throws Exception {
        testStepX(4);
        if(failed) {
            System.out.println(errorMessage);
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

        Map<String,ImmutablePair<String,String>> testCases = null;
        try {
            testCases = getInputOutputMatches(stepNumber);
        }
        catch (Exception ignored) {}

        if(Objects.isNull(testCases) || testCases.isEmpty()) {
            System.out.println("\tNo Test cases found, ensure inputs are present in /src/main/resources/stepX/inputs/... ");
            return;
        }

        ArrayList<String> results = new ArrayList<>(testCases.size());

        //Do custom comparator here to organize outputs.

        for(String testCase : testCases.keySet()) {
            try {
                String outputContents = testCases.get(testCase).getRight();
                if(Objects.isNull(outputContents)) {
                    //New Exception, IOException since cannot read the output.
                    throw new IOException("Unable to read file contents of: %s");
                }
                else {

                    //Continue with Test Case, store result in testCaseResult, also handle carriage returns.
                    outputContents = FileUtils.readFileToString(new File(outputContents), StandardCharsets.UTF_8).replace("\r\n", "\n").trim();

                    ResultContext rs = ((ResultContext) stepMethod.invoke(new Driver(),
                            FileUtils.openInputStream(FileUtils.getFile(testCases.get(testCase).getLeft()))));

                    Boolean testSuccess = rs.success;
                    String testCaseResult;
                    if (Objects.isNull(rs.content) || !testSuccess) {
                        //fail
                        testCaseResult = "FAIL";
                    }else testCaseResult = rs.content.trim();

                    Object[] formattedColumns = formatColumnOutputs(' ', testCase, testCases.get(testCase).getLeft(), testCaseResult, "");
                    boolean passed = outputContents.equalsIgnoreCase(testCaseResult);

                    if (!passed) {
                        formattedColumns[3] = RED + "x" + RESET;
                    } else formattedColumns[3] = "✓";
                    results.add(String.format(logMessage,formattedColumns));
                }
            }
            catch (NotImplementedException | IOException ex) {
                results.add(YELLOW + String.format(logMessage, (Object[]) formatColumnOutputs(' ',testCase,"Not found.. ","......","......"))+RESET);
            }
        }

        System.out.printf(logMessage, (Object[]) formatColumnOutputs(' ',"Test Name","Path","Result","Passed"));
        System.out.printf(logMessage, (Object[]) formatColumnOutputs('-',"","","",""));
        for(String s : results) {
            System.out.print(s);
        }
        System.out.println();
    }
    static Map<String, ImmutablePair<String,String>> getInputOutputMatches(Integer stepNumber) {
        final String discoveredDirectory = String.format(System.getProperty("user.dir")+"/src/main/resources/step%s/%s",stepNumber,"%s");
        final String inputDirectory = String.format(discoveredDirectory,"inputs");
        final String outputDirectory = String.format(discoveredDirectory,"outputs");

        HashMap<String,ImmutablePair<String,String>> files = new HashMap<>();

        //Sort the key-set based on a custom comparator later. Last enhancement for readability.

        for (Iterator<File> it = FileUtils.iterateFiles(new File(inputDirectory), null, false); it.hasNext(); ) {
            File f = it.next();
            Predicate<File> pred = new Predicate<File>() {
                @Override
                public boolean test(File file) {
                    if(f.getName().matches("\\d")) {
                        //Trying to match the case #s
                        return Integer.parseInt(f.getName().replaceAll("\\D", ""))
                                == Integer.parseInt(file.getName().replaceAll("\\D",""));
                    }
                    else {
                        String realName = f.getName().split("\\.")[0];
                        return file.getName().split("\\.")[0].equalsIgnoreCase(realName);
                    }
                }
            };

            try {
                File output = FileUtils.streamFiles(new File(outputDirectory),false, null)
                        .filter(pred)
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

    //This will break if delta of the length of the filename is greater than 10, but I'm lazy :shrug:
    static String[] formatColumnOutputs(Character filler, String... args) {
        String[] transformedArguments = new String[args.length];
        for(int i=0;i<args.length;i++) {
            StringBuilder s = new StringBuilder(args[i]);

            //If difference is greater than 10, then set the balance value to the size
            if((s.length()-columnLengths[i] > 10)) { columnLengths[i] = s.length() + 15; }
            if(columnLengths[i]>30 && i==2) {
                //Make sure the output doesn't run away (example step 1)
                columnLengths[i] = 30;
                if(s.length() > 27) {
                    s.delete(20,s.length());
                    s.append("...");
                }
            }

            while(s.length()<columnLengths[i]) {
                s.append(filler);
            }

            //Do not print the newline characters in the output
            transformedArguments[i]=s.toString().replaceAll("\n",".");
        }
        return transformedArguments;
    }
}
