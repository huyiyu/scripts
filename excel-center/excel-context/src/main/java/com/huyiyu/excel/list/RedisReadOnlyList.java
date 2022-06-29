package com.huyiyu.excel.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.ListOperations;

/**
 * 解决大对象内存溢出问题
 * 该List 延迟加载每次只取1条不要addAll到其他
 * List中
 */
public class RedisReadOnlyList<E> implements Collection<E> {

    private String redisListKey;
    private ListOperations listOperations;
    private final int size;
    private final int step;

    public RedisReadOnlyList(String redisListKey,
        ListOperations<String,E> listOperations,int step) {
        this.redisListKey = redisListKey;
        this.listOperations = listOperations;
        this.size = listOperations.size(redisListKey).intValue();
        this.step = step;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return listOperations.size(redisListKey) == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public Iterator<E> iterator() {
        return new RedisListIterator<E>();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("不允许该操作");
    }

    private class RedisListIterator<E> implements Iterator<E> {

        private List<E> cacheList;
        private int index = 0;
        private static final int STEP = 1000;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public E next() {
            if (CollectionUtils.isEmpty(cacheList)) {
                int remain = size - index;
                // 每次取出1000条
                int end = remain < STEP ? index + remain : index + STEP;
                cacheList = listOperations.range(redisListKey, index, end);
            }
            E remove = cacheList.remove(0);
            index++;
            return remove;
        }
    }

}
