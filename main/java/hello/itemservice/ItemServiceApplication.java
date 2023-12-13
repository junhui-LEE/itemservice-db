package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


//@Import(MemoryConfig.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
@Slf4j
@Import(JdbcTemplateV3Config.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

	@Bean // 내가 직접 등록했고, 그러면 테스트케이스에서 dataSource를 쓸때에는 항상 내가 등록한 dataSource가 기본으로 사용된다.
	@Profile("test")  // 테스트 수행시에는 dataSource를 직접 등록해서 쓸것이다.
	public DataSource dataSource(){
		log.info("메모리 데이터베이스 초기화");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver"); // H2 데이터베이스 드라이버를 지정해 준 것이다. 이 드라이버를 쓰겠다는 의미이다.
		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("test");
		dataSource.setPassword("1234");
		return dataSource;
	} // 이렇게 하면 H2 데이터베이스가 setUrl부분(mem 부분)을 보고 JVM안에 데이터베이스를 만들고 거기에 데이터를 쌓는다.

}
