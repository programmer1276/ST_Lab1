package lab1.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArthurAdventureTest {

    @Test
    @DisplayName("Тестирование состояний Артура: моргание и уверенность")
    void testArthurStates() {
        Arthur arthur = new Arthur();

        assertEquals("OPEN", arthur.getEyeState());
        assertEquals(50, arthur.getConfidence());


        arthur.blink();
        assertEquals("BLINKING", arthur.getEyeState());
    }

    @Test
    @DisplayName("Тестирование реакции Артура на разные типы объектов")
    void testArthurObservation() {
        Arthur arthur = new Arthur();

        // 1. Тест на знакомый объект (Пакет хлопьев)
        PhysicalObject familiar = new PhysicalObject("Cornflakes", Familiarity.FAMILIAR);
        assertEquals("Cornflakes", familiar.getName()); // Покрытие getName()
        assertEquals(Familiarity.FAMILIAR, familiar.getFamiliarity()); // Покрытие getFamiliarity()

        arthur.observe(Collections.singletonList(familiar));
        assertEquals(70, arthur.getConfidence(), "Уверенность должна вырасти на 20");

        // 2. Тест на чуждый объект (Матрац)
        PhysicalObject alien = new PhysicalObject("Mattress", Familiarity.ALIEN);
        arthur.observe(Collections.singletonList(alien));
        assertEquals(60, arthur.getConfidence(), "Уверенность должна упасть на 10");

        // 3. Тест на пустой список (граничный случай)
        arthur.observe(Collections.emptyList());
        assertEquals(60, arthur.getConfidence(), "Уверенность не должна измениться");
    }

    @Test
    @DisplayName("Тестирование Рыбки и Флакончика")
    void testFishAndVial() {
        BabelFish fish = new BabelFish();

        // Проверка рыбки
        assertTrue(fish.isGlowing());
        fish.setGlowing(false); // Покрытие setter
        assertFalse(fish.isGlowing());

        // Проверка флакончика
        Vial vial = new Vial(fish);
        assertTrue(vial.hasFish());
        assertEquals(fish, vial.getFish()); // Покрытие getFish()

        // Проверка пустого флакончика
        Vial emptyVial = new Vial(null);
        assertFalse(emptyVial.hasFish());
    }

    @Test
    @DisplayName("Тестирование Форда и его взаимодействия с Артуром")
    void testFordAndInteraction() {
        BabelFish fish = new BabelFish();
        Vial vial = new Vial(fish);
        Ford ford = new Ford(vial);

        // Проверка владения флаконом
        assertEquals(vial, ford.getVial());

        // Проверка влияния предложения Форда на Артура
        Arthur arthur = new Arthur();
        int initialConf = arthur.getConfidence();

        ford.offerAction(arthur);

        assertEquals(initialConf - 10, arthur.getConfidence(),
                "Предложение Форда (чуждое действие) должно снизить уверенность");
    }

    @Test
    @DisplayName("Комплексный тест: Артур в окружении множества предметов")
    void testComplexEnvironment() {
        Arthur arthur = new Arthur();
        List<PhysicalObject> environment = Arrays.asList(
                new PhysicalObject("Dentrassi underwear", Familiarity.ALIEN),
                new PhysicalObject("Squornshellous mattress", Familiarity.ALIEN),
                new PhysicalObject("Cornflakes", Familiarity.FAMILIAR)
        );

        arthur.observe(environment);

        // Расчет: 50 (база) - 10 (белье) - 10 (матрац) + 20 (хлопья) = 50
        assertEquals(50, arthur.getConfidence());
    }
}
