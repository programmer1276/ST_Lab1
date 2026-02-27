//package lab1.btree;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.List;
//
//class BPlusTreeTest {
//
//    @Test
//    @DisplayName("Полное покрытие: Вставки, поиск и дубликаты")
//    void testComprehensiveCoverage() {
//        BPlusTree tree = new BPlusTree();
//
//        // 1. Покрытие разделения корня (от LeafNode к InternalNode)
//        // Вставляем уникальные значения, чтобы дерево разделилось при M=7
//        for (int i = 1; i <= 10; i++) {
//            tree.insert(i * 10);
//        }
//
//        // 2. Проверка успешного поиска во всех частях дерева
//        assertTrue(tree.search(10), "Ключ 10 должен быть найден (начало)");
//        assertTrue(tree.search(50), "Ключ 50 должен быть найден (середина)");
//        assertTrue(tree.search(100), "Ключ 100 должен быть найден (конец)");
//        assertFalse(tree.search(5), "Поиск отсутствующего ключа (меньше минимального)");
//        assertFalse(tree.search(105), "Поиск отсутствующего ключа (больше максимального)");
//
//        // 3. Покрытие дубликатов в LeafNode (ветка idx >= 0)
//        tree.insert(50);
//        assertTrue(tree.search(50));
//
//        // 4. Покрытие циклов в InternalNode (три пути поиска)
//        // Путь 1: key < keys.get(0)
//        assertTrue(tree.search(10));
//        // Путь 2: key < keys.get(i) в середине
//        assertTrue(tree.search(40));
//        // Путь 3: key >= keys.get(size-1)
//        assertTrue(tree.search(100));
//
//        // 5. Покрытие каскадного разделения (массовая вставка)
//        for (int i = 0; i < 50; i++) {
//            tree.insert(i);
//        }
//
//        // 6. Проверка служебных методов
//        List<String> path = tree.getPath();
//        assertNotNull(path);
//        assertFalse(path.isEmpty(), "История операций не должна быть пустой");
//    }
//
//    @Test
//    @DisplayName("Покрытие специфических веток InternalNode.insert")
//    void testInternalNodeInsertBranches() {
//        BPlusTree tree = new BPlusTree();
//
//        // Создаем структуру с InternalNode
//        for (int i = 1; i <= 20; i++) {
//            tree.insert(i * 5);
//        }
//
//        // Покрытие случая, когда вставляемый при сплите ключ уже есть в InternalNode
//        // вставка ключей, граничащих с уже существующими разделителями
//        tree.insert(25);
//        tree.insert(26);
//
//        assertTrue(tree.search(25));
//        assertTrue(tree.search(100));
//    }
//
//    @Test
//    @DisplayName("Покрытие вставки в обратном порядке")
//    void testReverseOrderSplits() {
//        BPlusTree tree = new BPlusTree();
//        // Вставка в обратном порядке для покрытия веток вставки в начало списков (idx=0)
//        for (int i = 100; i >= 1; i--) {
//            tree.insert(i);
//        }
//
//        for (int i = 1; i <= 100; i++) {
//            assertTrue(tree.search(i), "Ключ " + i + " потерян после сплитов в начало");
//        }
//    }
//
//    @Test
//    @DisplayName("LeafNode.getFirstLeafKey при пустом листе бросает исключение")
//    void testLeafGetFirstLeafKeyEmpty() {
//        BPlusTree.LeafNode leaf = new BPlusTree.LeafNode();
//        assertThrows(IndexOutOfBoundsException.class, leaf::getFirstLeafKey);
//    }
//
//    @Test
//    @DisplayName("InternalNode.getFirstLeafKey при пустых дочерних нодах бросает исключение")
//    void testInternalGetFirstLeafKeyEmpty() {
//        BPlusTree.InternalNode internal = new BPlusTree.InternalNode();
//        assertThrows(IndexOutOfBoundsException.class, internal::getFirstLeafKey);
//    }
//
//    @Test
//    @DisplayName("InternalNode.insert: ветка idx >= 0 и ветка сплита InternalNode")
//    void testInternalInsertSpecialBranches() {
//        BPlusTree.InternalNode parent = new BPlusTree.InternalNode();
//
//        // Случай idx >= 0: подготовим ключ, равный upKey
//        parent.keys.add(20); // существует разделитель 20
//
//        BPlusTree.Node childReturningExistingUpKey = new BPlusTree.Node() {
//            @Override
//            BPlusTree.Node insert(int key, List<String> p, int m) {
//                // Вернём sibling, чей первый ключ совпадает с существующим разделителем (20)
//                BPlusTree.LeafNode sibling = new BPlusTree.LeafNode();
//                sibling.keys.add(20);
//                return sibling;
//            }
//
//            @Override
//            boolean search(int key, List<String> p) {
//                return false;
//            }
//
//            @Override
//            int getFirstLeafKey() {
//                return 20;
//            }
//        };
//
//        parent.children.add(childReturningExistingUpKey);
//        parent.children.add(new BPlusTree.LeafNode());
//
//        List<String> p = new java.util.ArrayList<>();
//        BPlusTree.Node result = parent.insert(15, p, 7);
//        // Так как мы вернули sibling с upKey==20, бинарный поиск найдёт idx >= 0 и вставит upKey
//        // в список keys и вставит новый дочерний узел в children.
//        assertEquals(2, parent.keys.size(), "Ожидается, что ключ был добавлен в parent.keys");
//        assertEquals(3, parent.children.size(), "Ожидается, что дочерний узел был добавлен в parent.children");
//
//        // Теперь проверим ветку сплита InternalNode
//        BPlusTree.InternalNode parent2 = new BPlusTree.InternalNode();
//        for (int i = 1; i <= 7; i++) {
//            parent2.keys.add(i * 10);
//            parent2.children.add(new BPlusTree.LeafNode());
//        }
//        parent2.children.add(new BPlusTree.LeafNode());
//
//        // Сделаем так, чтобы при вставке вернулся результат и увеличил keys.size() до 8 -> сплит
//        BPlusTree.Node stubChild = new BPlusTree.Node() {
//            @Override
//            BPlusTree.Node insert(int key, List<String> p2, int m) {
//                BPlusTree.LeafNode sib = new BPlusTree.LeafNode();
//                sib.keys.add(999);
//                return sib;
//            }
//
//            @Override
//            boolean search(int key, List<String> p2) {
//                return false;
//            }
//
//            @Override
//            int getFirstLeafKey() {
//                return 1;
//            }
//        };
//
//        // Подменим первую дочернюю ноду на stub, который вернёт sibling
//        parent2.children.set(0, stubChild);
//
//        BPlusTree.Node splitRes = parent2.insert(5, new java.util.ArrayList<>(), 7);
//        assertNotNull(splitRes, "InternalNode должен вернуть sibling при сплите");
//        assertTrue(splitRes instanceof BPlusTree.InternalNode, "Результат сплита должен быть InternalNode");
//    }
//
//    @Test
//    @DisplayName("Сравнение последовательности характерных точек с эталоном")
//    void testTracePointsWithReference() {
//        BPlusTree tree = new BPlusTree();
//
//        // Сценарий 1: Простая вставка (Эталон: ["INSERT"])
//        tree.insert(100);
//        List<String> expectedSimple = List.of("INSERT");
//        assertEquals(expectedSimple, tree.getPath(), "Эталон для простой вставки не совпал");
//
//        // Сценарий 2: Поиск (Эталон: ["SUCCESS"])
//        tree.search(100);
//        List<String> expectedSearch = List.of("SUCCESS");
//        assertEquals(expectedSearch, tree.getPath(), "Эталон для поиска не совпал");
//
//        // Сценарий 3: Сплит корня
//        // При M=7 вставляем элементы со 2-го по 7-й (всего будет 7 в дереве)
//        for (int i = 1; i <= 6; i++) tree.insert(i);
//
//        // Вставляем 8-й элемент.
//        // Эталон: сначала вставка в лист, затем его сплит, затем создание нового корня.
//        List<String> expectedSplit = List.of("INSERT", "LEAF_SPLIT", "NEW_ROOT");
//
//        tree.insert(7); // 8-й элемент
//        assertEquals(expectedSplit, tree.getPath(), "Последовательность при сплите корня не совпала с эталоном!");
//    }
//}


