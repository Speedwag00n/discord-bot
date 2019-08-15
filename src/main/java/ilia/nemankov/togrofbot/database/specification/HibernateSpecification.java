package ilia.nemankov.togrofbot.database.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface HibernateSpecification<T> {

    Predicate getPredicate(CriteriaBuilder builder, Root<T> root);

}
