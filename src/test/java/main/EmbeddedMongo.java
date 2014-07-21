package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;

public class EmbeddedMongo {
    private static final Logger LOG = LoggerFactory.getLogger(TestBase.class);
    private static final MongodStarter starter = MongodStarter.getDefaultInstance();
    private static final int port = 28018;
    private static MongodExecutable mongodExecutable;
    private static EmbeddedMongo instance;
    
    private EmbeddedMongo() {
        try {
            mongodExecutable = starter.prepare(new MongodConfigBuilder()
            .version(Version.Main.V2_6)
            .net(new Net(port, false))
            .build());

            mongodExecutable.start();
        } catch (Exception e) {
            LOG.error("Failed to start in memory mongodb for testing", e);
        }
    }

    public static EmbeddedMongo getInstance() {
        if (instance == null) {
            instance = new EmbeddedMongo();
        }
        return instance;
    }
}