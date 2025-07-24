package com.dice.minigameshub.game_clicker_service.common.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class CollectionUtils {

    public static <E, C extends Collection<E>> C combineCollections(Collection<E> one, Collection<E> two, Supplier<C> collectionConstructor) {
        return Stream.concat(one.stream(), two.stream())
                .collect(Collectors.toCollection(collectionConstructor));
    }

    public static <E> Set<E> combineSets(Set<E> one, Set<E> two) {
        return combineCollections(one, two, HashSet::new);
    }

    public static <E> List<E> combineLists(List<E> one, List<E> two) {
        return combineCollections(one, two, ArrayList::new);
    }
}
