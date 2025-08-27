/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class PageBuilder {
    public static StandardPageBuilder standard(int pages) {
        return new StandardPageBuilder(pages);
    }

    public static <T> ListPageBuilder<T> list(List<T> list) {
        return new ListPageBuilder<>(list);
    }

    public abstract static class BasePageBuilder<T extends BasePageBuilder<T, V>, V> {

        protected ISnowflake user;
        protected Function<V, List<PageButton>> buttons = b -> Collections.emptyList();
        protected Function<V, MessageEditData> page;
        protected Function<V, MessageEditData> empty;

        public T syncPage(Function<V, MessageEditData> page) {
            this.page = page;
            return self();
        }

        public T syncEmptyPage(Function<V, MessageEditData> page) {
            this.empty = page;
            return self();
        }

        public T forUser(ISnowflake user) {
            this.user = user;
            return self();
        }

        public abstract IPageBag build();

        private T self() {
            return (T) this;
        }
    }

    public static class ListPageBuilder<T> extends BasePageBuilder<ListPageBuilder<T>, IListPageBag<T>> {

        private final List<T> list;

        public ListPageBuilder(List<T> list) {
            this.list = list;
        }

        public IPageBag build() {
            if (user != null) {
                return new PrivateListPageBag<>(list, user.getIdLong()) {
                    @Override
                    public List<PageButton> buttons() {
                        return buttons.apply(this);
                    }

                    @Override
                    public MessageEditData buildPage() {
                        return page.apply(this);
                    }

                    @Override
                    public MessageEditData buildEmptyPage() {
                        if (empty == null) super.buildEmptyPage();
                        return empty.apply(this);
                    }
                };
            }
            return new ListPageBag<>(list) {
                @Override
                public List<PageButton> buttons() {
                    return buttons.apply(this);
                }

                @Override
                public MessageEditData buildPage() {
                    return page.apply(this);
                }

                @Override
                public MessageEditData buildEmptyPage() {
                    if (empty == null) super.buildEmptyPage();
                    return empty.apply(this);
                }
            };
        }
    }

    public static class StandardPageBuilder extends BasePageBuilder<StandardPageBuilder, PageBag> {
        private final int pages;

        public StandardPageBuilder(int pages) {
            this.pages = pages;
        }

        @Override
        public IPageBag build() {
            if (user != null) {
                return new PrivatePageBag(pages, user.getIdLong()) {
                    @Override
                    public List<PageButton> buttons() {
                        return buttons.apply(this);
                    }

                    @Override
                    public MessageEditData buildPage() {
                        return page.apply(this);
                    }

                    @Override
                    public MessageEditData buildEmptyPage() {
                        if (empty == null) super.buildEmptyPage();
                        return empty.apply(this);
                    }
                };
            }
            return new PageBag(pages) {
                @Override
                public List<PageButton> buttons() {
                    return buttons.apply(this);
                }

                @Override
                public MessageEditData buildPage() {
                    return page.apply(this);
                }

                @Override
                public MessageEditData buildEmptyPage() {
                    if (empty == null) super.buildEmptyPage();
                    return empty.apply(this);
                }
            };
        }
    }
}
