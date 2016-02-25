package io.github.lpedrosa.lifecycle;

import co.paralleluniverse.actors.LocalActor;
import co.paralleluniverse.actors.ShutdownMessage;
import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import io.github.lpedrosa.worker.messages.WorkerCallMessage;
import io.github.lpedrosa.worker.messages.WorkerCastMessage;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

public class LifecycleActorNormalTerminateTest {

    private static final FiberFactory TEST_SCHEDULER = new FiberForkJoinScheduler("test-scheduler", 4);

    @Test
    public void terminateShouldBeCalledOnNormalShutdown() throws ExecutionException, InterruptedException, SuspendExecution {
        LifecycleActor actor = new LifecycleActor("terminate-test-normal");
        Server<WorkerCallMessage, String, WorkerCastMessage> server = actor.spawn(TEST_SCHEDULER);
        server.shutdown();

        LocalActor.join(server);
    }

    @Test
    public void terminateShouldBeCalledOnWhenShutdownMessageIsSent() throws ExecutionException, InterruptedException, SuspendExecution {
        LifecycleActor actor = new LifecycleActor("terminate-test-normal-with-message");
        Server<WorkerCallMessage, String, WorkerCastMessage> server = actor.spawn(TEST_SCHEDULER);
        server.send(new ShutdownMessage(null));

        LocalActor.join(server);
    }

    @Test(expected = ExecutionException.class)
    public void terminateShouldBeCalledOnException() throws ExecutionException, InterruptedException, SuspendExecution {
        LifecycleActor actor = new LifecycleActor("terminate-test-exception");
        Server<WorkerCallMessage, String, WorkerCastMessage> server = actor.spawn(TEST_SCHEDULER);
        server.send(new Exception("lol"));

        LocalActor.join(server);
    }

}
