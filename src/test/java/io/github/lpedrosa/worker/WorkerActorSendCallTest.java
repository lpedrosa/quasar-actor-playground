package io.github.lpedrosa.worker;

import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.common.util.Exceptions;
import co.paralleluniverse.fibers.FiberFactory;
import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import io.github.lpedrosa.worker.messages.DoSomeWork;
import io.github.lpedrosa.worker.messages.WorkerCallMessage;
import io.github.lpedrosa.worker.messages.WorkerCastMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkerActorSendCallTest {

    private static final FiberFactory TEST_SCHEDULER = new FiberForkJoinScheduler("test-scheduler", 4);

    private Server<WorkerCallMessage, String, WorkerCastMessage> server;

    @Before
    public void setUp() {
        WorkerActor actor = new WorkerActor();
        this.server = actor.spawn(TEST_SCHEDULER);
    }

    @After
    public void tearDown() {
        this.server.shutdown();
    }

    @Test
    public void askingForTheWeatherShouldWork() throws InterruptedException, SuspendExecution {
        String reply = this.server.call(new DoSomeWork("weather"));

        // message should have something
        assertNotEquals(reply.length(), 0);
    }

    @Test(expected = WorkerException.class)
    public void askingForSomethingElseThanTheWeatherShouldFail() throws WorkerException, InterruptedException, SuspendExecution {
        try {
            String reply = this.server.call(new DoSomeWork("something else!"));
        } catch (RuntimeException e) {
            Throwable t = Exceptions.unwrap(e);
            if (t instanceof WorkerException)
                throw (WorkerException) t;
            else
                fail("Call threw some weird exception...");
        }

        fail("Calling the actor should have failed...");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void askingForUnhandledCallMessageShouldAlsoFail() throws InterruptedException, SuspendExecution {
        WorkerCallMessage fakeMessage = new WorkerCallMessage() {};
        String reply = this.server.call(fakeMessage);

        fail("Calling the actor should have failed...");
    }
}