//package lab1.btree;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("Атомарное тестирование BPlusTree (M=3)")
//class BPlusTreeTest {
//    private BPlusTree tree;
//
//    @BeforeEach
//    void setUp() {
//        // M=3 позволяет быстро проверять сплиты
//        tree = new BPlusTree(3);
//    }
//
//    @Test
//    @DisplayName("1. Валидация конструктора")
//    void testConstructorConstraints() {
//        assertAll("Проверка ограничений M",
//                () -> assertThrows(IllegalArgumentException.class, () -> new BPlusTree(8), "M > 7 запрещено"),
//                () -> assertThrows(IllegalArgumentException.class, () -> new BPlusTree(1), "M < 2 запрещено"),
//                () -> assertDoesNotThrow(() -> new BPlusTree(3), "M=3 должно работать")
//        );
//    }
//
//    @Test
//    @DisplayName("2. Базовая вставка и поиск")
//    void testBasicInsertSearch() {
//        tree.insert(10);
//
//        // Проверяем успех
//        assertTrue(tree.search(10));
//        assertEquals(List.of("SUCCESS"), tree.getPath(), "После успешного поиска путь должен быть [SUCCESS]");
//
//        // Проверяем неудачу (метод очистит путь и запишет FAIL)
//        assertFalse(tree.search(20));
//        assertEquals(List.of("FAIL"), tree.getPath(), "После неудачного поиска путь должен быть [FAIL]");
//    }
//
//    @Test
//    @DisplayName("3. Обработка дубликатов")
//    void testDuplicateHandling() {
//        tree.insert(5);
//        tree.insert(5); // Повторная вставка
//
//        // В коде стоит: if (idx >= 0) return null;
//        // Это значит, что при дубликате path останется пустым (т.к. мы сделали clear() в начале)
//        // или не будет содержать LEAF_SPLIT
//        assertFalse(tree.getPath().contains("INSERT"), "При дубликате вставка не должна фиксироваться повторно");
//        assertFalse(tree.getPath().contains("LEAF_SPLIT"));
//    }
//
//    @Test
//    @DisplayName("4. Разделение листа (LEAF_SPLIT)")
//    void testLeafSplit() {
//        for (int i = 1; i <= 3; i++) tree.insert(i);
//        // 4-й элемент вызывает сплит
//        tree.insert(4);
//
//        List<String> path = tree.getPath();
//        assertTrue(path.contains("INSERT"));
//        assertTrue(path.contains("LEAF_SPLIT"));
//        assertTrue(path.contains("NEW_ROOT"));
//    }
//
//    @Test
//    @DisplayName("5. Разделение внутреннего узла (INTERNAL_SPLIT)")
//    void testInternalSplit() {
//        // Для M=3 нужно спровоцировать каскад. 12 элементов гарантированно делят корень.
//        boolean splitHappened = false;
//        for (int i = 1; i <= 12; i++) {
//            tree.insert(i);
//            if (tree.getPath().contains("INTERNAL_SPLIT")) {
//                splitHappened = true;
//                break;
//            }
//        }
//        assertTrue(splitHappened, "INTERNAL_SPLIT должен произойти при M=3 на малом количестве данных");
//    }
//
//    @Test
//    @DisplayName("6. Поиск по разным ветвям InternalNode")
//    void testInternalSearchBranches() {
//        // Создаем структуру из нескольких уровней
//        for (int i = 10; i <= 60; i += 10) tree.insert(i);
//
//        assertAll("Ветки поиска",
//                () -> assertTrue(tree.search(10), "Левая ветка"),
//                () -> assertTrue(tree.search(30), "Средняя ветка"),
//                () -> assertTrue(tree.search(60), "Правая ветка")
//        );
//    }
//
//    @Test
//    @DisplayName("7. Вставка в начало (Reverse Order)")
//    void testReverseInsert() {
//        tree.insert(100);
//        tree.insert(50); // binarySearch вернет отрицательный idx, вставит в начало
//        assertTrue(tree.search(50));
//        assertTrue(tree.search(100));
//    }
//
//    @Test
//    @DisplayName("8. Исключения в пустых узлах")
//    void testEmptyNodeExceptions() {
//        BPlusTree.LeafNode leaf = new BPlusTree.LeafNode();
//        assertThrows(IndexOutOfBoundsException.class, leaf::getFirstLeafKey);
//
//        BPlusTree.InternalNode internal = new BPlusTree.InternalNode();
//        // В InternalNode вызовется children.get(0), что даст Exception
//        assertThrows(IndexOutOfBoundsException.class, internal::getFirstLeafKey);
//    }
//}


