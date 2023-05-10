package bgpersonnel.budget.budget;

public enum BudgetType {
    ANNUEL("Annuel"),
    MENSUEL("Mensuel"),
    UNIQUE("Unique");

    private final String name;

    BudgetType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
