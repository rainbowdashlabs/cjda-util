/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.message;

public class MessageTest {
    void test(){
        Message.of("test")
                .handler(null);
    }
}
