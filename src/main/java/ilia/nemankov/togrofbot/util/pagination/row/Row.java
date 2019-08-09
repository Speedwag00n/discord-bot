package ilia.nemankov.togrofbot.util.pagination.row;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Row {

    private String content;

    @Override
    public String toString() {
        return content;
    }

}
