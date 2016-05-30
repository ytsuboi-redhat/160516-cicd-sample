package cicd.sandbox.exception;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/05/30
 */
public class TargetNotFoundException extends RuntimeException {

    public TargetNotFoundException(Throwable e) {
        super(e);
    }

}
