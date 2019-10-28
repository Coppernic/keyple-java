/********************************************************************************
 * Copyright (c) 2019 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information regarding copyright
 * ownership.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package org.eclipse.keyple.core.seproxy.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.keyple.core.seproxy.plugin.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AbstractThreadedObservableLocalReader} class implements monitoring functions for a
 * local reader based on a self-managed execution thread.
 * <p>
 * The thread is started when the first observation is added and stopped when the last observation
 * is removed.
 * <p>
 * It manages a machine in a currentState that conforms to the definitions given in
 * {@link AbstractObservableLocalReader}
 */
public abstract class AbstractThreadedObservableLocalReader extends AbstractObservableLocalReader {
    /** logger */
    private static final Logger logger =
            LoggerFactory.getLogger(AbstractThreadedObservableLocalReader.class);

    protected ExecutorService executorService;

    protected enum Timeout {
        SE_INSERTION, SE_REMOVAL
    }

    private long timeoutSeInsert = 10000;// default value
    private long timeoutSeRemoval = 10000;// default value

    /**
     * Reader constructor
     * 
     * @param pluginName the name of the plugin that instantiated the reader
     * @param readerName the name of the reader
     */
    public AbstractThreadedObservableLocalReader(String pluginName, String readerName) {
        this(pluginName, readerName, Executors.newSingleThreadExecutor());


    }

    /**
     * Reader constructor
     * 
     * @param pluginName the name of the plugin that instantiated the reader
     * @param readerName the name of the reader
     * @param executorService if needed a executor can be specify, else a embedded single threaded
     *        executor will be used
     */
    public AbstractThreadedObservableLocalReader(String pluginName, String readerName,
            ExecutorService executorService) {
        super(pluginName, readerName);
        this.executorService = executorService;
        /* Init state */
        switchState(getInitState());
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    protected Map<AbstractObservableState.MonitoringState, AbstractObservableState> initStates() {
        Map<AbstractObservableState.MonitoringState, AbstractObservableState> states =
                new HashMap<AbstractObservableState.MonitoringState, AbstractObservableState>();
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_SE_INSERTION,
                new ThreadedWaitForSeInsertion(this, timeoutSeInsert));
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_SE_PROCESSING,
                new DefaultWaitForSeProcessing(this));
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_SE_REMOVAL,
                new ThreadedWaitForSeRemoval(this, timeoutSeRemoval));
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_START_DETECTION,
                new DefaultWaitForStartDetect(this));
        return states;
    }

    /**
     * Sets the value of the delay for the designated timeout
     * 
     * @param timeout timeout identifier
     * @param value delay in milliseconds
     */
    protected void setTimeout(Timeout timeout, long value) {
        switch (timeout) {
            case SE_INSERTION:
                timeoutSeInsert = value;
                break;
            case SE_REMOVAL:
                timeoutSeRemoval = value;
                break;
        }
    }
}
