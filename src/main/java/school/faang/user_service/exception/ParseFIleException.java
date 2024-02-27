package school.faang.user_service.exception;

public class ParseFIleException extends RuntimeException {
    public ParseFIleException(String msg) {
        super(msg);
    }

    public static class SkillNotFoundException extends RuntimeException {
        public SkillNotFoundException(String message) {
            super(message);
        }
    }
}
