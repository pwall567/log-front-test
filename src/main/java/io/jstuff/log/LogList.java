/*
 * @(#) LogList.java
 *
 * log-front-test  Logging interface testing
 * Copyright (c) 2021, 2022, 2024, 2025 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.jstuff.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import io.jstuff.text.StringMatcher;

/**
 * An implementation of {@link LogListener} that stores log items in a list.
 *
 * @author  Peter Wall
 */
public class LogList extends LogListener implements List<LogItem> {

    private final List<LogItem> list = new ArrayList<>();
    private final StringMatcher loggerNameMatcher;

    /**
     * Construct a {@code LogList} for one or more {@link Logger}s, identified by {@link StringMatcher}.
     *
     * @param   loggerNameMatcher   the {@link StringMatcher} for the {@link Logger} name
     */
    public LogList(StringMatcher loggerNameMatcher) {
        this.loggerNameMatcher = loggerNameMatcher;
    }

    /**
     * Construct a {@code LogList} for a specific {@link Logger}, identified by name.
     *
     * @param   loggerName  the {@link Logger} name
     */
    public LogList(String loggerName) {
        this(StringMatcher.simple(loggerName));
    }

    /**
     * Construct a {@code LogList} for a specific {@link Logger}, identified by class.
     *
     * @param   loggerClass the {@link Logger} class
     */
    public LogList(Class<?> loggerClass) {
        this(loggerClass.getName());
    }

    /**
     * Construct a {@code LogList} with no filtering.
     */
    public LogList() {
        this((StringMatcher)null);
    }

    /**
     * Receive a log event and store a new {@link LogItem} in the list.
     *
     * @param   time        the time of the event
     * @param   logger      the logger object
     * @param   level       the logging level
     * @param   message     the message
     * @param   throwable   a {@link Throwable}, if provided
     */
    @Override
    public void receive(long time, Logger logger, Level level, Object message, Throwable throwable) {
        if (loggerNameMatcher == null || loggerNameMatcher.matches(logger.getName())) {
            synchronized (list) {
                list.add(new LogItem(time, logger.getName(), level, message, throwable));
            }
        }
    }

    /**
     * Get an {@link Iterator} over the {@link LogItem} list entries.
     *
     * @return      the {@link Iterator}
     */
    @Override
    public Iterator<LogItem> iterator() {
        return list.iterator();
    }

    /**
     * Get the number of entries in the list.
     *
     * @return      the number of entries
     */
    public int getSize() {
        return list.size();
    }

    /**
     * Get a copy of the list.
     *
     * @return      the copy
     */
    public List<LogItem> toList() {
        return new ArrayList<>(list);
    }

    /**
     * Return {@code true} if the list contains a TRACE item with the given message.
     *
     * @param   message     the expected message
     * @return              {@code true} if found
     */
    public boolean hasTrace(Object message) {
        return hasLogItem(Level.TRACE, message);
    }

    /**
     * Return {@code true} if the list contains a DEBUG item with the given message.
     *
     * @param   message     the expected message
     * @return              {@code true} if found
     */
    public boolean hasDebug(Object message) {
        return hasLogItem(Level.DEBUG, message);
    }

    /**
     * Return {@code true} if the list contains an INFO item with the given message.
     *
     * @param   message     the expected message
     * @return              {@code true} if found
     */
    public boolean hasInfo(Object message) {
        return hasLogItem(Level.INFO, message);
    }

    /**
     * Return {@code true} if the list contains a WARN item with the given message.
     *
     * @param   message     the expected message
     * @return              {@code true} if found
     */
    public boolean hasWarn(Object message) {
        return hasLogItem(Level.WARN, message);
    }

    /**
     * Return {@code true} if the list contains an ERROR item with the given message.
     *
     * @param   message     the expected message
     * @return              {@code true} if found
     */
    public boolean hasError(Object message) {
        return hasLogItem(Level.ERROR, message);
    }

    /**
     * Return {@code true} if the list contains a log item with the given level and message.
     *
     * @param   level       the expected level
     * @param   message     the expected message
     * @return              {@code true} if found
     */
    public boolean hasLogItem(Level level, Object message) {
        for (LogItem logItem : list)
            if (logItem.getLevel().equals(level) && Objects.equals(logItem.getMessage(), message))
                return true;
        return false;
    }

