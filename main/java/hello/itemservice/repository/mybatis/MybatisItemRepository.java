package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisItemRepository implements ItemRepository {
    private final ItemMapper itemMapper;
    /*
    *   ItemMapper은 인터페이스지만 DI가 된다. ItemMapper인터페이스 위에 @Mapper을 써 주면 Mybatis spring 모듈에서 자동으로 인식해서
    *   ItemMapper인터페이스의 구현체를 만들어서 스프링 빈에 등록해 준다. 그래서 ItemMapper의 구현체를 DI 받을 수 있다.
    * */

    @Override
    public Item save(Item item){
        itemMapper.save(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam){
        itemMapper.update(itemId, updateParam);
    }

    @Override
    public Optional<Item> findById(Long id){
        return itemMapper.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond){
        return itemMapper.findAll(cond);
    }

}
