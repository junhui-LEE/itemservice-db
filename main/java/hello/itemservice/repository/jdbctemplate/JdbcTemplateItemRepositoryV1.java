package hello.itemservice.repository.jdbctemplate;


/*
* JdbcTemplate
*/

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {
    private final JdbcTemplate template;

    public JdbcTemplateItemRepositoryV1(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }
    @Override
    public Item save(Item item){
//        JdbcTemplate를 적용해서 DB에 insert를 해 보자.
        String sql = "insert into item(item_name, price, quantity) values(?, ?, ?)";
//        우선 insert 문을 썼다.
        KeyHolder keyHolder = new GeneratedKeyHolder();
//        우리는 java에서 pk를 증가시킨다음에 db에 데이터(튜플)을 보내는 것이 아니라 DB에 pk를 제외한 튜플을 넣으면 DB에서 pk가
//        자동 증가하고 그 자동증가한 값을 포함한 전체 튜플에 해당하는 값을 item객체로 만든다음에 item객체를 반환할 것이다.
//        따라서 DB내에서 자동증가한 pk값을 가져오기 위해서 KeyHolder가 필요하다. keyHolder을 jdbctemplate를 의미하는 template의
//        update메소드의 두번쨰 인자로 넣어주면 자동증가한 pk값을 keyHolder가 갖고 있게 된다.
        template.update(connection->{
//        template.update를 통해서 jdbc의 반복문제를 해결한다. 첫번쨰 인자로는 커넥션관련 람디식을 넣는다. 람다식의 내용은 아래와 같다.
//          => 기존의 jdbc연결과 비슷하다. (template가 내부적으로 connection을 연결해서 가지고 있다. 뭐.. 다른것도 많이 해준다)
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
//        sql과 "id"를 담고 있는 문자열배열을 인자로 넣어줌으로서 PreparedStatement를 만든다. 이래야 DB테이블 안에 있는 컬럼명이
//        id인 값을 가져올 수 있다.
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
//        여기서 만든 ps를 반환해 준다.
        }, keyHolder);
//        지금까지의 과정을 통해서 template.update를 통해서 keyHolder가 db테이블의 자동증가 값으로 채워졌다.
//        (template.update의 두번쨰 인자에 keyHolder가 들어갔네.. 연결되었다고 받아들이자, 내부 라이브러리를 보는 것은 다음에 보자)
        long key = keyHolder.getKey().longValue();
//        이러한 keyHolder을 이용해서 pk를 뽑아낸다음에
        item.setId(key);
        return item;
//        item에 key를 넣어 준다음에 item을 반환하면 된다.
    }
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam){
        String sql = "update item set item_name=?, price=?, quantity=? where id=?";
        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId);
    }
    @Override
    public Optional<Item> findById(Long id){
        String sql = "select id, item_name, price, quantity from item where id=?";
        try{
            Item item = template.queryForObject(sql, itemRowMapper(), id);
//            1. 한건 조회는 queryForObject를 쓰면 된다.
//            2. queryForObject의 두번쨰 인자에는 쿼리 결과를 어떻게 Item으로 만들 것이냐는 매핑정보를 넣어줘야 한다.
            return Optional.of(item);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }
    private RowMapper<Item> itemRowMapper(){
        return (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        };
    }
    @Override
    public List<Item> findAll(ItemSearchCond cond){
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";
        // 동적쿼리
        if(StringUtils.hasText(itemName) || maxPrice != null){
//           StringUtils.hasText() : org.apache.commons.lang에서 제공하거나 SpringFramework에서 기본으로 제공해 준다.
//           StringUtils 클래스는 자바의 String 클래스가 제공하는 문자열 관련 기능을 강화한 클래스라고 보면 된다.
//           그 중에서 StringUtils.hasText()는 문자열 유효성 검증 유틸리티 메소드 이고
//             들어온 문자열이 null인것,
//                          길이가 0인것
//                          공백("" or " ")인것 이
//                          하나라도 해당하면 false를 반환한다.
            sql = sql+" where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if(StringUtils.hasText(itemName)){
            sql = sql+" item_name like concat('%', ?, '%')";
            param.add(itemName);
            andFlag = true;
        }

        if(maxPrice != null){
            if(andFlag){
                sql = sql+" and";
            }
            sql = sql+" price <= ?";
            param.add(maxPrice);
        }

        log.info("sql = {}", sql);
        for(int i=0; i<param.toArray().length; i++){
            System.out.print(param.toArray()[i]);
        }
        return template.query(sql, itemRowMapper(), param.toArray());
    }

}