    /**
     * Return {@code true} if the list contains a TRACE item with its message containing the given content.
     *
     * @param   content     the expected message content
     * @return              {@code true} if found
     */
    public boolean hasTraceContaining(String content) {
        return hasLogItemContaining(Level.TRACE, content);
    }

    /**
     * Return {@code true} if the list contains a DEBUG item with its message containing the given content.
     *
     * @param   content     the expected message content
     * @return              {@code true} if found
     */
    public boolean hasDebugContaining(String content) {
        return hasLogItemContaining(Level.DEBUG, content);
    }

    /**
     * Return {@code true} if the list contains an INFO item with its message containing the given content.
     *
     * @param   content     the expected message content
     * @return              {@code true} if found
     */
    public boolean hasInfoContaining(String content) {
        return hasLogItemContaining(Level.INFO, content);
    }

    /**
     * Return {@code true} if the list contains a WARN item with its message containing the given content.
     *
     * @param   content     the expected message content
     * @return              {@code true} if found
     */
    public boolean hasWarnContaining(String content) {
        return hasLogItemContaining(Level.WARN, content);
    }

    /**
     * Return {@code true} if the list contains an ERROR item with its message containing the given content.
     *
     * @param   content     the expected message content
     * @return              {@code true} if found
     */
    public boolean hasErrorContaining(String content) {
        return hasLogItemContaining(Level.ERROR, content);
    }

    /**
     * Return {@code true} if the list contains a log item with the given level and with its message containing the
     * given content.
     *
     * @param   content     the expected message content
     * @return              {@code true} if found
     */
    public boolean hasLogItemContaining(Level level, String content) {
        for (LogItem logItem : list)
            if (logItem.getLevel().equals(level) && logItem.getMessageString().contains(content))
                return true;
        return false;
    }

    /**
     * Create a {@code LogList} (the use of {@code new LogList()} can cause compiler warnings, because the compiler is
     * not aware of any functions adding to the list).
     *
     * @return  a {@code LogList}
     */
    public static LogList create() {
        return new LogList();
    }

    /**
     * Create a {@code LogList} specifying a {@link StringMatcher}.
     *
     * @param   loggerNameMatcher   the {@link StringMatcher} for the {@link Logger} name
     * @return  a {@code LogList}
     */
    public static LogList create(StringMatcher loggerNameMatcher) {
        return new LogList(loggerNameMatcher);
    }

    /**
     * Create a {@code LogList} specifying a {@link Logger} name.
     *
     * @param   loggerName  the {@link Logger} name
     * @return  a {@code LogList}
     */
    public static LogList create(String loggerName) {
        return new LogList(loggerName);
    }

    /**
     * Create a {@code LogList} specifying a {@link Logger} class.
     *
     * @param   loggerClass the {@link Logger} class
     * @return  a {@code LogList}
     */
    public static LogList create(Class<?> loggerClass) {
        return new LogList(loggerClass);
    }

    // The following functions are just to satisfy the List interface. All modifying operations are blocked.

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(LogItem logItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends LogItem> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends LogItem> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LogItem get(int index) {
        return list.get(index);
    }

    @Override
    public LogItem set(int index, LogItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, LogItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LogItem remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<LogItem> listIterator() {
        return new LogListIterator(list);
    }

    @Override
    public ListIterator<LogItem> listIterator(int index) {
        return new LogListIterator(list, index);
    }

    @Override
    public List<LogItem> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public static class LogListIterator implements ListIterator<LogItem> {

        private final List<LogItem> list;
        private int index;

        public LogListIterator(List<LogItem> list) {
            this.list = list;
            index = 0;
        }

        public LogListIterator(List<LogItem> list, int index) {
            if (index > list.size())
                throw new IllegalArgumentException("Index " + index + " beyond end of list " + list.size());
            this.list = list;
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public LogItem next() {
            if (!hasNext())
                throw new NoSuchElementException(String.valueOf(index));
            return list.get(index++);
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public LogItem previous() {
            if (!hasPrevious())
                throw new NoSuchElementException(String.valueOf(index));
            return list.get(--index);
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(LogItem logItem) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(LogItem logItem) {
            throw new UnsupportedOperationException();
        }

    }

}
