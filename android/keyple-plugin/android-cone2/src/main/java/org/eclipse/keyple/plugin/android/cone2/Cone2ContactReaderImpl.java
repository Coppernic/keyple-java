package org.eclipse.keyple.plugin.android.cone2;

import org.eclipse.keyple.core.seproxy.SeSelector;
import org.eclipse.keyple.core.seproxy.exception.KeypleApplicationSelectionException;
import org.eclipse.keyple.core.seproxy.exception.KeypleChannelStateException;
import org.eclipse.keyple.core.seproxy.exception.KeypleIOReaderException;
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException;
import org.eclipse.keyple.core.seproxy.message.ApduResponse;
import org.eclipse.keyple.core.seproxy.plugin.AbstractStaticReader;
import org.eclipse.keyple.core.seproxy.protocol.SeProtocol;
import org.eclipse.keyple.core.seproxy.protocol.TransmissionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.coppernic.sdk.ask.Defines;
import fr.coppernic.sdk.ask.Reader;
import fr.coppernic.sdk.utils.core.CpcBytes;

/**
 * Implementation of {@link org.eclipse.keyple.core.seproxy.SeReader} to communicate with SAM
 * using Coppernic C-One 2 device
 *
 */
public class Cone2ContactReaderImpl extends AbstractStaticReader implements Cone2ContactReader {
    private static int readerNumber = 1;

    private static final Logger LOG = LoggerFactory.getLogger(Cone2ContactlessReaderImpl.class);

    private final Map<String, String> parameters = new HashMap<String, String>();

    private byte samNb;

    private Reader reader;
    private byte[] atr = null;
    private AtomicBoolean isPhysicalChannelOpened = new AtomicBoolean(false);

    protected Cone2ContactReaderImpl() {
        super(PLUGIN_NAME, READER_NAME + "_" + readerNumber);
        readerNumber++;

        reader = Cone2AskReader.getInstance();

        // We set parameters to default values
        // By default, contact reader is set tom SAM 1
        parameters.put(CONTACT_INTERFACE_ID,
                CONTACT_INTERFACE_ID_SAM_1);
    }

    @Override
    protected boolean checkSePresence() {
        reader.cscSelectSam(samNb, Defines.SAM_PROT_HSP_INNOVATRON);
        int[] atrLen = new int[1];
        byte[] atr = new byte[64];
        int ret = reader.cscResetSam((byte) 0x00, atr, atrLen);
        if (ret == Defines.RCSC_Ok) {
            this.atr = new byte[atrLen[0]];
            System.arraycopy(atr, 0, this.atr, 0, this.atr.length);
            return true;
        } else {
            this.atr = null;
            return false;
        }
    }

    @Override
    protected byte[] getATR() {
        return atr;
    }

    @Override
    protected ApduResponse openChannelForAid(SeSelector.AidSelector aidSelector) throws KeypleIOReaderException, KeypleChannelStateException, KeypleApplicationSelectionException {
        return null;
    }

    @Override
    protected void openPhysicalChannel() {
        reader.cscSelectSam(samNb, Defines.SAM_PROT_HSP_INNOVATRON);
        int[] atrLen = new int[1];
        byte[] atr = new byte[64];
        int ret = reader.cscResetSam((byte) 0x00, atr, atrLen);
        if (ret == Defines.RCSC_Ok) {
            this.atr = new byte[atrLen[0]];
            System.arraycopy(atr, 0, this.atr, 0, this.atr.length);
        } else {
            this.atr = null;
        }
        isPhysicalChannelOpened.set(true);
    }

    @Override
    protected void closePhysicalChannel() {
        isPhysicalChannelOpened.set(false);
    }

    @Override
    protected boolean isPhysicalChannelOpen() {
        return isPhysicalChannelOpened.get();
    }

    @Override
    protected boolean protocolFlagMatches(SeProtocol protocolFlag) throws KeypleReaderException {
        return true;
    }

    @Override
    protected byte[] transmitApdu(byte[] apduIn) throws KeypleIOReaderException {
        LOG.debug("transmitApdu: " + CpcBytes.byteArrayToString(apduIn));
        byte[] apduAnswer;

        try {
            Cone2AskReader.acquireLock();
            byte[] answer = new byte[260];
            int[] answerLen = new int[1];

            int ret;

            if (reader != null) {
                ret = reader.cscIsoCommandSam(apduIn, apduIn.length, answer, answerLen);
            } else {
                throw new KeypleIOReaderException("Reader has not been instantiated");
            }

            int length = answerLen[0];

            if (length < 2) {
                // Hopefully, this should not happen
                apduAnswer = new byte[2];
                LOG.error("Error, empty answer");
            } else {
                // first byte is always length value. We can ignore it
                apduAnswer = new byte[length];
                System.arraycopy(answer, 0, apduAnswer, 0, apduAnswer.length);
            }
        } finally {
            Cone2AskReader.releaseLock();
        }
        LOG.debug("End transmission");

        return apduAnswer;
    }

    @Override
    public TransmissionMode getTransmissionMode() {
        return TransmissionMode.CONTACTS;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public void setParameter(String key, String value) {
        if (parameters.containsKey(key)) {
            parameters.put(key, value);
        }

        if (key.compareTo(CONTACT_INTERFACE_ID) == 0) {
            // TODO add parameter here
            samNb = (byte) Cone2ParametersUtils.getIntParam(parameters, CONTACT_INTERFACE_ID,
                    CONTACT_INTERFACE_ID_SAM_1);
        }
    }
}