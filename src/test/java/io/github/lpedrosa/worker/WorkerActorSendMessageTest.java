package io.github.lpedrosa.worker;

import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import io.github.lpedrosa.worker.messages.DoSomeWork;
import io.github.lpedrosa.worker.messages.WorkerCallMessage;
import io.github.lpedrosa.worker.messages.WorkerCastMessage;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkerActorSendMessageTest {

    private static final FiberFactory TEST_SCHEDULER = new FiberForkJoinScheduler("test-scheduler", 4);

    @Test
    public void askingForTheWeatherShouldWork() throws WorkerException, SuspendExecution, InterruptedException {
        WorkerActor actor = new WorkerActor();

        Server<WorkerCallMessage, String, WorkerCastMessage> server = actor.spawn(TEST_SCHEDULER);

        String reply = server.call(new DoSomeWork("weather"));

        // message should have something
        assertNotEquals(reply.length(), 0);
    }
}