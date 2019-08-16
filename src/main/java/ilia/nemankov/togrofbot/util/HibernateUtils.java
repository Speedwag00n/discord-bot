package ilia.nemankov.togrofbot.util;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.QuerySettings;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;

public class HibernateUtils {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtils.class);

    private static SessionFactory instance;

    public static SessionFactory getSessionFactory() {
        if (instance == null) {
            try {
                Configuration configuration = new Configuration().configure();

                configuration.addAnnotatedClass(PlaylistEntity.class);
                configuration.addAnnotatedClass(MusicLinkEntity.class);
                configuration.addAnnotatedClass(AliasEntity.class);

                final String JDBC_DATABASE_URL = System.getenv("JDBC_DATABASE_URL");
                if (JDBC_DATABASE_URL == null) {
                    logger.error("Can not find system variable for JDBC database URL");
                    System.exit(0);
                }
                final String JDBC_DATABASE_USERNAME = System.getenv("JDBC_DATABASE_USERNAME");
                if (JDBC_DATABASE_URL == null) {
                    logger.error("Can not find system variable for JDBC database username");
                    System.exit(0);
                }
                final String JDBC_DATABASE_PASSWORD = System.getenv("JDBC_DATABASE_PASSWORD");
                if (JDBC_DATABASE_URL == null) {
                    logger.error("Can not find system variable for JDBC database password");
                    System.exit(0);
                }

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .applySetting("hibernate.connection.url", JDBC_DATABASE_URL)
                        .applySetting("hibernate.connection.username", JDBC_DATABASE_USERNAME)
                        .applySetting("hibernate.connection.password", JDBC_DATABASE_PASSWORD);
                instance = configuration.buildSessionFactory(builder.build());
                logger.debug("Created {} class instance", SessionFactory.class.getSimpleName());
            } catch (Exception e) {
                logger.debug("Failed to create {}", SessionFactory.class.getSimpleName(), e);
            }
        }
        return instance;
    }

    public static <T> TypedQuery<T> applySettings(TypedQuery<T> query, QuerySettings settings) {
        if (settings.getFirstResult() != null) {
            query.setFirstResult(settings.getFirstResult());
        }
        if (settings.getMaxResult() != null) {
            query.setMaxResults(settings.getMaxResult());
        }

        return query;
    }

}
