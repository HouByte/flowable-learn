package cn.bugio.flowable.ui;

import cn.bugio.flowable.ui.config.AppDispatcherServletConfiguration;
import cn.bugio.flowable.ui.config.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/10/08
 */
@Import({
        ApplicationConfiguration.class,
        AppDispatcherServletConfiguration.class
})
@EnableTransactionManagement
@SpringBootApplication
public class FlowableUIApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowableUIApplication.class,args);
    }
}
