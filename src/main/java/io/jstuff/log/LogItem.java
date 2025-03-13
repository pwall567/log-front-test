/*
 * @(#) LogItem.java
 *
 * log-front-test  Logging interface testing
 * Copyright (c) 2021, 2022, 2025 Peter Wall
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

import java.time.ZoneId;
import java.util.Objects;

/**
 * A log item, as used by the {@link LogList} class.  This is an effectively immutable object.
 *
 * @author  Peter Wall
 */
public class LogItem {

    private static final ZoneId defaultZoneId = ZoneId.systemDefault();

    private final long time;
    private final String name;
    private final Level level;
    private final Object message;
    private final Throwable throwable;

    private String messageString = null;

    /**
     * Create a {@code LogItem}.
     *
     * @param   time        the time of the log event in milliseconds
     * @param   name        the name of the {@link Logger}
     * @param   level       the {@link Level} of the log event
     * @param   message     the message as an object
     * @param   throwable   an optional {@link Throwable}
     */
    public LogItem(long time, String name, Level level, Object message, Throwable throwable) {
        this.time = time;
        this.name = name;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
    }

    /**
     * Get the message as a {@link String}.
     *
     * @return      the string form of the message, or an empty string if {@code null}
     */
    public String getMessageString() {
        if (messageString == null)
            messageString = message == null ? "" : message.toString();
        return messageString;
    }

    /**
     * Get the time of the log event.
     *
     * @return      the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Get the name of the {@link Logger} that output the log event.
     *
     * @return      the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the {@link Level} of the log event.
     *
     * @return      the {@link Level}
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Get the text of the log event.
     *
     * @return      the text
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Get the {@link Throwable} associated with the event, or {@code null} if none specified.
     *
     * @return      the {@link Throwable}, or {@code null}
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Create a formatted form of the {@code LogItem}.
     *
     * @return      the formatted string
     */
    @Override
    public String toString() {
        return toString(' ', defaultZoneId);
    }

    /**
     * Create a formatted form of the {@code LogItem}, using the specified separator and the default time zone.
     *
     * @param       separator   the custom separator
     * @return      the formatted string
     */
    public String toString(char separator) {
        return toString(separator, defaultZoneId);
    }

    /**
     * Create a formatted form of the {@code LogItem}, using the specified separator and the default time zone.
     *
     * @param       zoneId      the time zone to be applied
     * @return      the formatted string
     */
    public String toString(ZoneId zoneId) {
        return toString(' ', zoneId);
    }

    /**
     * Create a formatted form of the {@code LogItem}, using the specified separator and time zone.
     *
     * @param       separator   the custom separator
     * @param       zoneId      the time zone to be applied
     * @return      the formatted string
     */
    public String toString(char separator, ZoneId zoneId) {
        StringBuilder sb = new StringBuilder();
        int dayMillis = AbstractFormatter.getDayMillis(time, zoneId);
        AbstractFormatter.outputTime(dayMillis, ch -> sb.append((char)ch));
        sb.append(separator);
        sb.append(name).append(separator);
        sb.append(level).append(separator);
        sb.append(getMessageString());
        if (throwable != null) {
            sb.append(separator).append(throwable.getClass().getName());
            sb.append(separator).append(throwable.getMessage());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof LogItem))
            return false;
        LogItem other = (LogItem)obj;
        return time == other.time && Objects.equals(name, other.name) && Objects.equals(level, other.level) &&
                Objects.equals(message, other.message) && Objects.equals(throwable, other.throwable);
    }

    @Override
    public int hashCode() {
        return (int)time ^ Objects.hashCode(name) ^ Objects.hashCode(level) ^ Objects.hashCode(message) ^
                Objects.hashCode(throwable);
    }

}
