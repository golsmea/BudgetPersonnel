package bgpersonnel.budget.exeception;

public class TypeRapportNotFoundException extends RuntimeException{
    public TypeRapportNotFoundException(String message) {
        super(message);
    }

    public TypeRapportNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
