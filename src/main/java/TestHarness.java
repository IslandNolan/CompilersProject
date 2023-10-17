import org.apache.commons.lang3.NotImplementedException;

import java.io.InputStream;

public interface TestHarness {
    default String step1(InputStream is) {
        throw new NotImplementedException();
    }
    default String step2(InputStream is) {
        throw new NotImplementedException();
    }
    default String step3(InputStream is){
        throw new NotImplementedException();
    }
    default String step4(InputStream is) {
        throw new NotImplementedException();
    }

    default String displayResult(InputStream is, String result) {

        //This is necessary to retain compatibility with Dr. Kahanda's Testing methods.
        if(is.equals(System.in)) {
            System.out.println(result);
        }
        return result;
    }
}
