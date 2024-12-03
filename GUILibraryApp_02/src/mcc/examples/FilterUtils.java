package mcc.examples;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterUtils {

    /**
     * Filters a list of objects based on a given condition.
     *
     * @param list   The list to filter.
     * @param filter The condition to apply (Predicate).
     * @param <T>    The type of the objects in the list.
     * @return A filtered list of objects that match the condition.
     */
    public static <T> List<T> filterList(List<T> list, Predicate<T> filter) {
        return list.stream()
                   .filter(filter)
                   .collect(Collectors.toList());
    }
 
}
