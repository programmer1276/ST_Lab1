package lab1.model;

public class Vial {
    private BabelFish fish;

    public Vial(BabelFish fish) {
        this.fish = fish;
    }

    public boolean hasFish() {
        return fish != null;
    }

    public BabelFish getFish() {
        return fish;
    }
}
