package ilia.nemankov.togrofbot.util.pagination;

import ilia.nemankov.togrofbot.util.pagination.footer.Footer;
import ilia.nemankov.togrofbot.util.pagination.header.Header;
import ilia.nemankov.togrofbot.util.pagination.row.IndexedRow;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PaginationUtils {

    private static final Logger logger = LoggerFactory.getLogger(PaginationUtils.class);

    public static Page buildPage(Header header, List<? extends Row> rows, Footer footer) {
        return new Page(header, rows, footer);
    }

    public static int maxPage(int itemsOnPage, long itemsCount) {
        if (itemsOnPage <= 0 || itemsCount < 0) {
            logger.error("Received invalid args: itemsOnPage={}, itemsCount={}", itemsOnPage, itemsCount);
            throw new IllegalArgumentException();
        } else {
            return (int)(itemsCount / itemsOnPage + ((itemsCount % itemsOnPage == 0) ? 0 : 1));
        }
    }

    public static boolean isPageExist(int pageNumber, int itemsOnPage, long itemsCount) {
        if (pageNumber <= 0 || itemsOnPage <= 0 || itemsCount <= 0) {
            logger.error("Received invalid args: pageNumber={}, itemsOnPage={}, itemsCount={}", pageNumber, itemsOnPage, itemsCount);
            return false;
        } else {
            return (itemsCount / itemsOnPage + ((itemsCount % itemsOnPage == 0) ? 0 : 1)) >= pageNumber;
        }
    }

    public static <T> List<T> getPageContent(int pageNumber, int itemsOnPage, List<T> items) {
        if (pageNumber <= 0 || itemsOnPage <= 0) {
            logger.error("Received invalid args: pageNumber={}, itemsOnPage={}", pageNumber, itemsOnPage);
            throw new IllegalArgumentException();
        }
        return items.subList((pageNumber - 1) * itemsOnPage, (pageNumber * itemsOnPage > items.size()) ? items.size() : pageNumber * itemsOnPage);
    }

    public static List<? extends IndexedRow> setIndexes(int firstIndex, List<? extends IndexedRow> indexedRows) {
        if (firstIndex < 0) {
            logger.error("Received invalid args: firstIndex={}", firstIndex);
            throw new IllegalArgumentException();
        }
        int i = firstIndex;
        for (IndexedRow row : indexedRows) {
            row.setIndex(i++);
        }
        return indexedRows;
    }

}
