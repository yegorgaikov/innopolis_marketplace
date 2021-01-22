package root.dao;

import root.pojo.Item;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pattern Facade
 */

public interface JdbcItemDao {

    Item addItem(Item item);

    Item updateItem(Item item);

    boolean deleteItemById(BigDecimal itemId);

    List<Item> getAllItems();

    Item getItemById(BigDecimal itemId);
}
