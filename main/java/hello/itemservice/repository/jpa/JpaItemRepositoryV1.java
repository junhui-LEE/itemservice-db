package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional // JPA의 모든 데이터 변경(등록, 수정, 삭제)는 트랜잭션 안에서 이루어져야 한다. 조회는 트랜잭션 없이도 가능하다.
               // 변경(등록, 수정, 삭제)의 경우 일반적으로 서비스 계층에서 트랜잭션을 시작하기 떄문에 문제가 없다. 하지만 이번
               // 예제에서는 복잡한 비즈니스 로직이 없어서 서비스 계층에서 트랜잭션을 걸지 않았다. "JPA에서는 데이터 변경시
               // 트랜잭션이 필수다." 따라서 리포지토리에 트랜잭션을 걸어주었다. 다시한번 강조하지만 일반적으로는 비즈니스 로직을
               // 시작하는 서비스 계층에 트랜잭션을 걸어주는 것이 맞다.
public class JpaItemRepositoryV1 implements ItemRepository {

    private final EntityManager em;
    // 생성자를 보면 스프링과 JPA를 통합했기 때문에 스프링을 통해 엔티티 매니저(EntityManager)
    // 라는 것을 주입받은 것을 확인할 수 있다. JPA의 모든 동작은 엔티티 매니저를 통해서 이루어진다. 엔티티
    // 매니저는 내부에 데이터소스를 가지고 있고, 데이터베이스에 접근할 수 있다. JDBC API도 결국 EntityManager
    // 내부에서 다 쓴다.
    public JpaItemRepositoryV1(EntityManager em){
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item); // 저장은 이렇게 한다. // persist : 1)영구히 보존하다. 2)집요하게[고집스럽게/끈질기게] 계속하다. 3)(없어지지 않고)계속[지속]되다.
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        /*
        * findItem에서 setter을 통해서 수정을 했으면 이제 DB에 저장을 해줘야 할 것 같은데, 아무것도 안해줘도 된다.
        * 안해줘도 update쿼리가 실제 DB에 나간다. JPA가 트랜잭션 커밋되는 시점에 update쿼리 날린다. 예를들어 어??
        * findItem이 변경되었네?? 그럼 JPA가 변경된 findItem을 기반으로 update쿼리를 만들어서 DB에 날린다. 마치
        * 우리는 JAVA컬랜션에서 조회한 것을 변경하는 것 처럼 코드를 짰다.
        *
        * @Transactional은 메소드가 끝날때 DB에 커밋 날라간다. 그러면서 JPA가 update쿼리를 만들었고 먼저 update쿼리를
        * 날리고 그 다음에 커밋이 실행된다.
        * */
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }


    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        /*
        * findAll은 JPQL이란것을 쓴다. 하나를 조회할때는 식별자를 기준으로,pk를 기반으로, 하나를 조회할 때는 em.find(Item.class, id);를
        * 쓰면 되는데 여러가지 조건으로 복잡하게 쿼리를 짜야하면 이경우 JPA는 SQL이 아니라 거의 SQL과 95% 비슷한 JPQL이란것을 제공한다.
        * */

        String jpql = "select i from Item i";
        // i : Item엔티티 자체를 의미한다.(*도 아니고 i.item_name도 아니다.)
        // Item : 테이블명이 아니라 Item 엔티티 객체 명이다.

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if(StringUtils.hasText(itemName) || maxPrice != null){
            jpql += " where";
        }

        boolean andFlag = false;
        if(StringUtils.hasText(itemName)){
            jpql += " i.itemName like concat('%', :itemName, '%')"; // jpql도 이름기반 파라미터 사용할 수 있다.
            andFlag = true;
        }

        if(maxPrice != null){
            if(andFlag){
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);

        /*
        * EntityManager에서 제공하는 persist(), find()와 같이 이미 만들어진 쿼리가 아니라
        * 내가 직접 SQL쿼리를 만들려면 TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        * 를 해주면 된다. em.createQuery(jpql, Item.class); 가 중간에 매핑해주고 Item.class로 타입을
        * 지정했기 때문에 List<Item>에 바로 넘어갈 수 있다. jpql문법은 SQL과 거의 비슷한데 테이블을 대상으로
        * 하는 것이 아니라 Item엔티티를 대상으로 한다 정도로 이해하면 된다.
        * */
      
        if(StringUtils.hasText(itemName)){
            query.setParameter("itemName", itemName);
        }
        if(maxPrice != null){
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}