package app.ipreach.backend.shared.enums;

import java.util.Set;

public interface EnumMethods {

    // object
    default boolean equalsAny(Object... objects) {
        for (Object o : objects)
            if (this == o) return true;
        return false;
    }

    default boolean notEquals(Object object) {
        return this != object;
    }

    default boolean noneEquals(Object... objects) {
        return !equalsAny(objects);
    }

    // set
    default <E> boolean equalsAnyOnSet(Set<E> set) {
        return equalsAny(set.toArray());
    }

    default <E> boolean noneEqualsOnSet(Set<E> set) {
        return !equalsAnyOnSet(set);
    }

    // array

    default <E> boolean equalsAnyOnArray(E[] array) {
        for (E element : array)
            if (this == element) return true;
        return false;
    }

    default <E> boolean noneEqualsAnyOnArray(E[] array) {
        return !equalsAnyOnArray(array);
    }

}
