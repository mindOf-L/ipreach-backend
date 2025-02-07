package app.ipreach.backend.shared.creation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Generator {

    public static <E extends Enum<E>> E getRandomEnum(Class<E> enumType) {
        return enumType.getEnumConstants()[getRandomIntegerFromRange(0, enumType.getEnumConstants().length)];
    }

    public static Integer getRandomIntegerFromRange(int min, int max) {
        return new Random().ints(1, min, max).iterator().nextInt();
    }

    public static <E extends Enum<E>> List<E> getRandomListFromEnum(Class<E> enumType, int maxSize) {
        var s = new HashSet<E>();
        for (int i = 0; i < maxSize; i++)
            s.add(getRandomEnum(enumType));

        return new ArrayList<>(s);
    }

    public static <E>List<E> getRandomList(Supplier<E> callback) {
        var elements = getRandomIntegerFromRange(1, 10);
        return IntStream.rangeClosed(1, elements).mapToObj(_ -> callback.get()).collect(Collectors.toList());
    }

}
