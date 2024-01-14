package hello.itemservice.repository.jpa;


import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
//@Repository
//@Transactional
public class JpaItemRepositoryV1 implements ItemRepository {
/*
*   @Transactional : JPA의 모든 데이터 변경(등록, 수정, 삭제)은 트랜잭션 안에서 이루어져야 한다. 조회는
*   트랜잭션이 없어도 가능하다. 변경의 경우 일반적으로 서비스 계층에서 트랜잭션을 시작하기 때문에 문제가 없다.
*   하지만 이번 예제에서는 복잡한 비즈니스 로직이 없어서 서비스 계층에서 트랜잭션을 걸지 않았다. JPA에서는
*   데이터 변경시 트랜잭션이 필수다. 다시한번 강조하지만 일반적으로는 비즈니스 로직을 시작하는 서비스 계층에
*   트랜잭션을 걸어주는 것이 맞다.
* */
    private final EntityManager em;

    public JpaItemRepositoryV1(EntityManager em){
        this.em = em;
    }
    /*
    *   스프링과 JPA를 통합했기 때문에 스프링을 통해 엔티티 매니저(Entity Manager)라는 것을 주입 받는 것을 확인할 수 있다.
    *   JPA의 모든 동작은 엔티티 매니저를 통해서 이루어 진다. 엔티티 매니저는 내부에 데이터소스를 가지고 있고 데이터베이스에
    *   접근할 수 있다. JDBC API도 결국 내부에서 다 쓴다.
    * */

    @Override
    public Item save(Item item) {
        em.persist(item);  // 저장은 이렇게 한다.
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        /*
        *   "update 저장을 해줘야 할 것 같은데 아무것도 안해줘도 된다." 안해줘도 update쿼리가 실제 DB에 나간다.
        *   JPA가 트랜잭션 커밋되는 시점에 update 쿼리를 날린다. 예를들어 어?? findItem이 변경되었네??
        *   그럼 JPA가 변경된 findItem을 기반으로 update 쿼리를 만들어서 DB에 날린다. "마치 우리는 JAVA컬랙션에서
        *   조회한것을 변경하는 것 처럼 코드를 짰다."
        * */
    }
    /*
    *   @Transactional은 메소드가 끝날때 DB에 커밋 날라간다. 그러면서 JPA가 update쿼리를 만들었고 update쿼리를 날리고
    *   그 다음 커밋이 실행된다.
    * */

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    /*
    이 부분(아래의 코드)에 대해서 다시한번 정리할 겸 써본다. 아래의 코드가 의미하는 바를 써본다. ㅎㅎ
    검색 조건에 따라서 ItemSearchCond가(그 안의 itemName, maxPrice)각각 다르게 들어온다.
    처음 localhost:8080/items 로 들어오면 ItemSearchCond의 itemName과 maxPrice는 null이다.
    그럼 item객체 모두가 ITEM테이블로부터 조회가 되어야 한다.
    즉 1) itemName와 maxPrice가 모두 null이면 ITEM테이블의 모든 데이터(row)를 가져와야 한다.
    사용자가 상품이름과 최대가격을 입력한 다음에 검색버튼을 누르면 똑같은 url로 메소드는 get으로
    itemName과 maxPrice를 key로 하고 그 key에 값을 채운후 itemSearch라는 이름으로 localhost:8080/items컨트롤러가
    받는 곳으로 간다. 이때 itemName은 null이고 maxPrice는 값이 있는 경우, itemName은 값이 있고 maxPrice는 null인 경우
    itemName과 maxPrice둘다 값이 있는 경우가 있다. 따라서 3경우에 따른 데이터(row)들이 나오도록 구현을 해야 한다.
    즉, 2) itemName은 null이고 maxPrice는 값이 있는 경우 3) itemName은 값이 있고 maxPrice는 null인경우
    4) itemName과 maxPrice둘다 값이 있는경우, 1)2)3)4) 이 4개의 경우를 만족시켜서 각각 다른 데이터(row)들이 나오도록
    쿼리를 구성하고 구성된 쿼리가 실행되도록 코드를 구현해야 한다.
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        /*
        *   findAll은 JPQL이란것을 쓴다. 하나를 조회할때는, 식별자를 기반으로, pk를 기반으로, 하나를 조회할 때는
        *   em.find(Item.class, id);를 쓰면 되는데 여러가지 조건으로 복잡하게 쿼리를 짜야 하면 이 경우, JPA는
        *   SQL이 아니라 거의 SQL과 95% 비슷한 JPQL이란것을 제공한다.( JPQL : 객체 쿼리 언어 )
        * */
//        String jpql = "select i from Item i";
//        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
//        return query.getResultList();
        /*
        *   동적쿼리를 빼고 단순하게 실행시키려면 이렇게 하면 된다. 그럼 끝이다. 중간에 매핑해주고 심지어 jpql에서
        *   i라고 조회한 것을 Item.class로 타입을 지정했기 때문에 List<Item>에 바로 넘어갈 수 있다. jpql문법은
        *   SQL과 거의 비슷한데 테이블을 대상으로 하는것이 아니라 Item엔티티를 대상으로 한다. 정도로 이해하면 된다.
        * */

        /*
        * *****************************************************************************************
        *   JDBC API에서는 동적인 쿼리, 즉, Repository layer의 한개의 메소드 입장에서 들어오는 파라미터가 어떤 것이냐
        *   에 따라서 다른 쿼리를 DB에 보내주기 위해서 바로 SQL문을 썼다. 하지만 JPA(EntityManager)는 객체의 패러다임과
        *   RDB패러다임의 불일치를 해결해 주는 역할을 하고 있고 내부적으로 JDBC API를 사용해서 그러한 역할(패러다임의 불일치
        *   해결)을 수행한다. 그러한 JPA(EntityManager)에서 그 JPA를 사용하는 Repository layer의 한개의 메소드
        *   입장에서 들어오는 파라미터에 따라서 다른 SQL쿼리가 실행되도록 하기 위해서는 안타깝게도 바로 SQL을 사용할 수 없다.
        *   객체쿼리 언어인 개발자는 메소드에서 JPQL을 사용하고 JPA는 그 JPQL을 가져다가 SQL으로 바꿔서 DB로 부터 정보를
        *   CRUD한다. 다 해주면 좋겠지만 너무 많은것을 바라는 것일까.. 아무튼 JPA에서는 상황에 따른 쿼리(SQL)을 날리고
        *   싶을때에는 SQL이 아니라 JPQL이란 것을 사용한다. 그리고 JPA에서 제공하는 createQuery(); 메소드를 통해서
        *   상황에 따라서 다른 쿼리를 createQuery();메소드가 만들어 준다.
        * */
        // select i에서의 i는 Item엔티티 자체를 의미한다.(*도 아니고 i.item_name도 아니다 ㅋㅋ)
        String jpql = "selectrrrrr i from Item i";
        // from Item에서 Item은 테이블명이 아니라 Item엔티티 객체명이다. // Item i에서 i는 alias이다.
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();
        boolean andFlag = false;

        if(StringUtils.hasText(itemName) || maxPrice != null){
            jpql = jpql+" where";
        }

        if(StringUtils.hasText(itemName)){
            jpql = jpql+" i.itemName like concat('%', :itemName, '%')";
            // jpql도 이름기반의 파라미터를 사용할 수 있다.
            andFlag = true;
        }

        if(maxPrice != null){
            if(andFlag){
                jpql = jpql+" and";
            }
            jpql = jpql+" i.price <= :maxPrice";
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if(StringUtils.hasText(itemName)){
            query.setParameter("itemName", itemName);
        }
        if(maxPrice != null){
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}
