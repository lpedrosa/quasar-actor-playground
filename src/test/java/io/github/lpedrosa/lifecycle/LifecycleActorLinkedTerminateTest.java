package io.github.lpedrosa.lifecycle;

import co.paralleluniverse.actors.LocalActor;
import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import io.github.lpedrosa.worker.messages.WorkerCallMessage;
import io.github.lpedrosa.worker.messages.WorkerCastMessage;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

public class LifecycleActorLinkedTerminateTest {

    private static final FiberFactory TEST_SCHEDULER = new FiberForkJoinScheduler("test-scheduler", 4);

    @Test(expected = ExecutionException.class)
    public void terminateShouldBeCalledWhenLinkedActorDiesNormally() throws ExecutionException, InterruptedException, SuspendExecution {
        LifecycleActor actor = new LifecycleActor("terminate-test-linked");
        Server<WorkerCallMessage, String, WorkerCastMessage> server = actor.spawn(TEST_SCHEDULER);
        LifecycleActor actor2 = new LifecycleActor("terminate-test-linked-sibling", server);
        Server<WorkerCallMessage, String, WorkerCastMessage> server2 = actor2.spawn(TEST_SCHEDULER);

        server2.shutdown();

        LocalActor.join(server2);
        LocalActor.join(server);
    }

    @Test(expected = ExecutionException.class)
    public void terminateShouldBeCalledWhenLinkedActorDiesOnError() throws ExecutionException, InterruptedException, SuspendExecution {
        LifecycleActor actor = new LifecycleActor("terminate-test-linked-error");
        Server<WorkerCallMessage, String, WorkerCastMessage> server = actor.spawn(TEST_SCHEDULER);
        LifecycleActor actor2 = new LifecycleActor("terminate-test-linked-error-sibling", server);
        Server<WorkerCallMessage, String, WorkerCastMessage> server2 = actor2.spawn(TEST_SCHEDULER);

        server2.send(new Exception("lol"));

        LocalActor.join(server2);
        LocalActor.join(server);
    }

}
