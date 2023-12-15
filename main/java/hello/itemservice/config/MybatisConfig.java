package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.mybatis.ItemMapper;
import hello.itemservice.repository.mybatis.MybatisItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MybatisConfig {

    private final ItemMapper itemMapper;

    @Bean
    public ItemService itemService(){
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository(){
        return new MybatisItemRepository(itemMapper);
        /*
        *   dataSource를 안쓰고 itemMapper을 써줬다? dataSource는 어떻하나요? Mybatis 모듈이 dataSource나 트랜잭션 매니저와 같은 것을
        *   전부 읽어서 itemMapper와 전부 연결시켜 준다. 그래서 그냥 itemMapper만 써주면 된다.
        *
        * */
    }
}
