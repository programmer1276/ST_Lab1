package lab1.model;

import java.util.List;

public class Arthur {
    private int confidence = 50;
    private String eyeState = "OPEN";

    public void blink() { this.eyeState = "BLINKING"; }

    public String getEyeState() { return eyeState; }

    public void observe(List<PhysicalObject> objects) {
        for (PhysicalObject obj : objects) {
            if (obj.getFamiliarity() == Familiarity.FAMILIAR) {
                confidence += 20;
            } else {
                confidence -= 10;
            }
        }
    }

    public int getConfidence() { return confidence; }
}