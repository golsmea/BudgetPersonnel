package bgpersonnel.budget.exeception;

public class GenerationRapportException extends RuntimeException {
    public GenerationRapportException(String message) {
        super(message);
    }

    public GenerationRapportException(String message, Throwable cause) {
        super(message, cause);
    }
}
