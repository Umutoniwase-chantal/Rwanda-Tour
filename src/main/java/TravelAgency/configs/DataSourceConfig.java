//package TravelAgency.configs;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DataSourceConfig {
//
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        // Set your DataSource properties (URL, username, password, etc.)
//        dataSource.setUrl("jdbc:mysql://localhost:3306/tour");
//        dataSource.setUsername("root");
//        dataSource.setPassword("Admin@123");
//        return dataSource;
//    }
//}

package TravelAgency.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        // PostgreSQL configuration
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://dpg-cm0ses8cmk4c739dm7qg-a.oregon-postgres.render.com/tour_bwoo");
        dataSource.setUsername("tour");
        dataSource.setPassword("ZvTY5lgySal7NOjEQkUyXV1wjQm8eLBm");

        return dataSource;
    }
}


