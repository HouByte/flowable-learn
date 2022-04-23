package cn.flowboot.flowable.config;

/**
 * <h1>流程引擎配置文件</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/07
 */

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@RequiredArgsConstructor
@Configuration
@Data
@Slf4j
public class ProcessEngineConfig {


    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(){
        SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
        springProcessEngineConfiguration.setDataSource(dataSource);
        springProcessEngineConfiguration.setDatabaseSchemaUpdate("true");
        springProcessEngineConfiguration.setTransactionManager(dataSourceTransactionManager(dataSource));

        return springProcessEngineConfiguration;
    }




}
