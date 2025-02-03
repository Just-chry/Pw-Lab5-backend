package fullstack.rest.model;

public class VerifyCodeRequest {
    private String verificationCode;

    public VerifyCodeRequest() {}

    public VerifyCodeRequest(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
