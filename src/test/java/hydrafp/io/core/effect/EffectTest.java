package hydrafp.io.core.effect;

import hydrafp.io.core.adt.Try;
import hydrafp.io.core.adt.Either;
import hydrafp.io.core.adt.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EffectTest {

    private Runtime runtime;

    @BeforeEach
    void setUp() {
        runtime = new SimpleRuntime();
    }

    @AfterEach
    void tearDown() {
        ((SimpleRuntime) runtime).shutdown();
    }

    @Nested
    @DisplayName("PureEffect Tests")
    class PureEffectTests {

        @Test
        @DisplayName("Pure effect should return the wrapped value")
        void pureEffectReturnsWrappedValue() {
            Effect<String> effect = Effects.pure("test");
            assertEquals("test", effect.unsafeRunSync(runtime));
        }

        @Test
        @DisplayName("Pure effect map should transform the value")
        void pureEffectMapTransformsValue() {
            Effect<String> effect = Effects.pure("test");
            Effect<Integer> mapped = effect.map(String::length);
            assertEquals(4, mapped.unsafeRunSync(runtime));
        }

        @Test
        @DisplayName("Pure effect flatMap should compose effects")
        void pureEffectFlatMapComposesEffects() {
            Effect<String> effect = Effects.pure("test");
            Effect<Integer> flatMapped = effect.flatMap(s -> Effects.pure(s.length()));
            assertEquals(4, flatMapped.unsafeRunSync(runtime));
        }
    }

    @Nested
    @DisplayName("IOEffect Tests")
    class IOEffectTests {

        @Test
        @DisplayName("IO effect should execute the IO operation")
        void ioEffectExecutesOperation() {
            Effect<String> effect = Effects.fromIO(r -> "io result");
            assertEquals("io result", effect.unsafeRunSync(runtime));
        }

        @Test
        @DisplayName("IO effect should handle exceptions")
        void ioEffectHandlesExceptions() {
            Effect<String> effect = Effects.fromIO(r -> { throw new RuntimeException("error"); });
            Try<String> result = effect.attempt(runtime);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure() instanceof RuntimeException);
        }

        @Test
        @DisplayName("IO effect recover should handle exceptions")
        void ioEffectRecoverHandlesExceptions() {
            Effect<String> effect = Effects.<String>fail(new RuntimeException("error"))
                    .recover(ex -> "recovered");
            assertEquals("recovered", effect.unsafeRunSync(runtime));
        }
    }

    @Nested
    @DisplayName("Effect Utility Methods Tests")
    class EffectUtilityMethodsTests {

        @Test
        @DisplayName("fromTry should create an effect from a Try")
        void fromTryCreatesEffect() {
            Try<String> try_ = Try.success("try test");
            Effect<String> effect = Effects.fromTry(try_);
            assertEquals("try test", effect.unsafeRunSync(runtime));
        }

        @Test
        @DisplayName("fromEither should create an effect from an Either")
        void fromEitherCreatesEffect() {
            Either<String, Integer> either = Either.right(42);
            Effect<Integer> effect = Effects.fromEither(either);
            assertEquals(42, effect.unsafeRunSync(runtime));
        }

        @Test
        @DisplayName("delay should create a delayed effect")
        void delayShouldCreateDelayedEffect() {
            AtomicInteger counter = new AtomicInteger(0);
            Effect<Integer> effect = Effects.delay(counter::incrementAndGet);
            assertEquals(0, counter.get()); // Not executed yet
            assertEquals(1, effect.unsafeRunSync(runtime));
            assertEquals(1, counter.get()); // Executed once
        }


        @Test
        @DisplayName("retry should retry failed effects")
        void retryShouldRetryFailedEffects() {
            AtomicInteger attempts = new AtomicInteger(0);
            Effect<String> failingEffect = Effects.fromIO(r -> {
                if (attempts.incrementAndGet() < 3) {
                    throw new RuntimeException("Failing");
                }
                return "Success";
            });
            Effect<String> retryingEffect = Effects.retry(failingEffect, 3);
            assertEquals("Success", retryingEffect.unsafeRunSync(runtime));
            assertEquals(3, attempts.get());
        }

        @Test
        @DisplayName("memoize should cache effect results")
        void memoizeShouldCacheEffectResults() {
            AtomicInteger counter = new AtomicInteger(0);
            Effect<Integer> effect = Effects.memoize(Effects.fromIO(r -> counter.incrementAndGet()));
            assertEquals(1, effect.unsafeRunSync(runtime));
            assertEquals(1, effect.unsafeRunSync(runtime));
            assertEquals(1, counter.get());
        }
    }

    @Nested
    @DisplayName("Defer Tests")
    class DeferTests {
        @Test
        @DisplayName("defer should create a new effect each time it's run")
        void deferShouldCreateNewEffectEachRun() {
            AtomicInteger counter = new AtomicInteger(0);
            Effect<Integer> effect = Effects.defer(() -> Effects.pure(counter.incrementAndGet()));
            assertEquals(0, counter.get(), "Counter should not be incremented before execution");
            assertEquals(1, effect.unsafeRunSync(runtime), "First execution should return 1");
            assertEquals(2, effect.unsafeRunSync(runtime), "Second execution should return 2");
            assertEquals(2, counter.get(), "Counter should be incremented twice");
        }

        @Test
        @DisplayName("deferMemoized should create the effect only once")
        void deferMemoizedShouldCreateEffectOnlyOnce() {
            AtomicInteger counter = new AtomicInteger(0);
            Effect<Integer> effect = Effects.deferMemoized(() -> Effects.pure(counter.incrementAndGet()));
            assertEquals(0, counter.get(), "Counter should not be incremented before execution");
            assertEquals(1, effect.unsafeRunSync(runtime), "First execution should return 1");
            assertEquals(1, effect.unsafeRunSync(runtime), "Second execution should also return 1");
            assertEquals(1, counter.get(), "Counter should be incremented only once");
        }

        @Test
        @DisplayName("defer should handle exceptions in effect creation")
        void deferShouldHandleExceptionsInEffectCreation() {
            Effect<String> effect = Effects.defer(() -> {
                throw new RuntimeException("Effect creation failed");
            });
            Try<String> result = effect.attempt(runtime);
            assertTrue(result.isFailure(), "Result should be a failure");
            assertInstanceOf(RuntimeException.class, result.getFailure(), "Failure should be a RuntimeException");
            assertEquals("Effect creation failed", result.getFailure().getMessage(), "Failure message should match");
        }

        @Test
        @DisplayName("deferMemoized should handle exceptions in effect creation")
        void deferMemoizedShouldHandleExceptionsInEffectCreation() {
            AtomicInteger attempts = new AtomicInteger(0);
            Effect<String> effect = Effects.deferMemoized(() -> {
                attempts.incrementAndGet();
                throw new RuntimeException("Effect creation failed");
            });
            Try<String> result1 = effect.attempt(runtime);
            Try<String> result2 = effect.attempt(runtime);
            assertTrue(result1.isFailure(), "First result should be a failure");
            assertTrue(result2.isFailure(), "Second result should also be a failure");
            assertEquals(1, attempts.get(), "Effect creation should only be attempted once");
            assertInstanceOf(RuntimeException.class, result1.getFailure(), "Failure should be a RuntimeException");
            assertEquals("Effect creation failed", result1.getFailure().getMessage(), "Failure message should match");
        }

        @Test
        @DisplayName("defer should be thread-safe")
        void deferShouldBeThreadSafe() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger(0);
            Effect<Integer> effect = Effects.defer(() -> Effects.pure(counter.incrementAndGet()));
            int threadCount = 100;
            CountDownLatch latch = new CountDownLatch(threadCount);
            Set<Integer> results = Collections.synchronizedSet(new HashSet<>());

            for (int i = 0; i < threadCount; i++) {
                new Thread(() -> {
                    results.add(effect.unsafeRunSync(runtime));
                    latch.countDown();
                }).start();
            }

            latch.await(5, TimeUnit.SECONDS);
            assertEquals(threadCount, results.size(), "Each thread should get a unique value");
        }

        @Test
        @DisplayName("deferMemoized should be thread-safe")
        void deferMemoizedShouldBeThreadSafe() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger(0);
            Effect<Integer> effect = Effects.deferMemoized(() -> Effects.pure(counter.incrementAndGet()));
            int threadCount = 100;
            CountDownLatch latch = new CountDownLatch(threadCount);
            Set<Integer> results = Collections.synchronizedSet(new HashSet<>());

            for (int i = 0; i < threadCount; i++) {
                new Thread(() -> {
                    results.add(effect.unsafeRunSync(runtime));
                    latch.countDown();
                }).start();
            }

            latch.await(5, TimeUnit.SECONDS);
            assertEquals(1, results.size(), "All threads should get the same value");
            assertEquals(1, counter.get(), "Counter should be incremented only once");
        }
    }


    @Nested
    @DisplayName("Asynchronous Execution Tests")
    class AsynchronousExecutionTests {

        @Test
        @DisplayName("runAsync should execute effect asynchronously")
        void runAsyncExecutesAsynchronously() throws ExecutionException, InterruptedException {
            Effect<String> effect = Effects.pure("async test");
            CompletableFuture<String> future = effect.runAsync(runtime);
            assertEquals("async test", future.get());
        }

        @Test
        @DisplayName("runAsync should handle exceptions")
        void runAsyncHandlesExceptions() {
            Effect<String> effect = Effects.fromIO(r -> { throw new RuntimeException("async error"); });
            CompletableFuture<String> future = effect.runAsync(runtime);
            assertThrows(ExecutionException.class, future::get);
        }
    }

    @Nested
    @DisplayName("Effect Conversion Tests")
    class EffectConversionTests {

        @Test
        @DisplayName("toEither should convert effect to Either")
        void toEitherConvertsEffect() {
            IOEffect<String> effect = (IOEffect<String>) Effects.pure("test");
            Either<Throwable, String> either = effect.toEither(runtime);
            assertTrue(either.isRight());
            assertEquals("test", either.getRight());
        }

        @Test
        @DisplayName("toOption should convert effect to Option")
        void toOptionConvertsEffect() {
            IOEffect<String> effect = (IOEffect<String>) Effects.pure("test");
            Option<String> option = effect.toOption(runtime);
            assertTrue(option.isDefined());
            assertEquals("test", option.get());
        }
    }

    @Nested
    @DisplayName("Runtime Tests")
    class RuntimeTests {

        @Test
        @DisplayName("SimpleRuntime should provide an executor")
        void simpleRuntimeProvidesExecutor() {
            SimpleRuntime runtime = new SimpleRuntime();
            assertNotNull(runtime.getExecutor());
            runtime.shutdown();
        }

        @Test
        @DisplayName("SimpleRuntime with custom thread pool size")
        void simpleRuntimeWithCustomThreadPoolSize() {
            SimpleRuntime runtime = new SimpleRuntime(5);
            assertNotNull(runtime.getExecutor());
            runtime.shutdown();
        }
    }
}