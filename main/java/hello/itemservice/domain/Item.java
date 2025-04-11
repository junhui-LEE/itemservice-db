package hello.itemservice.domain;

import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Data
@Entity // 1)테이블과 매핑되어서 관리되는 객체
// 참고로 여기에 @Table(name="item")이렇게 적어도 되는데 객체(@Entity)명과 테이블명과 같으면 생략 가능하다.
public class Item {
    @Id // 2) 아!! private Long id 가 pk 이구나!!
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 3)
    private Long id;

    @Column(name="item_name", length=10) // 4)
  
    private String itemName;
    private Integer price;     // 컬럼명과 데이터명이 같으면 비워두면 된다.
    private Integer quantity;  // 컬럼명과 데이터명이 같으면 비워두면 된다.

//    @OneToMany(mappedBy = "id")
//    private List<Movie> movies = new ArrayList<>();

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

// * @Entity : JPA가 사용하는 객체라는 뜻이다. 이 에노테이션이 있어야 JPA가 인식할 수 있다. 이렇게
//   @Entity가 붙은 객체를 JPA에서는 엔티티라고 한다.
// * @Id : 테이블의 PK와 해당 필드를 매핑한다.
// * @GeneratedValue(strategy = GenerationType.IDENTITY) : PK 생성 값을 데이터베이스에서
//   생성하는 IDENTITY 방식을 사용한다. 예) MySQL auto increment
// * @Column : 객체의 필드를 테이블의 컬럼과 매핑한다.
//   * name = "item_name" : 객체는 itemName이지만 테이블의 컬럼은 item_name이므로 이렇게
//     매핑했다.
//   * length = 10 : JPA의 매핑정보(1)2)3)4))로 DDL(create table)도 생성할 수 있는데
//     (INSERT도 만드는데 당연히 DDL도 만들 수 있다.), 그때 컬럼의 길이 값으로 활용된다.
//     여기서는 length = 10 이기 떄문에 varchar 10 으로 표현된다.
//   * @Column을 생략할 경우 필드의 이름을 테이블 컬럼 이름으로 사용한다. 참고로 지금처럼 스프링
//     부트와 통합해서 사용하면 필드 이름을 테이블 컬럼명으로 변경할 때 객체 필드의 카멜 케이스를
//     테이블 컬럼의 언더스코어로 자동으로 변환해준다.
//      * itemName -> item_name, 따라서 위 예제의 @Column(name = "item_name")를 생략해도
//        된다.

// JPA는 public 또는 protected의 기본 생성자가 필수이다. 기본생성자를 꼭 넣어주자.