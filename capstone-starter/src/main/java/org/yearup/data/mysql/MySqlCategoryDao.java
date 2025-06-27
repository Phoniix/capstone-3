package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        String query = """
                    SELECT * FROM categories;
                    """;
        List<Category> c = new ArrayList<>();
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                c.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failure to run query in: MySqlCategoryDao");
            throw new RuntimeException();
        }
        return c;
    }

    @Override
    public Category getById(int categoryId)
    {
        String query = """
                    SELECT * FROM categories WHERE category_id = ?;
                    """;
        Category category;
        try (Connection connection = this.dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, categoryId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("category_id") == categoryId) {
                        return mapRow(rs);
                    }
                }
        }   catch (SQLException e) {
            System.out.println("Failure to run query in: MySqlCategoryDao");
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        Category c;
        int counter;
        String query = """
                INSERT INTO categories (name, description)
                VALUES (?, ?)
                """;
        try (Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            counter = stmt.executeUpdate();
            System.out.println("Rows Updated: " + counter);
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                while (keys.next()) {
                    category.setCategoryId(keys.getInt(1));
                }
                return category;
            }
        } catch (SQLException e) {
            System.out.println("Failure to run query in: MySqlCategoryDao");
            throw new RuntimeException();
        }
    }

    @Override
    public void update(int categoryId, Category category)
    {
        int counter;
        String query = """
                UPDATE categories
                SET name = ?
                SET description = ?
                WHERE category_id = ?
                """;
        try (Connection connection = dataSource.getConnection();
            PreparedStatement stmt  = connection.prepareStatement(query)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, categoryId);
            counter =  stmt.executeUpdate();
            if (counter >= 1) {
                System.out.println("Rows Affected: " + counter);
            }
        } catch (SQLException e) {
            System.out.println("Failure to run query in: MySqlCategoryDao");
            throw new RuntimeException();
        }
    }

    @Override
    public void delete(int categoryId)
    {
        int counter;
        String query = """
                DELETE FROM categories
                WHERE category_id = ?;
        """;
        try (Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            counter = stmt.executeUpdate();
            if (counter >= 1) {
                System.out.println("Rows Affected: " + counter);
            }
        } catch (SQLException e) {
            System.out.println("Failure to run query in: MySqlCategoryDao");
            throw new RuntimeException();
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
