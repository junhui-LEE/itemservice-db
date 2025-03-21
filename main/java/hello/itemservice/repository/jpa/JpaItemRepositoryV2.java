package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaItemRepositoryV2 implements ItemRepository {

    private final SpringDataJpaItemRepository repository;

    @Override
    public Item save(Item item) {
        return repository.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = repository.findById(itemId).orElseThrow();
        // orElseThrow() : Optional객체 안의 값이 null이면 예외를 발생시키고
        // 있으면 그 값을 발생시킨다.
        // Optional객체 안에 값(여기서는 Item객체)이 없는 경우 Optional.orElseThrow()를 통해서
        // 명시적으로 예외를 던질 수 있다. findById(1).orElseThrow(() -> new NoSuchElementException("Member Not Found"));
        // 이렇게 쓸수 있고 자바10 부터는 orElseThrow()의 인수 없이도 사용할 수 있다.
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        if(StringUtils.hasText(itemName) && maxPrice != null){
            return repository.findItems("%"+itemName+"%", maxPrice);
        }else if(StringUtils.hasText(itemName)){
            return repository.findByItemNameLike("%"+itemName+"%");
        }else if(maxPrice != null){
            return repository.findByPriceLessThanEqual(maxPrice);
        }else{
            return repository.findAll();
        }
    }
}