package org.apache.logging;

import java.util.function.Function;

public final class LogFactory {

    /**
     * Set this to whatever you want at startup.
     */
    public static Function<Class<?>, Log> LOGGER_FACTORY = (c) -> {
        final String logName = c.getSimpleName();
        return new Log() {

            private void log(String level, Object message, Throwable t) {
                String messageString;
                try {
                    messageString = message.toString();
                } catch (Throwable e) {
                    messageString = "<error evaluating message: "+e+">";
                }

                System.err.println("PDFBOX ["+level+"] "+logName+": "+messageString);
                if (t != null) {
                    t.printStackTrace(System.err);
                }
            }

            @Override
            public boolean isTraceEnabled() {
                return false;
            }

            @Override
            public boolean isDebugEnabled() {
                return false;
            }

            @Override
            public boolean isInfoEnabled() {
                return true;
            }

            @Override
            public boolean isWarnEnabled() {
                return true;
            }

            @Override
            public boolean isErrorEnabled() {
                return true;
            }

            @Override
            public boolean isFatalEnabled() {
                return true;
            }

            @Override
            public void trace(Object message) {}

            @Override
            public void trace(Object message, Throwable t) {}

            @Override
            public void debug(Object message) {}

            @Override
            public void debug(Object message, Throwable t) {}

            @Override
            public void info(Object message) {
                log("INFO", message, null);
            }

            @Override
            public void info(Object message, Throwable t) {
                log("INFO", message, t);
            }

            @Override
            public void warn(Object message) {
                log("WARN", message, null);
            }

            @Override
            public void warn(Object message, Throwable t) {
                log("WARN", message, t);
            }

            @Override
            public void error(Object message) {
                log("ERROR", message, null);
            }

            @Override
            public void error(Object message, Throwable t) {
                log("ERROR", message, t);
            }

            @Override
            public void fatal(Object message) {
                log("FATAL", message, null);
            }

            @Override
            public void fatal(Object message, Throwable t) {
                log("FATAL", message, t);
            }
        };
    };

    public static <T> Log getLog(Class<T> loggedClass) {
        return LOGGER_FACTORY.apply(loggedClass);
    }
}
