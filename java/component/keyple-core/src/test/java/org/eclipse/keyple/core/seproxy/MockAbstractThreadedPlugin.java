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
package org.eclipse.keyple.core.seproxy;

import java.util.Map;
import java.util.SortedSet;
import org.eclipse.keyple.core.seproxy.exception.KeypleBaseException;
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException;
import org.eclipse.keyple.core.seproxy.plugin.AbstractReader;
import org.eclipse.keyple.core.seproxy.plugin.AbstractThreadedObservablePlugin;

public class MockAbstractThreadedPlugin extends AbstractThreadedObservablePlugin {


    protected MockAbstractThreadedPlugin(String name) {
        super(name);
    }

    @Override
    protected SortedSet<String> fetchNativeReadersNames() throws KeypleReaderException {
        return null;
    }

    @Override
    protected SortedSet<SeReader> initNativeReaders() throws KeypleReaderException {
        return null;
    }

    @Override
    protected AbstractReader fetchNativeReader(String name) throws KeypleReaderException {
        return null;
    }

    @Override
    public Map<String, String> getParameters() {
        return null;
    }

    @Override
    public void setParameter(String key, String value)
            throws IllegalArgumentException, KeypleBaseException {

    }
}