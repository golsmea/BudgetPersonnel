package bgpersonnel.budget.transaction;

public enum TypeTransaction {
    DEPENSE("Dépense"),
    REVENU("Revenu");
    private final String name;

    TypeTransaction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
