/********************************************************************************
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information regarding copyright
 * ownership.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package org.eclipse.keyple.plugin.android.cone2;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.keyple.core.seproxy.plugin.AbstractObservableReader;
import org.eclipse.keyple.core.seproxy.plugin.AbstractStaticPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Enables Keyple to communicate with the the Android device embedded NFC reader. In the Android
 * platform, NFC reader must be link to an application activity.
 *
 *
 *
 *
 *
 */

public final class AndroidCone2Plugin extends AbstractStaticPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(AndroidCone2Plugin.class);

    private static AndroidCone2Plugin uniqueInstance;

    static final String PLUGIN_NAME = "AndroidCone2Plugin";

    private final Map<String, String> parameters = new HashMap<String, String>();// not in use in this

    // plugin

    private AndroidCone2Plugin() {
        super(PLUGIN_NAME);
    }

    public static AndroidCone2Plugin getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new AndroidCone2Plugin();
        }

        return uniqueInstance;
    }

    @Override
    public Map<String, String> getParameters() {
        LOG.warn("Android NFC Plugin does not support parameters, see AndroidNfcReader instead");
        return parameters;
    }

    @Override
    public void setParameter(String key, String value) {
        LOG.warn("Android NFC Plugin does not support parameters, see AndroidNfcReader instead");
        parameters.put(key, value);
    }


    /**
     * For an Android NFC device, the Android NFC Plugin manages only one @{@link AndroidCone2Reader}.
     * 
     * @return SortedSet<ProxyReader> : contains only one element, the
     *         singleton @{@link AndroidCone2Reader}
     */
    @Override
    protected SortedSet<AbstractObservableReader> initNativeReaders() {
        LOG.debug("InitNativeReader() add the unique instance of AndroidNfcReader");
        // return the only one reader in a list
        SortedSet<AbstractObservableReader> readers = new TreeSet<AbstractObservableReader>();
        readers.add(AndroidCone2Reader.getInstance());
        return readers;
    }


    /**
     * Return the AndroidNfcReader whatever is the provided name
     * 
     * @param name : name of the reader to retrieve
     * @return instance of @{@link AndroidCone2Reader}
     */
    @Override
    protected AbstractObservableReader fetchNativeReader(String name) {
        return readers.first();
    }
}
