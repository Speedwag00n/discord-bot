package ilia.nemankov.togrofbot.util.pagination;

import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.pagination.footer.Footer;
import ilia.nemankov.togrofbot.util.pagination.header.Header;
import ilia.nemankov.togrofbot.util.pagination.row.IndexedRow;
import ilia.nemankov.togrofbot.util.pagination.row.Row;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils {

    public static Page buildPage(int pageNumber, Header header, List<Row> rows, Footer footer) throws PageNotFoundException {
        SettingsProvider settings = SettingsProvider.getInstance();
        return buildPage(pageNumber, settings.getDefaultPageSize(), header, rows, footer);
    }

    public static Page buildPage(int pageNumber, int itemsOnPage, Header header, List<Row> rows, Footer footer) throws PageNotFoundException {
        if (!((pageNumber > 0) && (rows.size() / itemsOnPage + ((rows.size() % itemsOnPage == 0) ? 0 : 1)) >= pageNumber)) {
            throw new PageNotFoundException();
        }
        if (header != null) {
            header.setPageNumber(pageNumber);
            header.setMaxPageNumber((rows.size() / itemsOnPage + ((rows.size() % itemsOnPage == 0) ? 0 : 1)));
        }
        List<Row> pageRows = new ArrayList<>();
        for (int i = (pageNumber - 1) * itemsOnPage; i < ((rows.size() > pageNumber * itemsOnPage) ? pageNumber * itemsOnPage : rows.size()); i++) {
            Row row = rows.get(i);
            if (row instanceof IndexedRow) {
                ((IndexedRow) row).setIndex(i + 1);
            }
            pageRows.add(row);
        }
        return new Page(header, pageRows, footer);
    }

}
