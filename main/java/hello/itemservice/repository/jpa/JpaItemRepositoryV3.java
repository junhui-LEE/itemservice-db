package hello.itemservice.repository.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static hello.itemservice.domain.QItem.item;

@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;
    /*
    *   JPAQueryFactory는 queryDSL이다. 결과적으로 JPA의 jpql을 만들어 주는 빌더 역할을 한다.
    *   JPAQueryFactory는 JPA쿼리, 다시말해 jpql을 만들어 주는 공장이다. ㅋㅋ 그 안에 JPA인
    *   EntityManager을 넣어 주면 된다. jdbctemplate에서 datasource를 넣어주는 것과 비슷하다.
    * */

    public JpaItemRepositoryV3(EntityManager em){
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;  // jpa에서는 insert문에서 persist를 쓴다. spring data jpa에서는 save이다.
    }
    // 저장은 이렇게 하면된다. 이것은 JPA 그냥 쓴 것이다.
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
        // jpa에서는 select * from item where id=:id을 em.find(Item.class, id) 이렇게 쓴다.
    }
    // update와 한개를 조회하는 findById는 그냥 JPA쓰면 된다.

    // 이제부터 queryDSL을 써 보겠다.

    public List<Item> selectAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        QItem item = new QItem("i");
        /*
        *   원래는 QItem item = new QItem("i"); 이렇게 쓰고 바래 아래 쿼리인 select()에 item을 넣어 주면 된다.
        *   혹은 QItem이 내부적으로 item을 static으로 가지고 있어서 select(QItem.item)이렇게 넣어도 된다.
        *   그리고 QItem안의 item이 static이기 때문에 static import할 수도 있다. 다시말해 QItem.item에서
        *   QItem을 import static함으로서 QItem을 생략하고 select(item)이렇게 써도 된다.
        * */
        List<Item> result = query
                .select(item)
                .from(item)
                .fetch();
        return result;
    }

    // Old ㅋㅋ 옛날것 ㅋㅋ 네이밍 좋다 ㅋㅋ
    public List<Item> findAllOld(ItemSearchCond itemSearchCond){
        String itemName = itemSearchCond.getItemName();
        Integer maxPrice = itemSearchCond.getMaxPrice();

        QItem item = QItem.item;
        BooleanBuilder builder = new BooleanBuilder();
        // BooleanBuilder라는 것이 있고 조건에 따라서 넣는 것이다.

        if(StringUtils.hasText(itemName)) {
            builder.and(item.itemName.like("%"+itemName+"%"));
        // builder에 and, or 이렇게 넣을 수 있다.
        }
        if(maxPrice != null){   // loe : 작거나 같다(java 코드에서는 <= 는 안된다.ㅋㅋ)
            builder.and(item.price.loe(maxPrice));
        }  // queryDSL이 제공하는 item에는 price 필드가 있다.
        // 이렇게 builder을 만들고 where()에 넣어 주면 된다.
        List<Item> result = query
                .select(item)
                .from(item)
                .where(builder)
                .fetch();
        return result;
    }

    // 위의 코드를 리펙토링 한 것이 아래의 코드이다.
    @Override
    public List<Item> findAll(ItemSearchCond itemSearchCond){
        String itemName = itemSearchCond.getItemName();
        Integer maxPrice = itemSearchCond.getMaxPrice();

        List<Item> result = query
                .select(item)
                .from(item)
                .where(itemName(itemName), maxPrice(maxPrice))
//              ,를 쓰면 and조건이 된다. 만일 인자(여기서는 함수)가 null이면 where조건에서 무시된다.
//                .limit(1)  --> 여기에 .limit(1)이렇게 쓸 수 있다. 그냥 queryDSL코드에 다 있다.
                .fetch();

        return result;
    }

    public BooleanExpression itemName(String itemName){
        if(StringUtils.hasText(itemName)){
            return item.itemName.like("%"+itemName+"%");
        }
        return null;
    }

    public BooleanExpression maxPrice(Integer maxPrice){
        if(maxPrice != null){
            return item.price.loe(maxPrice);
        }
        return null;
    }

}


















