package io.github.lpedrosa.worker;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.ServerActor;
import co.paralleluniverse.fibers.SuspendExecution;
import io.github.lpedrosa.worker.messages.DoSomeWork;
import io.github.lpedrosa.worker.messages.WorkerCallMessage;
import io.github.lpedrosa.worker.messages.WorkerCastMessage;

public class WorkerActor extends ServerActor<WorkerCallMessage, String, WorkerCastMessage> {

    @Override
    protected String handleCall(ActorRef<?> from, Object id, WorkerCallMessage m) throws Exception, SuspendExecution {
        if (m instanceof DoSomeWork)
            return handleDoSomeWork((DoSomeWork) m);
        else
            // this will throw UnsupportedOperationException
            return super.handleCall(from, id, m);
    }

    private String handleDoSomeWork(DoSomeWork m) throws  WorkerException, SuspendExecution {
        if ("weather".equals(m.getTaskType()))
            return "It's quite sunny";
        else
            throw new WorkerException("I'm pretty useless at that... [taskType:" + m.getTaskType() + "]");
    }
}
