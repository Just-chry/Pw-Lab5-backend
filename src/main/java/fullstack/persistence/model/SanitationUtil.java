package fullstack.persistence.model;

import org.owasp.html.PolicyFactory;
import org.owasp.html.HtmlPolicyBuilder;

public class SanitationUtil {
    private static final PolicyFactory HTML_POLICY = new HtmlPolicyBuilder().toFactory();

    public static String sanitize(String input) {
        return HTML_POLICY.sanitize(input);
    }
}