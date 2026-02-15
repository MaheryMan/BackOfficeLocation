package exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException() {
        super("Unauthorized: Invalid or expired token");
    }
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