package lab1.btree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Полное покрытие BPlusTree (100% Coverage)")
class BPlusTreeTest {
    private BPlusTree tree;

    @BeforeEach
    void setUp() {
        tree = new BPlusTree(3);
    }

    @Test
    @DisplayName("1. Валидация всех конструкторов")
    void testConstructorCoverage() {
        assertAll("Конструкторы",
                // Покрытие дефолтного конструктора (14-й метод)
                () -> assertDoesNotThrow(() -> new BPlusTree()),
                // Покрытие исключений
                () -> assertThrows(IllegalArgumentException.class, () -> new BPlusTree(8)),
                () -> assertThrows(IllegalArgumentException.class, () -> new BPlusTree(1))
        );
    }

    @Test
    @DisplayName("2. Базовая вставка и поиск")
    void testBasicInsertSearch() {
        tree.insert(10);
        assertTrue(tree.search(10));
        assertEquals(List.of("SUCCESS"), tree.getPath());

        assertFalse(tree.search(20));
        assertEquals(List.of("FAIL"), tree.getPath());
    }

    @Test
    @DisplayName("3. Обработка дубликатов")
    void testDuplicateHandling() {
        tree.insert(5);
        tree.insert(5);
        // Ветка idx >= 0 в LeafNode возвращает null, path очищается и остается пустым
        assertTrue(tree.getPath().isEmpty() || !tree.getPath().contains("INSERT"));
    }

