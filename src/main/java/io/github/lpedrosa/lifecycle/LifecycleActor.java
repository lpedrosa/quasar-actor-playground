package io.github.lpedrosa.lifecycle;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.ExitMessage;
import co.paralleluniverse.actors.LifecycleException;
import co.paralleluniverse.actors.behaviors.ServerActor;
import co.paralleluniverse.common.util.Exceptions;
import co.paralleluniverse.fibers.SuspendExecution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifecycleActor extends ServerActor {

    private final static Logger LOG = LoggerFactory.getLogger(LifecycleActor.class);

    private ActorRef sibling;

    public LifecycleActor(String name) {
        this(name, null);
    }

    public LifecycleActor(String name, ActorRef<Object> sibling) {
        super(name);
        this.sibling = sibling;
    }

    @Override
    protected void init() throws InterruptedException, SuspendExecution {
        link(this.sibling);
    }

    @Override
    protected void handleInfo(Object m) throws SuspendExecution {
        // blow up with given exception
        if (m instanceof Exception)
            Exceptions.rethrow((Exception)m);
    }

    @Override
    protected void terminate(Throwable cause) throws SuspendExecution {
        if (cause != null) {
            handleTerminateOnError(cause);
        } else {
            // normal
            LOG.info("Terminating actor [name:{}] normally!", this.getName());
        }
    }

    private void handleTerminateOnError(Throwable cause) {
        if (cause instanceof LifecycleException) {
            handleLinkDeath((LifecycleException) cause);
        } else {
            LOG.info("Terminating actor [name:{}] due to cause: {}", this.getName(), Exceptions.unwrap(cause).getClass().getName());
        }
    }

    private void handleLinkDeath(LifecycleException e) {
        Throwable t = getLinkDeathCause(e);
        String msg = "Terminating actor [name:{}] because it's sibling died.";
        if (t != null)
            LOG.info(msg + " It died because: {}", this.getName(), Exceptions.unwrap(e).getClass().getName());
        LOG.info(msg, this.getName());
    }

    private Throwable getLinkDeathCause(LifecycleException cause) {
        if (cause.message() instanceof ExitMessage) {
            ExitMessage m = (ExitMessage) cause.message();
            return m.cause;
        }
        return null;
    }

}
