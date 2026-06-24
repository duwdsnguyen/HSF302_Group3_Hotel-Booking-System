package hsf.g3.hotel_booking_system;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

public class DatabaseInitializerApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(HotelBookingSystemApplication.class)
                        .web(WebApplicationType.NONE)
                        .run(args);

        DataSource dataSource = context.getBean(DataSource.class);

        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/schema.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/data.sql"));
        }

        context.close();

        System.out.println("Database initialized successfully.");
    }
}