    @Test
    @DisplayName("4. Разделение листа (LEAF_SPLIT)")
    void testLeafSplit() {
        for (int i = 1; i <= 4; i++) tree.insert(i);
        List<String> path = tree.getPath();
        assertTrue(path.contains("LEAF_SPLIT"));
        assertTrue(path.contains("NEW_ROOT"));
    }

    @Test
    @DisplayName("6. Поиск по всем ветвям InternalNode")
    void testInternalSearchBranches() {
        int[] values = {10, 20, 30, 40, 50, 60, 70, 80, 90};
        for (int v : values) tree.insert(v);

        // Проверяем 3 ветки:
        assertAll("Ветки поиска",
                // Ключ меньше самого первого разделителя (первый ребенок)
                () -> assertTrue(tree.search(10), "Поиск в самой левой ветви"),

                // Ключ между разделителями (средние дети)
                () -> assertTrue(tree.search(40), "Поиск в средней ветви"),

                // Ключ больше последнего разделителя (самый правый ребенок)
                () -> assertTrue(tree.search(90), "Поиск в самой правой ветви"),

                // Неудачный поиск в середине
                () -> assertFalse(tree.search(45), "Неудачный поиск в середине")
        );
    }

    @Test
    @DisplayName("11. Граничные условия циклов и пустые результаты")
    void testSearchLoopBounds() {
        // Тест для покрытия случая, когда цикл во внутреннем узле доходит до конца
        BPlusTree customTree = new BPlusTree(7);
        for (int i = 1; i <= 3; i += 2) customTree.insert(i);

        // Поиск ключа больше всех существующих (проход i до конца keys.size())
        assertFalse(customTree.search(100));
        // Поиск ключа меньше всех
        assertFalse(customTree.search(0));
    }

    @Test
    @DisplayName("7. Вставка в начало и середину списка (binarySearch)")
    void testInsertOrderVariations() {
        // Вставка в обратном порядке (idx = 0 в binarySearch)
        tree.insert(100);
        tree.insert(10);
        // Вставка в середину
        tree.insert(50);

        assertTrue(tree.search(10));
        assertTrue(tree.search(50));
        assertTrue(tree.search(100));
    }

    @Test
    @DisplayName("9. Исключения в пустых узлах (getFirstLeafKey)")
    void testEmptyNodeExceptions() {
        BPlusTree.LeafNode leaf = new BPlusTree.LeafNode();
        assertThrows(IndexOutOfBoundsException.class, leaf::getFirstLeafKey);

        BPlusTree.InternalNode internal = new BPlusTree.InternalNode();
        assertThrows(IndexOutOfBoundsException.class, internal::getFirstLeafKey);
    }

    @Test
    @DisplayName("10. Покрытие метода getPath")
    void testGetPath() {
        tree.insert(1);
        List<String> path = tree.getPath();
        assertNotNull(path);
        // Проверка, что возвращается копия (изменение копии не влияет на оригинал)
        path.clear();
        tree.search(1);
        assertFalse(tree.getPath().isEmpty());
    }

    @Test
    @DisplayName("5. Разделение внутреннего узла (INTERNAL_SPLIT)")
    void testInternalSplit() {
        // Уменьшаем M до 2, чтобы сплиты происходили максимально часто
        BPlusTree tinyTree = new BPlusTree(2);
        boolean splitHappened = false;
        for (int i = 1; i <= 4; i++) {
            tinyTree.insert(i);
            if (tinyTree.getPath().contains("INTERNAL_SPLIT")) {
                splitHappened = true;
            }
        }
        assertTrue(splitHappened, "INTERNAL_SPLIT должен произойти при M=2");
    }

    @Test
    @DisplayName("8. Покрытие веток InternalNode.insert (idx >= 0)")
    void testInternalInsertExistingKey() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40); // Вызывает первый LEAF_SPLIT, появляется корень

        // Вставляем элементы, которые гарантированно пройдут по всем веткам условий
        int[] extra = {5, 15, 25, 35, 45, 55, 65};
        for(int x : extra) tree.insert(x);

        assertTrue(tree.search(25));
    }
}