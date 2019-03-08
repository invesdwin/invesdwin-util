package de.invesdwin.util.shutdown;

/**
 * Gets called when the JVM is cleanly shut down.
 * 
 * Spring beans with this annotation are automatically registered. Else for normal classes
 * ShutdownHookManager.register() must be called manually.
 * 
 * @author subes
 * 
 */
public interface IShutdownHook {

    void shutdown() throws Exception;

}
