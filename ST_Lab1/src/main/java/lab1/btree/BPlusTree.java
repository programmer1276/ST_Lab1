//package lab1.btree;
//
//import java.util.*;
//
//public class BPlusTree {
//    private final int M = 7;
//    private Node root = new LeafNode();
//    private final List<String> path = new ArrayList<>();
//
//    public void insert(int key) {
//        path.clear();
//        Node newNode = root.insert(key, path, M);
//        if (newNode != null) {
//            // Характерная точка: создание нового корня (рост дерева)
//            path.add("NEW_ROOT");
//            InternalNode newRoot = new InternalNode();
//            newRoot.keys.add(newNode.getFirstLeafKey());
//            newRoot.children.add(root);
//            newRoot.children.add(newNode);
//            root = newRoot;
//        }
//    }
//
//    public boolean search(int key) {
//        path.clear();
//        return root.search(key, path);
//    }
//
//    public List<String> getPath() { return new ArrayList<>(path); }
//
//    static abstract class Node {
//        List<Integer> keys = new ArrayList<>();
//        abstract Node insert(int key, List<String> p, int m);
//        abstract boolean search(int key, List<String> p);
//        abstract int getFirstLeafKey();
//    }
//
//    static class LeafNode extends Node {
//        @Override
//        Node insert(int key, List<String> p, int m) {
//            int idx = Collections.binarySearch(keys, key);
//            if (idx < 0) keys.add(-(idx + 1), key);
//            p.add("INSERT");
//            if (keys.size() > m) {
//                // Характерная точка: разделение листа
//                p.add("LEAF_SPLIT");
//                LeafNode sibling = new LeafNode();
//                int mid = keys.size() / 2;
//                sibling.keys.addAll(keys.subList(mid, keys.size()));
//                keys.subList(mid, keys.size()).clear();
//                return sibling;
//            }
//            return null;
//        }
//
//        @Override
//        boolean search(int key, List<String> p) {
//            boolean found = Collections.binarySearch(keys, key) >= 0;
//            p.add(found ? "SUCCESS" : "FAIL");
//            return found;
//        }
//
//        @Override
//        int getFirstLeafKey() { return keys.get(0); }
//    }
//
//    static class InternalNode extends Node {
//        List<Node> children = new ArrayList<>();
//
//        @Override
//        Node insert(int key, List<String> p, int m) {
//            int i = 0;
//            while (i < keys.size()) {
//                if (key < keys.get(i)) break;
//                i++;
//            }
//            Node result = children.get(i).insert(key, p, m);
//            if (result != null) {
//                int upKey = result.getFirstLeafKey();
//                int idx = Collections.binarySearch(keys, upKey);
//                if (idx < 0) keys.add(-(idx + 1), upKey);
//                else keys.add(idx, upKey);
//
//                children.add((idx < 0 ? -(idx + 1) : idx) + 1, result);
//
//                if (keys.size() > m) {
//                    // Характерная точка: разделение внутреннего узла
//                    p.add("INTERNAL_SPLIT");
//                    InternalNode sib = new InternalNode();
//                    int mid = keys.size() / 2;
//                    sib.keys.addAll(keys.subList(mid + 1, keys.size()));
//                    sib.children.addAll(children.subList(mid + 1, children.size()));
//                    keys.subList(mid, keys.size()).clear();
//                    children.subList(mid + 1, children.size()).clear();
//                    return sib;
//                }
//            }
//            return null;
//        }
//
//        @Override
//        boolean search(int key, List<String> p) {
//            int i = 0;
//            while (i < keys.size()) {
//                if (key < keys.get(i)) break;
//                i++;
//            }
//            return children.get(i).search(key, p);
//        }
//
//        @Override
//        int getFirstLeafKey() { return children.get(0).getFirstLeafKey(); }
//    }
//}

package lab1.btree;

import java.util.*;

public class BPlusTree {
    // Делаем M изменяемым через конструктор
    private final int M;
    private Node root = new LeafNode();
    private final List<String> path = new ArrayList<>();

    // Конструктор с проверкой ограничения
    public BPlusTree(int m) {
        if (m > 7) {
            throw new IllegalArgumentException("Параметр M не может быть больше 7");
        }
        if (m < 2) {
            throw new IllegalArgumentException("Параметр M должен быть не менее 2 для корректной работы дерева");
        }
        this.M = m;
    }

    // Дефолтный конструктор для совместимости с предыдущими тестами
    public BPlusTree() {
        this(7);
    }

    public void insert(int key) {
        path.clear();
        Node newNode = root.insert(key, path, M);
        if (newNode != null) {
            path.add("NEW_ROOT");
            InternalNode newRoot = new InternalNode();
            newRoot.keys.add(newNode.getFirstLeafKey());
            newRoot.children.add(root);
            newRoot.children.add(newNode);
            root = newRoot;
        }
    }

    public boolean search(int key) {
        path.clear();
        return root.search(key, path);
    }

    public List<String> getPath() { return new ArrayList<>(path); }

    static abstract class Node {
        List<Integer> keys = new ArrayList<>();
        abstract Node insert(int key, List<String> p, int m);
        abstract boolean search(int key, List<String> p);
        abstract int getFirstLeafKey();
    }

    static class LeafNode extends Node {
        @Override
        Node insert(int key, List<String> p, int m) {
            int idx = Collections.binarySearch(keys, key);
            // Если ключ уже есть, ничего не вставляем (предотвращение дубликатов)
            if (idx >= 0) return null;

            keys.add(-(idx + 1), key);
            p.add("INSERT");

            if (keys.size() > m) {
                p.add("LEAF_SPLIT");
                LeafNode sibling = new LeafNode();
                int mid = keys.size() / 2;
                sibling.keys.addAll(keys.subList(mid, keys.size()));
                keys.subList(mid, keys.size()).clear();
                return sibling;
            }
            return null;
        }

        @Override
        boolean search(int key, List<String> p) {
            boolean found = Collections.binarySearch(keys, key) >= 0;
            p.add(found ? "SUCCESS" : "FAIL");
            return found;
        }

        @Override
        int getFirstLeafKey() { return keys.get(0); }
    }

    static class InternalNode extends Node {
        List<Node> children = new ArrayList<>();

        @Override
        Node insert(int key, List<String> p, int m) {
            int i = 0;
            while (i < keys.size()) {
                if (key < keys.get(i)) break;
                i++;
            }
            Node result = children.get(i).insert(key, p, m);
            if (result != null) {
                int upKey = result.getFirstLeafKey();
                int idx = Collections.binarySearch(keys, upKey);

                int insertPos = (idx < 0) ? -(idx + 1) : idx;
                keys.add(insertPos, upKey);
                children.add(insertPos + 1, result);

                if (keys.size() > m - 1) {
                    p.add("INTERNAL_SPLIT");
                    InternalNode sib = new InternalNode();
                    int mid = keys.size() / 2;

                    // Перенос ключей и детей во второй узел
                    sib.keys.addAll(keys.subList(mid + 1, keys.size()));
                    sib.children.addAll(children.subList(mid + 1, children.size()));

                    // Очистка текущего узла
                    keys.subList(mid, keys.size()).clear();
                    children.subList(mid + 1, children.size()).clear();
                    return sib;
                }
            }
            return null;
        }

        @Override
        boolean search(int key, List<String> p) {
            int i = 0;
            while (i < keys.size()) {
                if (key < keys.get(i)) break;
                i++;
            }
            return children.get(i).search(key, p);
        }

        @Override
        int getFirstLeafKey() { return children.get(0).getFirstLeafKey(); }
    }
}