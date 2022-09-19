/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.threading;

import java.util.concurrent.ThreadFactory;

public class ThreadFactories {
    public static ThreadFactory threadFactory(String name, Thread.UncaughtExceptionHandler exceptionHandler) {
        return r -> {
            var thread = new Thread(r, name);
            thread.setUncaughtExceptionHandler(exceptionHandler);
            return thread;
        };
    }

    public static ThreadFactory threadFactory(ThreadGroup group, Thread.UncaughtExceptionHandler exceptionHandler) {
        return r -> {
            var thread = new Thread(group, r, "");
            thread.setUncaughtExceptionHandler(exceptionHandler);
            return thread;
        };
    }

    public static String threadErrorMessage(Thread thread) {
        return String.format("Unhandled exception in thread %s-%s.", thread.getName(), thread.getId());
    }

    public static ThreadFactoryBuilder builder() {
        return new ThreadFactoryBuilder();
    }

    public static class ThreadFactoryBuilder {
        private ThreadGroup group;
        private String name;
        private boolean daemon;
        private int priority = -1;
        private Thread.UncaughtExceptionHandler exceptionHandler;

        public ThreadFactoryBuilder group(ThreadGroup group) {
            this.group = group;
            return this;
        }

        public ThreadFactoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ThreadFactoryBuilder daemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        public ThreadFactoryBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public ThreadFactoryBuilder exceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public ThreadFactory build() {
            return r -> {
                Thread thread;
                if (group == null) {
                    thread = new Thread(r);
                } else {
                    thread = new Thread(group, r);
                }

                if (name != null) {
                    thread.setName(name);
                }

                thread.setDaemon(daemon);
                if (priority != -1) {
                    thread.setPriority(priority);
                }

                if (exceptionHandler != null) {
                    thread.setUncaughtExceptionHandler(exceptionHandler);
                }

                return thread;
            };
        }
    }
}
