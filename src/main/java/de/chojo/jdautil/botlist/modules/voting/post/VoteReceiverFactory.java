/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting.post;

import de.chojo.jdautil.botlist.modules.shared.AuthHandler;
import de.chojo.jdautil.botlist.modules.voting.post.payload.BotlistMe;
import de.chojo.jdautil.botlist.modules.voting.post.payload.DiscordbotlistCom;
import de.chojo.jdautil.botlist.modules.voting.post.payload.TopGg;

public interface VoteReceiverFactory {

    VoteReceiverFactory TOP_GG = key -> VoteReceiver.create(TopGg.class, AuthHandler.of(key));
    VoteReceiverFactory DISCORDBOTLIST_COM = key -> VoteReceiver.create(DiscordbotlistCom.class, AuthHandler.of(key));
    VoteReceiverFactory BOTLIST_ME = key -> VoteReceiver.create(BotlistMe.class, AuthHandler.of(key));

    VoteReceiver<?> build(String key);
}
