package fullstack.util;

import java.util.regex.Pattern;

public class ContactValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PHONE_REGEX = "^\\+[0-9]{10,15}$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
    private static final Pattern phonePattern = Pattern.compile(PHONE_REGEX);

    public static boolean isValidEmail(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phonePattern.matcher(phone).matches();
    }

    public static String formatPhone(String phone) {
        if (phone != null && !phone.startsWith("+39")) {
            return "+39" + phone;
        }
        return phone;
    }
}
