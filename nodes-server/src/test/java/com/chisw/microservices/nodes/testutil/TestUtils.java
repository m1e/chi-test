package com.chisw.microservices.nodes.testutil;

import com.chisw.microservices.nodes.persistence.jpa.entity.Node;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Utilities for tests.
 */
public abstract class TestUtils {

    private TestUtils() {

        throw new AssertionError();
    }

    /**
     * Replaces all occurrence of {@code '} (single quote) with {@code "} (double quote) and all occurrence of {@code \'} with
     * {@code '}.
     * <p/>
     * This allows to easily input json string into the java code without having to escape double quotes.
     *
     * @param jsonWithSingleQuotes A JSON string with single quotes instead of double quotes.
     * @param args                 Arguments to be passed to the {@link String#format(String, Object...)} function.
     * @return A JSON string with double quotes.
     */
    public static String json(String jsonWithSingleQuotes, Object... args) {

        return String.format(jsonWithSingleQuotes.replace('\'', '"').replace("\\'", "'"), args);
    }

    @SafeVarargs
    public static <E> List<E> list(E... elements) {

        return new ArrayList<>(Arrays.asList(elements));
    }

    @SafeVarargs
    public static <E> Set<E> set(E... elements) {

        return new HashSet<>(Arrays.asList(elements));
    }

    public static <E> Set<E> set(Collection<E> elements) {

        return new HashSet<>(elements);
    }

    public static <E> List<E> iteratorToList(Iterator<E> iterator) {

        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }

        List list = new ArrayList();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public static Node node(String id, String path) {

        Node node = new Node();
        node.setId(id);
        node.setPath(path);
        return node;
    }

    public static void assertNodeEquals(Node expected, Node actual) {

        assertThat(expected.getId(), is(actual.getId()));
        assertThat(expected.getPath(), is(actual.getPath()));
    }

    @SafeVarargs
    public static <E> Iterator<E> iterator(E... elements) {

        return list(elements).iterator();
    }

}
