import java.io.BufferedInputStream;
import java.io.InputStream;

public class ResultContext {
    Boolean success = true;
    String content = null;
    Class<? extends InputStream> cl;

    public ResultContext(Class<? extends InputStream> isC) {
        cl = isC;
    }
    public ResultContext withSuccess(Boolean outcome) {
        this.success = false;
        return this;
    }
    public ResultContext withContent(String sb) {
        content = sb;
        return this;
    }
    public ResultContext displayIfApplicable() {
        if(cl.equals(BufferedInputStream.class)) {
            System.out.print(content);
        }
        return this;
    }
}
