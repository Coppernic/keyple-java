/*
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License version 2.0 which accompanies this distribution, and is
 * available at https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 */

package org.keyple.calypso.commands.po.builder;

import java.nio.ByteBuffer;
import org.keyple.calypso.commands.SendableInSession;
import org.keyple.calypso.commands.po.AbstractPoCommandBuilder;
import org.keyple.calypso.commands.po.CalypsoPoCommands;
import org.keyple.calypso.commands.po.PoRevision;
import org.keyple.calypso.commands.utils.RequestUtils;
import org.keyple.commands.CommandsTable;
import org.keyple.commands.InconsistentCommandException;
import org.keyple.seproxy.ApduRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class AppendRecordCmdBuild. This class provides the dedicated constructor to build the Update
 * Record APDU command.
 *
 * @author Ixxi
 *
 */
public class AppendRecordCmdBuild extends AbstractPoCommandBuilder implements SendableInSession {

    /** The command. */
    private static CommandsTable command = CalypsoPoCommands.APPEND_RECORD;

    /**
     * Instantiates a new append record cmd build.
     *
     * @param commandeReference the commande reference
     * @param request the request
     * @throws InconsistentCommandException the inconsistent command exception
     */
    AppendRecordCmdBuild(CommandsTable commandeReference, ApduRequest request)
            throws InconsistentCommandException {
        super(commandeReference, request);
    }

    /**
     * Instantiates a new UpdateRecordCmdBuild.
     *
     * @param revision the revision of the PO
     * @param sfi the sfi to select
     * @param newRecordData the new record data to write
     * @throws InconsistentCommandException the inconsistent command exception
     */
    public AppendRecordCmdBuild(PoRevision revision, byte sfi, ByteBuffer newRecordData) {
        super(command, null);
        if (revision != null) {
            this.defaultRevision = revision;
        }
        byte cla = PoRevision.REV2_4.equals(this.defaultRevision) ? (byte) 0x94 : (byte) 0x00;
        byte p1 = (byte) 0x00;
        byte p2 = (sfi == 0) ? (byte) 0x00 : (byte) (sfi * 8);

        this.request = RequestUtils.constructAPDURequest(cla, command, p1, p2, newRecordData);
    }


    /**
     * Instantiates a new append record cmd build.
     *
     * @param request the request
     * @throws InconsistentCommandException the inconsistent command exception
     */
    public AppendRecordCmdBuild(ApduRequest request) throws InconsistentCommandException {
        super(CalypsoPoCommands.APPEND_RECORD, request);
        RequestUtils.controlRequestConsistency(command, request);
    }

}
