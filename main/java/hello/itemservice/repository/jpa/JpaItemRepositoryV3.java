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
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static hello.itemservice.domain.QItem.item;

@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {

//    복습차원에서
//    @Transactional :
//    JPA의 모든 데이터변경(등록,수정,삭제)은 트랜잭션 안에서 이루어져야 한다.
//    조회는 트랜잭션 없어도 가능하다.
//    변경의 경우 일반적으로 서비스 계층에서 트랜잭션을 시작하기 때문에 문제가 없다.

//   @Repository :
//   @Repository가 붙은 클래스는 예외변환 AOP의 적용 대상이 된다.
//   예를들어서 EntityManager에서 JPA예외가 발생하면 JpaItemRepositoryV3에 JPA예외가 전달된다.
//   그런데 JpaItemRepositoryV3에 @Repository가 붙었기 때문에 JpaItemRepositoryV3에 대한
//   AOP Proxy 객체가 만들어 진다. 만들어진 AOP 프록시 객체에 JPA예외가 전달된다. 그리고 그 AOP Proxy객체는
//   JPA예외를 스프링 예외 추상화로 변환한다. 그리고 변환된 스프링 예외 추상화 예외는 AOP Proxy객체에 의해서
//   서비스 계층에 전달된다.


    private final EntityManager em;
    private final JPAQueryFactory query;
    // 이것이 queryDSL이다. queryDSL은 결과적으로 JPA의 jpql을 만들어주는 빌더 역할을 한다.
    // queryDSL-JPA(여기서는 query 참조변수에 담긴 객체)가 쿼리용 엔티티 객체나 기타 객체들을 이용해서
    // jpql을 만들고 queryDSL-JPA(여기서는 query 참조변수에 담긴 객체)가 내부적으로 fetch(); 와 같은
    // JPA를 통해서 DB에 접근하라는 메소드도 제공한다.

    // JPAQueryFactory : JPA쿼리, 즉,jpql을 만들어주는 공장이다ㅋㅋ 그 안에 JPA인 EntityManager을 넣어주면 된다.
    // jdbctemplate에서 datasource를 넣어주는 것과 비슷하다.


    public JpaItemRepositoryV3(EntityManager em){
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override

    public Item save(Item item){
        // 저장은 이렇게 하면 된다. 이것은 JPA그냥 쓴 것이다.
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam){
        // update는 그냥 JPA를 쓰면 된다.

        Item findItem = findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id){
        // findById는 그냥 JPA를 쓰면 된다.
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }


    // 이제부터 QueryDSL-JPA를 써 보겠다.
//    public List<Item> findAllOld(ItemSearchCond itemSearchCond){
//        String itemName = itemSearchCond.getItemName();
//        Integer maxPrice = itemSearchCond.getMaxPrice();
//
//        QItem item = QItem.item; // 컴파일 시점에 Q타입 객체(쿼리용 엔티티 객체)가 생긴다.
//        BooleanBuilder builder = new BooleanBuilder();
//        // com.querydsl.core에 BooleanBuilder라는 것이 있고 조건에 따라서 넣는 것이다.
//
//        if(StringUtils.hasText(itemName)){
//            builder.and(item.itemName.like("%" + itemName + "%"));
//            // builder 에 and나 or 넣을 수 있다.
//        }
//        if(maxPrice != null){
//            builder.and(item.price.loe(maxPrice));
//            // 쿼리용 엔티티 객체(Q타입 객체)에는 price필드가 있다.
//            // 이 Q타입 객체를 builder에 넣고 builder을 where()에 넣어주면
//            // queryDSL-JPA(query참조변수에 할당된 객체)가 쿼리(jpql)을 만드는 것이다.
//        }
//
//        List<Item> result = query
//                .select(item)
//                .from(item)
//                .where(builder)
//                .fetch();
//
//        return result;
//    }

    @Override
    public List<Item> findAll(ItemSearchCond cond){
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();
        // 원래는 QItem item = new QItem("i"); 이렇게 쓰고("i" 는 별칭이다.) item을 바로 아래 쿼리인
        // select()에 넣어주면 된다. 혹은 QItem이 내부적으로 item을 static으로 가지고 있어서 select(QItem.item) 이렇게
        // 넣어도 된다. 그리고 QItem안의 item이 static이기 떄문에 static import할 수도 있다. 다시말해, QItem.item에서
        // QItem.item을 import static 함으로서 QItem을 생략하고 select(item)이렇게 써도 된다.
        List<Item> result = query
                .select(item)
                .from(item) // from(item) 밑에 .limit(1) 이렇게 쓸 수 있다. 그냥 코드에 다 있다.
                .where(likeItemName(itemName), maxPrice(maxPrice)) // , 로 where()의 인자들을 구별하면 and 조건이 된다.
                .fetch();

        return result;
    }

    private BooleanExpression likeItemName(String itemName){
        if(StringUtils.hasText(itemName)){
            return item.itemName.like("%" + itemName + "%");
        }
        return null;
        // null이면 where조건에서 무시된다.
    }

    private BooleanExpression maxPrice(Integer maxPrice){
        if(maxPrice != null){
            return item.price.loe(maxPrice);
        }
        return null;
    }
}

