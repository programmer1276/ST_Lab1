package lab1.model;

import java.util.List;

public class Ford {
    private Vial vial;

    public Ford(Vial vial) {
        this.vial = vial;
    }

    public void offerAction(Arthur a) {
        // Предложение засунуть рыбку в ухо — это "чуждое" событие для Артура
        PhysicalObject proposal = new PhysicalObject("Fish in ear proposal", Familiarity.ALIEN);
        a.observe(List.of(proposal));
    }

    public Vial getVial() {
        return vial;
    }
}
