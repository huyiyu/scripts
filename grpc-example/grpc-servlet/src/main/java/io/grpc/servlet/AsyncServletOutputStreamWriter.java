//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.grpc.servlet;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import io.grpc.InternalLogId;
import java.io.IOException;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;

final class AsyncServletOutputStreamWriter {
    private final AtomicReference<WriteState> writeState;
    private final Log log;
    private final BiFunction<byte[], Integer, ActionItem> writeAction;
    private final ActionItem flushAction;
    private final ActionItem completeAction;
    private final BooleanSupplier isReady;
    private final Queue<ActionItem> writeChain;
    @Nullable
    private volatile Thread parkingThread;

    AsyncServletOutputStreamWriter(AsyncContext asyncContext, ServletServerStream.ServletTransportState transportState, final InternalLogId logId) throws IOException {
        this.writeState = new AtomicReference(AsyncServletOutputStreamWriter.WriteState.DEFAULT);
        this.writeChain = new ConcurrentLinkedQueue();
        final Logger logger = Logger.getLogger(AsyncServletOutputStreamWriter.class.getName());
        this.log = new Log() {
            public boolean isLoggable(Level level) {
                return logger.isLoggable(level);
            }

            public void fine(String str, Object... params) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "[" + logId + "]" + str, params);
                }

            }

            public void finest(String str, Object... params) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "[" + logId + "] " + str, params);
                }

            }
        };
        ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream();
        this.writeAction = (bytes, numBytes) -> () -> {
            outputStream.write(bytes, 0, numBytes);
            transportState.runOnTransportThread(() -> transportState.onSentBytes(numBytes));
            if (this.log.isLoggable(Level.FINEST)) {
                this.log.finest("outbound data: length={0}, bytes={1}", numBytes, ServletServerStream.toHexString(bytes, numBytes));
            }

        };
        this.flushAction = () -> {
            this.log.finest("flushBuffer");
            asyncContext.getResponse().flushBuffer();
        };
        this.completeAction = () -> {
            this.log.fine("call is completing");
            transportState.runOnTransportThread(() -> {
                transportState.complete();
                asyncContext.complete();
                this.log.fine("call completed");
            });
        };
        this.isReady = () -> outputStream.isReady();
    }

    @VisibleForTesting
    AsyncServletOutputStreamWriter(BiFunction<byte[], Integer, ActionItem> writeAction, ActionItem flushAction, ActionItem completeAction, BooleanSupplier isReady, Log log) {
        this.writeState = new AtomicReference(AsyncServletOutputStreamWriter.WriteState.DEFAULT);
        this.writeChain = new ConcurrentLinkedQueue();
        this.writeAction = writeAction;
        this.flushAction = flushAction;
        this.completeAction = completeAction;
        this.isReady = isReady;
        this.log = log;
    }

    void writeBytes(byte[] bytes, int numBytes) throws IOException {
        this.runOrBuffer((ActionItem)this.writeAction.apply(bytes, numBytes));
    }

    void flush() throws IOException {
        this.runOrBuffer(this.flushAction);
    }

    void complete() {
        try {
            this.runOrBuffer(this.completeAction);
        } catch (IOException var2) {
        }

    }

    void onWritePossible() throws IOException {
        this.log.finest("onWritePossible: ENTRY. The servlet output stream becomes ready");
        this.assureReadyAndDrainedTurnsFalse();

        while(this.isReady.getAsBoolean()) {
            WriteState curState = (WriteState)this.writeState.get();
            ActionItem actionItem = (ActionItem)this.writeChain.poll();
            if (actionItem != null) {
                actionItem.run();
            } else if (this.writeState.compareAndSet(curState, curState.withReadyAndDrained(true))) {
                this.log.finest("onWritePossible: EXIT. All data available now is sent out and the servlet output stream is still ready");
                return;
            }
        }

        this.log.finest("onWritePossible: EXIT. The servlet output stream becomes not ready");
    }

    private void assureReadyAndDrainedTurnsFalse() {
        while(((WriteState)this.writeState.get()).readyAndDrained) {
            this.parkingThread = Thread.currentThread();
            LockSupport.parkNanos(Duration.ofHours(1L).toNanos());
        }

        this.parkingThread = null;
    }

    private void runOrBuffer(ActionItem actionItem) throws IOException {
        WriteState curState = (WriteState)this.writeState.get();
        if (curState.readyAndDrained) {
            actionItem.run();
            if (actionItem == this.completeAction) {
                return;
            }

            if (!this.isReady.getAsBoolean()) {
                boolean successful = this.writeState.compareAndSet(curState, curState.withReadyAndDrained(false));
                LockSupport.unpark(this.parkingThread);
                Preconditions.checkState(successful, "Bug: curState is unexpectedly changed by another thread");
                this.log.finest("the servlet output stream becomes not ready");
            }
        } else {
            this.writeChain.offer(actionItem);
            if (!this.writeState.compareAndSet(curState, curState.withReadyAndDrained(false))) {
                Preconditions.checkState(((WriteState)this.writeState.get()).readyAndDrained, "Bug: onWritePossible() should have changed readyAndDrained to true, but not");
                ActionItem lastItem = (ActionItem)this.writeChain.poll();
                if (lastItem != null) {
                    Preconditions.checkState(lastItem == actionItem, "Bug: lastItem != actionItem");
                    this.runOrBuffer(lastItem);
                }
            }
        }

    }

    @VisibleForTesting
    interface Log {
        default boolean isLoggable(Level level) {
            return false;
        }

        default void fine(String str, Object... params) {
        }

        default void finest(String str, Object... params) {
        }
    }

    private static final class WriteState {
        static final WriteState DEFAULT = new WriteState(false);
        final boolean readyAndDrained;

        WriteState(boolean readyAndDrained) {
            this.readyAndDrained = readyAndDrained;
        }

        @CheckReturnValue
        WriteState withReadyAndDrained(boolean readyAndDrained) {
            return new WriteState(readyAndDrained);
        }
    }

    @FunctionalInterface
    @VisibleForTesting
    interface ActionItem {
        void run() throws IOException;
    }
}
