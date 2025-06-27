package org.yearup.data.mysql;

import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySqlCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        Map<Integer, ShoppingCartItem> items = new HashMap<>();
        ShoppingCartItem item = new ShoppingCartItem();
        ShoppingCart shoppingCart = new ShoppingCart();
        String query = """
                SELECT * FROM shopping_cart
                WHERE id = ?
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query))
        {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                item.setProduct(MySqlCartDao.mapRowProduct(rs));
                items.put(rs.getInt("quantity"), item);
            }
            shoppingCart.setItems(items);
        } catch (SQLException e) {
            System.out.println("Failure to run query in: Shopping Cart");
        }
        return shoppingCart;
    }

    private static Product mapRowProduct (ResultSet resultSet) throws SQLException {
        int productId = resultSet.getInt("product_id");
        String name = resultSet.getString("name");
        BigDecimal price = resultSet.getBigDecimal("price");
        int categoryId = resultSet.getInt("category_id");
        String description = resultSet.getString("description");
        String color = resultSet.getString("color");
        int stock = resultSet.getInt("stock");
        boolean isFeatured = resultSet.getBoolean("featured");
        String imageUrl = resultSet.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }
}
