/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination;

import de.chojo.jdautil.menus.MenuService;
import de.chojo.jdautil.pagination.bag.PageBuilder;
import de.chojo.jdautil.pagination.bag.PageButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaginationTest {

    @Test
    void pageBuilderAddsButtons() {
        var bag = PageBuilder.list(List.of("a", "b"))
                .syncPage(page -> MessageEditData.fromContent(page.currentElement()))
                .buttons(page -> List.of(PageButton.of(Button.primary("refresh", "Refresh"))))
                .build();

        assertEquals(1, bag.buttons().size());
        assertEquals("a", bag.buildPage().getContent());
    }

    @Test
    void serviceBuildersChainToBuild() {
        var shardManager = Mockito.mock(ShardManager.class);

        PageService pages = PageService.builder(shardManager)
                .previousId("prev")
                .nextId("next")
                .withCache(cache -> cache.expireAfterAccess(1, TimeUnit.MINUTES))
                .build();
        MenuService menus = MenuService.builder(shardManager)
                .withCache(cache -> cache.expireAfterAccess(1, TimeUnit.MINUTES))
                .build();

        assertNotNull(pages);
        assertNotNull(menus);
    }
}
