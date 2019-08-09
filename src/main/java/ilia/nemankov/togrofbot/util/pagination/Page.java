package ilia.nemankov.togrofbot.util.pagination;

import ilia.nemankov.togrofbot.util.pagination.footer.Footer;
import ilia.nemankov.togrofbot.util.pagination.header.Header;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class Page {

    private Header header;
    private Footer footer;
    private List<Row> rows;

    public Page(Header header, List<Row> rows, Footer footer) {
        this.header = header;
        this.rows = rows;
        this.footer = footer;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(header.toString() + "\n");
        for (Row row : rows) {
            builder.append(row.toString() + "\n");
        }
        //TODO added footer realisation class
        //builder.append(footer.toString());
        return builder.toString();
    }

}
