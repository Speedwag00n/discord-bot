package ilia.nemankov.togrofbot.database.specification.impl.composite;

import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import ilia.nemankov.togrofbot.database.specification.Specification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class NotSpecification<T> extends AbstractSpecification<T> {

    private Specification<T> argument;

    @Override
    public boolean isSatisfiedBy(T entity) {
        return !argument.isSatisfiedBy(entity);
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<T> root) {
        return builder.not(argument.getPredicate(builder, root));
    }

    @Override
    public Class<T> getType() {
        return argument.getType();
    }

}