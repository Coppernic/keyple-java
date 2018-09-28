/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.eclipse.keyple.plugin.stub;



import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.keyple.calypso.command.po.PoRevision;
import org.eclipse.keyple.calypso.command.po.builder.IncreaseCmdBuild;
import org.eclipse.keyple.calypso.command.po.builder.ReadRecordsCmdBuild;
import org.eclipse.keyple.seproxy.*;
import org.eclipse.keyple.seproxy.event.ReaderEvent;
import org.eclipse.keyple.seproxy.exception.KeypleChannelStateException;
import org.eclipse.keyple.seproxy.exception.KeypleIOReaderException;
import org.eclipse.keyple.seproxy.exception.KeypleReaderException;
import org.eclipse.keyple.seproxy.exception.NoStackTraceThrowable;
import org.eclipse.keyple.seproxy.protocol.ContactlessProtocols;
import org.eclipse.keyple.seproxy.protocol.SeProtocolSetting;
import org.eclipse.keyple.util.ByteBufferUtils;
import org.eclipse.keyple.util.Observable;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StubReaderTest {

    StubReader reader;

    Logger logger = LoggerFactory.getLogger(StubReaderTest.class);

    // init before each test
    @Before
    public void SetUp() throws InterruptedException, KeypleReaderException {
        // clear observers from others tests as StubPlugin is a singleton

        StubPlugin stubPlugin = StubPlugin.getInstance();

        logger.info("Stubplugin readers size {}", stubPlugin.getReaders().size());
        Assert.assertEquals(0, stubPlugin.getReaders().size());

        logger.info("Stubplugin observers size {}", stubPlugin.countObservers());
        Assert.assertEquals(0, stubPlugin.countObservers());

        reader = StubPlugin.getInstance().plugStubReader("StubReaderTest");
        Thread.sleep(500);

    }

    @After
    public void tearDown() throws InterruptedException, KeypleReaderException {
        StubPlugin.getInstance().clearObservers();
        StubPlugin.getInstance().unplugReader("StubReaderTest");
        Thread.sleep(500);

    }


    /*
     * TRANSMIT
     */


    @Test
    public void testInsert() throws NoStackTraceThrowable {
        // add observer
        reader.addObserver(new Observable.Observer<ReaderEvent>() {
            @Override
            public void update(ReaderEvent event) {
                Assert.assertEquals(event.getReaderName(), reader.getName());
                Assert.assertEquals(event.getPluginName(), StubPlugin.getInstance().getName());
                Assert.assertEquals(ReaderEvent.EventType.SE_INSERTED, event.getEventType());

            }
        });
        // test
        reader.insertSe(hoplinkSE());

        // assert
        Assert.assertTrue(reader.isSePresent());
    }

    @Test
    public void testATR() {
        // add observer
        reader.addObserver(new Observable.Observer<ReaderEvent>() {
            @Override
            public void update(ReaderEvent event) {
                SeRequest atrRequest = new SeRequest(new SeRequest.AtrSelector("3B.*"), null, true);


                try {
                    SeResponse atrResponse =
                            reader.transmit(new SeRequestSet(atrRequest)).getSingleResponse();

                    Assert.assertNotNull(atrResponse);

                } catch (KeypleReaderException e) {
                    Assert.fail();
                }

            }
        });
        // test
        reader.insertSe(hoplinkSE());

        // assert
        Assert.assertTrue(reader.isSePresent());
    }


    @Test(expected = IllegalArgumentException.class)
    public void transmit_Hoplink_null() throws Exception {
        reader.insertSe(hoplinkSE());
        reader.transmit((SeRequestSet) null);

        // throws exception
    }

    @Test
    public void transmit_Hoplink_Sucessfull() throws KeypleReaderException {
        // init Request
        SeRequestSet requests = getRequestIsoDepSetSample();

        // init SE
        reader.insertSe(hoplinkSE());

        // add Protocol flag
        reader.addSeProtocolSetting(
                new SeProtocolSetting(StubProtocolSetting.SETTING_PROTOCOL_ISO14443_4));
        // test
        SeResponseSet seResponse = reader.transmit(requests);

        // assert
        Assert.assertTrue(seResponse.getSingleResponse().getApduResponses().get(0).isSuccessful());
    }


    // @Test
    // public void transmit_null_Selection() throws KeypleReaderException {
    // // init SE
    // // no SE
    //
    // // init request
    // SeRequestSet seRequest = getRequestIsoDepSetSample();
    //
    // // add Protocol flag
    // reader.addSeProtocolSetting(
    // new SeProtocolSetting(StubProtocolSetting.SETTING_PROTOCOL_ISO14443_4));
    //
    // // test
    // SeResponseSet resp = reader.transmit(seRequest);
    //
    // Assert.assertNull(resp.getSingleResponse());
    // }


    @Test(expected = KeypleReaderException.class)
    public void transmit_no_response() throws KeypleReaderException {
        // init Request
        SeRequestSet requests = getNoResponseRequest();

        // init SE
        reader.insertSe(noResponseToCommandSE());

        // add Protocol flag
        reader.addSeProtocolSetting(
                new SeProtocolSetting(StubProtocolSetting.SETTING_PROTOCOL_ISO14443_4));
        // test
        SeResponseSet seResponse = reader.transmit(requests);
    }

    /*
     * NAME and PARAMETERS
     */

    @Test
    public void testGetName() {
        Assert.assertNotNull(reader.getName());
    }

    // Set wrong parameter
    @Test(expected = KeypleReaderException.class)
    public void testSetWrongParameter() throws Exception {
        reader.setParameter("WRONG_PARAMETER", "a");
    }

    // Set wrong parameters
    @Test(expected = KeypleReaderException.class)
    public void testSetWrongParameters() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("WRONG_PARAMETER", "d");
        parameters.put(StubReader.ALLOWED_PARAMETER_1, "a");
        reader.setParameters(parameters);
    }

    // Set correct paramaters
    @Test
    public void testSetParameters() throws Exception {
        Map<String, String> p1 = new HashMap<String, String>();
        p1.put(StubReader.ALLOWED_PARAMETER_1, "a");
        p1.put(StubReader.ALLOWED_PARAMETER_2, "a");

        reader.setParameters(p1);
        Map<String, String> p2 = reader.getParameters();
        assert (p1.equals(p2));


    }



    /*
     * HELPERS
     */


    private SeRequestSet getRequestIsoDepSetSample() {
        String poAid = "A000000291A000000191";

        ReadRecordsCmdBuild poReadRecordCmd_T2Env = new ReadRecordsCmdBuild(PoRevision.REV3_1,
                (byte) 0x14, (byte) 0x01, true, (byte) 0x20);

        List<ApduRequest> poApduRequestList;

        poApduRequestList = Arrays.asList(poReadRecordCmd_T2Env.getApduRequest());

        SeRequest.Selector selector = new SeRequest.AidSelector(ByteBufferUtils.fromHex(poAid));

        SeRequest seRequest = new SeRequest(selector, poApduRequestList, false,
                ContactlessProtocols.PROTOCOL_ISO14443_4);

        return new SeRequestSet(seRequest);

    }

    /*
     * No Response: increase command is not defined in the StubSE
     *
     * An Exception will be thrown.
     */
    private SeRequestSet getNoResponseRequest() {
        String poAid = "A000000291A000000191";

        IncreaseCmdBuild poIncreaseCmdBuild =
                new IncreaseCmdBuild(PoRevision.REV3_1, (byte) 0x14, (byte) 0x01, 0);

        List<ApduRequest> poApduRequestList;

        poApduRequestList = Arrays.asList(poIncreaseCmdBuild.getApduRequest());

        SeRequest.Selector selector = new SeRequest.AidSelector(ByteBufferUtils.fromHex(poAid));

        SeRequest seRequest = new SeRequest(selector, poApduRequestList, false,
                ContactlessProtocols.PROTOCOL_ISO14443_4);

        return new SeRequestSet(seRequest);

    }

    private StubSecureElement hoplinkSE() {


        return new StubSecureElement() {

            @Override
            public ByteBuffer processApdu(ByteBuffer apduIn) throws KeypleIOReaderException {

                addHexCommand("00 A4 04 00 0A A0 00 00 02 91 A0 00 00 01 91 00",
                        "6F25840BA000000291A00000019102A516BF0C13C70800000000C0E11FA653070A3C230C1410019000");
                addHexCommand("00 B2 01 A4 20",
                        "00000000000000000000000000000000000000000000000000000000000000009000");

                return super.processApdu(apduIn);
            }

            @Override
            public ByteBuffer getATR() {
                return ByteBufferUtils
                        .fromHex("3B 8E 80 01 80 31 80 66 40 90 89 12 08 02 83 01 90 00 0B");
            }

            @Override
            public String getSeProcotol() {
                return "PROTOCOL_ISO14443_4";
            }
        };



    }

    private StubSecureElement noResponseToCommandSE() {
        return new StubSecureElement() {

            @Override
            public ByteBuffer processApdu(ByteBuffer apduIn) throws KeypleIOReaderException {

                addHexCommand("00 A4 04 00 0A A0 00 00 02 91 A0 00 00 01 91 00",
                        "6F25840BA000000291A00000019102A516BF0C13C70800000000C0E11FA653070A3C230C1410019000");

                return super.processApdu(apduIn);
            }

            @Override
            public ByteBuffer getATR() {
                return ByteBufferUtils
                        .fromHex("3B 8E 80 01 80 31 80 66 40 90 89 12 08 02 83 01 90 00 0B");
            }

            @Override
            public String getSeProcotol() {
                return "PROTOCOL_ISO14443_4";
            }
        };
    }

    private StubSecureElement getSENoconnection() {
        return new StubSecureElement() {
            @Override
            public ByteBuffer getATR() {
                return null;
            }

            @Override
            public boolean isPhysicalChannelOpen() {
                return false;
            }

            // override methods to fail open connection
            @Override
            public void openPhysicalChannel() throws KeypleChannelStateException {
                throw new KeypleChannelStateException("Impossible to estasblish connection");
            }

            @Override
            public void closePhysicalChannel() throws KeypleChannelStateException {
                throw new KeypleChannelStateException("Channel is not open");
            }

            @Override
            public ByteBuffer processApdu(ByteBuffer apduIn) throws KeypleIOReaderException {
                throw new KeypleIOReaderException("Error while transmitting apdu");
            }

            @Override
            public String getSeProcotol() {
                return null;
            }
        };

    }


    static ApduRequest getApduSample() {
        return new ApduRequest(ByteBufferUtils.fromHex("FEDCBA98 9005h"), false);
    }



}
