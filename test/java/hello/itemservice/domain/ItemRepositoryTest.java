package hello.itemservice.domain;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import hello.itemservice.repository.memory.MemoryItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


//@Rollback(value = false)
//@Commit
@Slf4j
@Transactional
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

//    스프링은 테스트 데이터 초기화를 위해 트랜잭션을 적용하고 롤백하는 방식을 @Transactional
//    애노테이션 하나로 깔끔하게 해결해 준다.
//    (@Transactional은 서비스 계층에서 트랜잭션 적용할때 쓰는 것 아니야? 맞다. 그런데 @Transactional이 테스트에서 사용되면 좀 특별하게 사용된다.)

/*
    @Autowired
    PlatformTransactionManager transactionManager;
    // dataSource와 transactionManager은 SpringBoot가 자동으로 빈으로 등록해 준다.
    TransactionStatus status;

    @BeforeEach
    void beforeEach(){
        // 트랜잭션 시작
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }
*/
    @AfterEach
    void afterEach() {
        //MemoryItemRepository 의 경우 제한적으로 사용
        if (itemRepository instanceof MemoryItemRepository) {
            ((MemoryItemRepository) itemRepository).clearStore();
        }
        // 트랜잭션 롤백
        //transactionManager.rollback(status);
        // ...
    }

// // 2024 02 02 queryDSL의 findAll()을 테스트 하기 위해서 임시로 만들어 둔 것이다.
//    @Test
//    void selectAll(){
//        Item itemA = new Item("itemA", 10000, 10);
//        Item itemB = new Item("itemB", 20000, 20);
//        Item itemC = new Item("itemC", 30000, 30);
//        ItemSearchCond itemSearchCond = new ItemSearchCond("itemABC", 60000);
//
//        itemRepository.save(itemA);
//        itemRepository.save(itemB);
//        itemRepository.save(itemC);
//
//        List<Item> items = itemRepository.findAll(itemSearchCond);
//        log.info("itemA의 itemName={}, price={}, quantity={}", items.get(0).getItemName(), items.get(0).getPrice(), items.get(0).getQuantity());
//        log.info("itemB의 itemName={}, price={}, quantity={}", items.get(1).getItemName(), items.get(1).getPrice(), items.get(1).getQuantity());
//        log.info("itemC의 itemName={}, price={}, quantity={}", items.get(2).getItemName(), items.get(2).getPrice(), items.get(2).getQuantity());
//    }

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
//    @Commit
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();

        //when
        ItemUpdateDto updateParam = new ItemUpdateDto("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);

        //then
        Item findItem = itemRepository.findById(itemId).get();
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    @Test
    void findItems() {
        //given
        Item item1 = new Item("itemA-1", 10000, 10);
        Item item2 = new Item("itemA-2", 20000, 20);
        Item item3 = new Item("itemB-1", 30000, 30);


        log.info("repository={}", itemRepository.getClass());
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        //둘 다 없음 검증
        test(null, null, item1, item2, item3);
        test("", null, item1, item2, item3);

        //itemName 검증
        test("itemA", null, item1, item2);
        test("temA", null, item1, item2);
        test("itemB", null, item3);

        //maxPrice 검증
        test(null, 10000, item1);

        //둘 다 있음 검증
        test("itemA", 10000, item1);
    }

    void test(String itemName, Integer maxPrice, Item... items) {
        List<Item> result = itemRepository.findAll(new ItemSearchCond(itemName, maxPrice));
        assertThat(result).containsExactly(items);
    }
}
