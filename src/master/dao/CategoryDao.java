package master.dao;

import common.jdbc.JdbcTemplate;
import master.dto.CategoryDto;

import java.util.List;

public class CategoryDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int insertCategory(CategoryDto categoryDto) {
        String sql = "INSERT INTO category (category_id, category_name) VALUES (seq_category.NEXTVAL, ?)";
        return jdbcTemplate.update(sql, pstmt -> pstmt.setString(1, categoryDto.getCategoryName()));
    }

    public List<CategoryDto> selectAllCategories() {
        String sql = "SELECT category_id, category_name FROM category ORDER BY category_id";
        return jdbcTemplate.query(sql, rs -> new CategoryDto(
                rs.getInt("category_id"),
                rs.getString("category_name")
        ));
    }

    public CategoryDto selectCategoryById(int categoryId) {
        String sql = "SELECT category_id, category_name FROM category WHERE category_id = ?";
        return jdbcTemplate.queryForObject(sql,
                pstmt -> pstmt.setInt(1, categoryId),
                rs -> new CategoryDto(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                ));
    }

    public int updateCategory(CategoryDto categoryDto) {
        String sql = "UPDATE category SET category_name = ? WHERE category_id = ?";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setString(1, categoryDto.getCategoryName());
            pstmt.setInt(2, categoryDto.getCategoryId());
        });
    }

    public int deleteCategory(int categoryId) {
        String sql = "DELETE FROM category WHERE category_id = ?";
        return jdbcTemplate.update(sql, pstmt -> pstmt.setInt(1, categoryId));
    }

    public boolean existsCategoryId(int categoryId) {
        String sql = "SELECT COUNT(*) FROM category WHERE category_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, categoryId));
    }

    public boolean existsCategoryName(String categoryName) {
        String sql = "SELECT COUNT(*) FROM category WHERE category_name = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setString(1, categoryName));
    }

    public boolean existsBrandByCategoryId(int categoryId) {
        String sql = "SELECT COUNT(*) FROM brand WHERE category_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, categoryId));
    }
}

