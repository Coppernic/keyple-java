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
package org.eclipse.keyple.plugin.remotese.rm.json;


import java.io.IOException;
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException;
import org.eclipse.keyple.plugin.remotese.transport.model.KeypleDto;
import org.eclipse.keyple.plugin.remotese.transport.model.KeypleDtoHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class KeypleDtoHelperTest {

    private static final Logger logger = LoggerFactory.getLogger(KeypleDtoHelperTest.class);

    @Test
    public void testContainsException() {
        Exception ex = new KeypleReaderException("keyple Reader Exception message",
                new IOException("error io"));

        Throwable t =
                new IllegalStateException("illegal state  message", new IOException("error io"));

        Throwable npe = new NullPointerException("NPE  message");


        KeypleDto dtoWithException =
                KeypleDtoHelper.ExceptionDTO("any", ex, "any", "any", "any", "any", "any", "any");
        KeypleDto dtoWithThrowable =
                KeypleDtoHelper.ExceptionDTO("any", t, "any", "any", "any", "any", "any", "any");
        KeypleDto dtoWithNPE =
                KeypleDtoHelper.ExceptionDTO("any", npe, "any", "any", "any", "any", "any", "any");

        logger.debug(KeypleDtoHelper.toJson(dtoWithException));
        logger.debug(KeypleDtoHelper.toJson(dtoWithNPE));
        logger.debug(KeypleDtoHelper.toJson(dtoWithThrowable));

        Assert.assertTrue(KeypleDtoHelper.containsException(dtoWithException));
        Assert.assertTrue(KeypleDtoHelper.containsException(dtoWithThrowable));
        Assert.assertTrue(KeypleDtoHelper.containsException(dtoWithNPE));


    }

}
