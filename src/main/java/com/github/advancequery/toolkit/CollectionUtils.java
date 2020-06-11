package com.github.advancequery.toolkit;

import java.util.Collection;

/**
 * @author linyunrui
 */
public class CollectionUtils {

    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }
}
