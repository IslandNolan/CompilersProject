import org.apache.commons.lang3.NotImplementedException;

import java.io.InputStream;

public interface TestHarness {
    default ResultContext step1(InputStream is) {
        throw new NotImplementedException();
    }
    default ResultContext step2(InputStream is) {
        throw new NotImplementedException();
    }
    default ResultContext step3(InputStream is) { throw new NotImplementedException(); }
    default ResultContext step4(InputStream is) {
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
