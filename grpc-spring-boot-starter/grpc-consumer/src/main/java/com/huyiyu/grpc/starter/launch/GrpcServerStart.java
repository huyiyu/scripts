package com.huyiyu.grpc.starter.launch;

import com.huyiyu.grpc.starter.property.GrpcServerProperties;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.SmartLifecycle;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
@Slf4j
@RequiredArgsConstructor
public class GrpcServerStart implements SmartLifecycle {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final GrpcServerProperties gRpcServerProperties;

    private final ObjectProvider<ServerInterceptor> serverInterceptorProvider;
    private final ObjectProvider<BindableService> bindableServiceProvider;

    private final Consumer<ServerBuilder<?>> configurator;

    private Server server;

    private final ServerBuilder<?> serverBuilder;

    private CountDownLatch latch;

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }
        log.info("Starting gRPC Server ...");
        latch = new CountDownLatch(1);
        try {
            if (gRpcServerProperties.isEnableReflection()) {
                serverBuilder.addService(ProtoReflectionService.newInstance());
                log.info("'{}' service has been registered.", ProtoReflectionService.class.getName());
            }
            configurator.accept(serverBuilder);
            bindableServiceProvider.forEach(serverBuilder::addService);
            bindableServiceProvider
            server = serverBuilder
                    .add
                    .build().start();
            isRunning.compareAndSet(false,true);
            startDaemonAwaitThread();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start GRPC server", e);
        }

    }





    private void startDaemonAwaitThread() {
        Thread awaitThread = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                log.error("gRPC server awaiter interrupted.", e);
            } finally {
                isRunning.set(false);
            }
        });
        awaitThread.setName("grpc-server-awaiter");
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override
    public void stop() {
        Optional.ofNullable(server).ifPresent(s -> {
            log.info("Shutting down gRPC server ...");


            s.shutdown();
            int shutdownGrace = gRpcServerProperties.getShutdownGrace();
            try {
                // If shutdownGrace is 0, then don't call awaitTermination
                if (shutdownGrace < 0) {
                    s.awaitTermination();
                } else if (shutdownGrace > 0) {
                    s.awaitTermination(shutdownGrace, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                log.error("gRPC server interrupted during destroy.", e);
            } finally {
                latch.countDown();
            }
            log.info("gRPC server stopped.");
        });

    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
}
