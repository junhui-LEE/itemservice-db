package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.dynamicSQL.DynamicSQLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
//@Import(MemoryConfig.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
//@Import(JdbcTemplateV3Config.class)
//@Import(MybatisConfig.class)
//@Import(JpaConfig.class)
//@Import(SpringDataJpaConfig.class)
@Import(QuerydslConfig.class)
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


/*
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
*/
//	=> 메모리 db에 대한 dataSource와 db접속정보(test/resources/application.properites 부분) 가 없어도 스프링부트가 알아서
//	   메모리 db를 생성하고 그에 맞는 dataSource도 생성하고, 그에 맞는 db접속정보도 생성해서 db와 test application을 연결시켜 준다.
//	   즉, 개발자는 테스트 수행시 메모리db의 테이블을 정의하는 DDL이 테스트 코드에 있을때 테스트 코드에 @Transactional만 붙여주면 반복덜인 테스트를 수행 할 수 있다.
}
