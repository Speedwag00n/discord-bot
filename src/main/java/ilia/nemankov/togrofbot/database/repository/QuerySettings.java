package ilia.nemankov.togrofbot.database.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuerySettings {

    private Integer firstResult;
    private Integer maxResult;

}
