/*
 * @(#) LogListTest.java
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

package io.jstuff.log.test;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.jstuff.text.StringMatcher;
import io.jstuff.log.Level;
import io.jstuff.log.Log;
import io.jstuff.log.LogItem;
import io.jstuff.log.LogList;
import io.jstuff.log.Logger;
import io.jstuff.log.NullLogger;

public class LogListTest {

    @Test
    public void shouldCreateListAndAllowCopies() {
        Logger nullLogger = new NullLogger("xxx");
        try (LogList logList = LogList.create()) {
            assertEquals(0, logList.getSize());
            List<LogItem> list1 = logList.toList();
            assertEquals(0, list1.size());
            logList.receive(System.currentTimeMillis(), nullLogger, Level.INFO, "Testing", null);
            assertEquals(1, logList.getSize());
            List<LogItem> list2 = logList.toList();
            assertEquals(1, list2.size());
            assertEquals(0, list1.size());
            LogItem logItem = logList.iterator().next();
            assertEquals("xxx", logItem.getName());
            assertEquals(Level.INFO, logItem.getLevel());
            assertEquals("Testing", logItem.getMessage());
            assertEquals("Testing", logItem.getMessageString());
            assertNull(logItem.getThrowable());
        }
    }

    @Test
    public void shouldUseClockOnLogItems() {
        ZoneOffset zoneOffset = ZoneOffset.ofHours(12);
        OffsetDateTime time = OffsetDateTime.of(2022, 6, 12, 22, 41, 3, 456_000_000, zoneOffset);
        Clock clock = Clock.fixed(time.toInstant(), time.getOffset());
        Logger logger = Log.getLogger("wombat", clock);
        try (LogList logList = LogList.create()) {
            logger.info("Hello!");
            Iterator<LogItem> iterator = logList.iterator();
            assertTrue(iterator.hasNext());
            LogItem logItem = iterator.next();
            assertEquals(time.toInstant().toEpochMilli(), logItem.getTime());
            assertEquals("wombat", logItem.getName());
            assertEquals(Level.INFO, logItem.getLevel());
            assertEquals("Hello!", logItem.getMessage());
            assertEquals("Hello!", logItem.getMessageString());
            assertNull(logItem.getThrowable());
            assertEquals("22:41:03.456 wombat INFO Hello!", logItem.toString(zoneOffset));
        }
    }

    @Test
    public void shouldFindTraceLogItem() {
        Logger logger = Log.getLogger(Level.TRACE);
        try (LogList logList = LogList.create()) {
            logger.trace("Trace Message alpha");
            assertTrue(logList.hasTrace("Trace Message alpha"));
            assertFalse(logList.hasDebug("Trace Message alpha"));
            assertFalse(logList.hasTrace("Another Message"));
            assertTrue(logList.hasTraceContaining("alpha"));
            assertFalse(logList.hasInfoContaining("alpha"));
            assertFalse(logList.hasTraceContaining("beta"));
        }
    }

    @Test
    public void shouldFindDebugLogItem() {
        Logger logger = Log.getLogger(Level.DEBUG);
        try (LogList logList = LogList.create()) {
            logger.debug("Debug Message alpha");
            assertTrue(logList.hasDebug("Debug Message alpha"));
            assertFalse(logList.hasTrace("Debug Message alpha"));
            assertFalse(logList.hasDebug("Another Message"));
            assertTrue(logList.hasDebugContaining("alpha"));
            assertFalse(logList.hasWarnContaining("alpha"));
            assertFalse(logList.hasDebugContaining("beta"));
        }
    }

    @Test
    public void shouldFindInfoLogItem() {
        Logger logger = Log.getLogger();
        try (LogList logList = LogList.create()) {
            logger.info("Test Message alpha");
            assertTrue(logList.hasInfo("Test Message alpha"));
            assertFalse(logList.hasError("Test Message alpha"));
            assertFalse(logList.hasInfo("Another Message"));
            assertTrue(logList.hasInfoContaining("alpha"));
            assertFalse(logList.hasDebugContaining("alpha"));
            assertFalse(logList.hasInfoContaining("beta"));
        }
    }

    @Test
    public void shouldFindWarnLogItem() {
        Logger logger = Log.getLogger();
        try (LogList logList = LogList.create()) {
            logger.warn("Warning Message alpha");
            assertTrue(logList.hasWarn("Warning Message alpha"));
            assertFalse(logList.hasInfo("Warning Message alpha"));
            assertFalse(logList.hasWarn("Another Message"));
            assertTrue(logList.hasWarnContaining("alpha"));
            assertFalse(logList.hasErrorContaining("alpha"));
            assertFalse(logList.hasWarnContaining("beta"));
        }
    }

    @Test
    public void shouldFindErrorLogItem() {
        Logger logger = Log.getLogger();
        try (LogList logList = LogList.create()) {
            logger.error("Error Message alpha");
            assertTrue(logList.hasError("Error Message alpha"));
            assertFalse(logList.hasInfo("Error Message alpha"));
            assertFalse(logList.hasError("Another Message"));
            assertTrue(logList.hasErrorContaining("alpha"));
            assertFalse(logList.hasDebugContaining("alpha"));
            assertFalse(logList.hasErrorContaining("beta"));
        }
    }

    @Test
    public void shouldFilterByLoggerName() {
        Logger logger1 = Log.getLogger("goanna");
        Logger logger2 = Log.getLogger("skink");
        try (LogList logList = LogList.create("goanna")) {
            logger1.info("alpha");
            logger2.info("beta");
            assertTrue(logList.hasInfo("alpha"));
            assertFalse(logList.hasInfo("beta"));
        }
    }

    @Test
    public void shouldFilterByLoggerClass() {
        Logger logger1 = Log.getLogger(LogItem.class);
        Logger logger2 = Log.getLogger(LogList.class);
        try (LogList logList = LogList.create(LogItem.class)) {
            logger1.info("gamma");
            logger2.info("delta");
            assertTrue(logList.hasInfo("gamma"));
            assertFalse(logList.hasInfo("delta"));
        }
    }

    @Test
    public void shouldFilterByStringMatcher() {
        Logger logger1 = Log.getLogger("wallaby");
        Logger logger2 = Log.getLogger("wombat");
        Logger logger3 = Log.getLogger("echidna");
        try (LogList logList = LogList.create(StringMatcher.wildcard("w*"))) {
            logger1.info("one");
            logger2.info("two");
            logger3.info("three");
            assertTrue(logList.hasInfo("one"));
            assertTrue(logList.hasInfo("two"));
            assertFalse(logList.hasInfo("three"));
        }
    }

}
