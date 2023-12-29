package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.dynamicSQL.DynamicSQLMapper;
import hello.itemservice.repository.dynamicSQL.DynamicSQLRepository;
import hello.itemservice.repository.mybatis.ItemMapper;
import hello.itemservice.repository.mybatis.MybatisItemRepository;
import hello.itemservice.service.DynamicSQLService;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DynamicSQLConfig {
    private final DynamicSQLMapper dynamicSQLMapper;
    @Bean
    public DynamicSQLService dynamicSQLService(){
        return new DynamicSQLService(dynamicSQLRepository());
    }
    @Bean
    public DynamicSQLRepository dynamicSQLRepository(){
        return new DynamicSQLRepository(dynamicSQLMapper);
        /*
         *   dataSource를 안쓰고 dynamicSQLMapper 써줬다?
         *   dataSource는 어떻하나요? Mybatis 모듈이 dataSource나
         *   트랜잭션 매니저와 같은 것을 전부 읽어서 dynamicSQLMapper와
         *   전부 연결시켜 준다. 그래서 그냥 dynamicSQLMapper만 써주면 된다.
         *
         * */
    }
}
