package bitone.akeneo.product_generator.infrastructure.database;

import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bitone.akeneo.product_generator.domain.model.ChannelRepository;
import bitone.akeneo.product_generator.domain.model.LocaleRepository;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;

public class DbChannelRepository implements ChannelRepository {

    private LocaleRepository localeRepository;
    private HashMap<String, Channel> channels;

    public DbChannelRepository(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository;
    }

    public Channel get(String code) {
        return channels.get(code);
    }

    public int count() {
        return channels.size();
    }

    public Channel[] all() {
        return (Channel[]) channels.values().toArray(new Channel[channels.size()]);
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Statement stmt;

        channels = new HashMap<String, Channel>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT c.id, c.code, GROUP_CONCAT(l.code) as locales FROM pim_catalog_channel c "
            + "LEFT JOIN pim_catalog_channel_locale cl ON cl.channel_id = c.id "
            + "LEFT JOIN pim_catalog_locale l ON l.id = cl.locale_id "
            + "GROUP BY c.id, c.code"
        );

        while(rs.next()){
            int id  = rs.getInt("c.id");
            String code = rs.getString("c.code");
            String[] localeCodes = rs.getString("locales").split(",");

            ArrayList<Locale> locales = new ArrayList<Locale>();

            for(String localeCode : localeCodes) {
                locales.add(localeRepository.get(localeCode));
            }

            Channel channel = new Channel(id, code, locales.toArray(new Locale[locales.size()]), null);
            channels.put(code, channel);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
