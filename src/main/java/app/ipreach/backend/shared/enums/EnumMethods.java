package app.ipreach.backend.shared.enums;

import java.util.Set;

public interface EnumMethods {

    default boolean equalsAny(Object... objects) {
        for (Object o : objects)
            if (this == o) return true;
        return false;
    }

    default <E> boolean equalsAny(Set<E> set) {
        return equalsAny(set.toArray());
    }

    default boolean noneEquals(Object... objects) {
        return !equalsAny(objects);
    }

    default <E> boolean noneEquals(Set<E> set) {
        return !equalsAny(set);
    }

    default boolean notEquals(Object object) {
        return this != object;
    }

}
