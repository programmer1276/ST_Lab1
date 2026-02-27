package lab1.model;

public class PhysicalObject {
    private String name;
    private Familiarity familiarity;

    public PhysicalObject(String name, Familiarity familiarity) {
        this.name = name;
        this.familiarity = familiarity;
    }

    public Familiarity getFamiliarity() {
        return familiarity;
    }

    public String getName() {
        return name;
    }
}
