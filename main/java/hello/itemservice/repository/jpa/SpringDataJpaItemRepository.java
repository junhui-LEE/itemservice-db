package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {
    // 스프링 데이터 JPA를 사용하기 위해서는 우선 스프링 데이터 JPA를 의미하는 인터페이스인
    // JpaRepository<Item, Long>인터페이스를 상속받으면 되고 <Item, Long>와 같이 제네릭에
    // <관리할 엔티티, 엔티티의 pk타입> 이렇게 넣어주면 JPA가 제네릭 타입으로 들어온 엔티티(Item)
    // 를 관리한다.

    List<Item> findByItemNameLike(String itemName);
    // select i
    // from Item i
    // where i.itemName like ?

    // return em.createQuery("select i from Item i where i.itemName like  :itemName")
    //         .setParameter("itemName", itemName)
	//         .getResultList();
    List<Item> findByPriceLessThanEqual(Integer price);
    // select i
    // from Item i
    // where i.price <= ?

    // return em.createQuery("select i from Item i where i.price <= :price")
    //          .setParameter("price", price)
    //          .getResultList();

    // 쿼리 메서드 (아래 메서드와 같은 기능 수행)
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);
    // select i
    // from Item i
    // where i.itemName like ? and i.price <= ?

    // return em.createQuery("select i from Item i where i.itemName like :itemName and price <= :price")
    //          .setParameter("itemName", itemName)
    //          .getResultList();

    // 쿼리 직접 실행
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName")String itemName, @Param("price")Integer price);
}
