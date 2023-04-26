package bgpersonnel.budget.reporting;

public enum ETypeReport {
    CSV("csv"),
    PDF("pdf"),
    XLS("xls");

    private String type;


    ETypeReport(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
