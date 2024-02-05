package cum.jesus.cts.util;

import java.util.AbstractList;
import java.util.Arrays;

public final class StaticList<E> extends AbstractList<E> {
    private final Object[] elements;
    private int pos = 0;

    public StaticList(int size, E initialValue) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        elements = new Object[size];
        Arrays.fill(elements, initialValue);
    }

    public StaticList(int size) {
        this(size, null);
    }

    @Override
    public boolean add(E e) {
        checkIndex(pos);
        elements[pos++] = e;
        return true;
    }

    @Override
    public E get(int index) {
        checkIndex(index);
        @SuppressWarnings("unchecked")
        E element = (E) elements[index];
        return element;
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index);
        E old = get(index);
        elements[index] = element;
        return old;
    }

    @Override
    public int size() {
        return elements.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.length);
        }
    }
}
