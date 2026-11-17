---
title: Pagination
sidebar_position: 5
---

# Pagination

A paginated message shows one page at a time with a previous button, a page counter and a next button. You only
describe how to build a page. cjda-util builds it when the user scrolls.

Enable it on the hub:

```java
.withPagination(pages -> pages.previousText("pages.previous")
        .nextText("pages.next"))
```

`withDefaultPagination()` uses the plain texts "Previous" and "Next".

## Show a list

```java
List<String> entries = loadEntries();

context.registerPage(new ListPageBag<>(entries) {
    @Override
    public MessageEditData buildPage() {
        return MessageEditData.fromContent(currentElement());
    }
}, true);
```

The second argument makes the reply ephemeral.

## Page bags

A page bag knows how many pages there are and which one is shown. Pick the one that fits and override `buildPage()`.

| Bag | Use it for |
|---|---|
| `ListPageBag<T>(collection)` | One page per element. `currentElement()` returns the element of the page. |
| `PageBag(pages)` | A fixed number of pages. `current()` returns the page index, starting at 0. |
| `PrivateListPageBag<T>(collection, ownerId)` | Like `ListPageBag`, but only the owner can scroll. |
| `PrivatePageBag(pages, ownerId)` | Like `PageBag`, but only the owner can scroll. |

The page wraps around: next on the last page shows the first one.

Override `buildEmptyPage()` to show something when there are no pages. Without it, an empty bag throws
`EmptyPageBagException`.

## Build a bag without a class

`PageBuilder` builds a bag from lambdas:

```java
IPageBag bag = PageBuilder.list(entries)
        .syncPage(page -> MessageEditData.fromContent(page.currentElement()))
        .syncEmptyPage(page -> MessageEditData.fromContent("Nothing here."))
        .buttons(page -> List.of(PageButton.of(Button.primary("refresh", "words.refresh"))))
        .forUser(event.getUser())
        .build();
```

`PageBuilder.standard(pages)` does the same for a fixed number of pages. `forUser` makes the bag private.

## Extra buttons

Override `buttons()` to add buttons below the page:

```java
@Override
public List<PageButton> buttons() {
    return List.of(PageButton.of(
            bag -> Button.primary("refresh", "words.refresh"),
            (bag, event) -> event.deferEdit().queue()));
}
```

Your button lambda acknowledges the interaction. The previous and next buttons are handled for you.

## Lifetime

A paginated message lives for 10 minutes after its last use. Clicks after that are ignored.
