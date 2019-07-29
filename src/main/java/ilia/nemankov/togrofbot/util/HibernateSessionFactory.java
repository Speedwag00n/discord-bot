package ilia.nemankov.togrofbot.util;

import ilia.nemankov.togrofbot.database.entity.MusicLink;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistToMusicLink;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateSessionFactory {

    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactory.class);

    private static SessionFactory instance;

    private HibernateSessionFactory() {

    }

    public static SessionFactory getSessionFactory() {
        if (instance == null) {
            try {
                Configuration configuration = new Configuration().configure();

                configuration.addAnnotatedClass(PlaylistEntity.class);
                configuration.addAnnotatedClass(MusicLink.class);
                configuration.addAnnotatedClass(PlaylistToMusicLink.class);

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                instance = configuration.buildSessionFactory(builder.build());
                logger.debug("Created {} class instance", SessionFactory.class.getSimpleName());
            } catch (Exception e) {
                logger.debug("Failed to create {}", SessionFactory.class.getSimpleName(), e);
            }
        }
        return instance;
    }

}
