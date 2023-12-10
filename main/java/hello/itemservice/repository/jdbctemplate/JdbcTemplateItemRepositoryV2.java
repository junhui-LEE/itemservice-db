package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource){
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(itemName, price, quantity) " +
                "values(:itemName, :price, :quantity)";
//                      물음표가 아니라 이름 기반으로 써 줘야 한다.
        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
//        save()에 넘어온 item객체의 필드명을 보고 파라미터를 만든다. (여러 방법중 하나이다)
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);
//        파라미터를 템플릿에 넘겨줘야 하는데 이때 우리가 앞서 구현한 이름기반으로 된 param을 넘겨줘야 한다. (param은 이름기반이지 순서 기반이 아니다)
        Long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item " +
                "set itemName=:itemName," +
                   " price=:price," +
                   " quantity=:quantity" +
               " where id=:id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);
        template.update(sql, param);
//        순서 기반이 아니라 이름 기반의 파라미터를 넘기는데 여기서는 new MapSqlParameterSource()을 통해서 이름 기반의 파라미터인 param을 만들었다.
//        save()에서 썼던 BeanPropertySqlParameterSource를 써도 되는데 개인 학습이기 때문에 여러가지 케이스를 다뤄보는 것이다.
    }

    @Override
    public Optional<Item> findById(Long id){
        String sql = "select id, item_name, price, quantity from item where id=:id";
        try{
            Map<String, Object> param = Map.of("id", id);
//            이름기반의 파라미터를 Map으로 만들 수도 있다. Map.of()는 java 문법이다.
            Item item = template.queryForObject(sql, param, itemRowMapper());
//            Map으로 만든 파라미터(param)을 두번째 인자에 두고 반환된 결과를 Item객체로 바꾸는 코드(itemRowMapper())는 세번째 인자에 넣는다.
            return Optional.of(item);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }
    private RowMapper<Item> itemRowMapper(){
//        return (rs, rowNum) -> {
//            Item item = new Item();
//            item.setId(rs.getLong("id"));
//            item.setItemName(rs.getString("item_name"));
//            item.setPrice(rs.getInt("price"));
//            item.setQuantity(rs.getInt("quantity"));
//            return item;
//        };
        return BeanPropertyRowMapper.newInstance(Item.class); // camel 변환 지원
    }
    @Override
    public List<Item> findAll(ItemSearchCond cond){
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";
        // 동적쿼리
        if(StringUtils.hasText(itemName) || maxPrice != null){
            sql = sql+" where";
        }

        boolean andFlag = false;
//        List<Object> param = new ArrayList<>();
        if(StringUtils.hasText(itemName)){
            sql = sql+" item_name like concat('%', :itemName, '%')";
//            param.add(itemName);
            andFlag = true;
        }

        if(maxPrice != null){
            if(andFlag){
                sql = sql+" and";
            }
            sql = sql+" price <= :maxPrice";
//            param.add(maxPrice);
        }

        log.info("sql = {}", sql);
//        for(int i=0; i<param.toArray().length; i++){
//            System.out.print(param.toArray()[i]);
//        }
        return template.query(sql, param, itemRowMapper());
    }
}
