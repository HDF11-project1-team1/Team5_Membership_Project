package master.dao;

import common.jdbc.JdbcTemplate;
import master.dto.BrandDto;

import java.util.List;

public class BrandDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int insertBrand(BrandDto brandDto) {
        String sql = "INSERT INTO brand (brand_id, category_id, brand_name) VALUES (seq_brand.NEXTVAL, ?, ?)";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, brandDto.getCategoryId());
            pstmt.setString(2, brandDto.getBrandName());
        });
    }

    public List<BrandDto> selectAllBrands() {
        String sql = "SELECT brand_id, category_id, brand_name FROM brand ORDER BY brand_id";
        return jdbcTemplate.query(sql, rs -> new BrandDto(
                rs.getInt("brand_id"),
                rs.getInt("category_id"),
                rs.getString("brand_name")
        ));
    }

    public BrandDto selectBrandById(int brandId) {
        String sql = "SELECT brand_id, category_id, brand_name FROM brand WHERE brand_id = ?";
        return jdbcTemplate.queryForObject(sql,
                pstmt -> pstmt.setInt(1, brandId),
                rs -> new BrandDto(
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getString("brand_name")
                ));
    }

    public List<BrandDto> selectBrandsByCategoryId(int categoryId) {
        String sql = "SELECT brand_id, category_id, brand_name FROM brand WHERE category_id = ? ORDER BY brand_id";
        return jdbcTemplate.query(sql,
                pstmt -> pstmt.setInt(1, categoryId),
                rs -> new BrandDto(
                        rs.getInt("brand_id"),
                        rs.getInt("category_id"),
                        rs.getString("brand_name")
                ));
    }

    public int updateBrand(BrandDto brandDto) {
        String sql = "UPDATE brand SET category_id = ?, brand_name = ? WHERE brand_id = ?";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, brandDto.getCategoryId());
            pstmt.setString(2, brandDto.getBrandName());
            pstmt.setInt(3, brandDto.getBrandId());
        });
    }

    public int deleteBrand(int brandId) {
        String sql = "DELETE FROM brand WHERE brand_id = ?";
        return jdbcTemplate.update(sql, pstmt -> pstmt.setInt(1, brandId));
    }

    public boolean existsBrandId(int brandId) {
        String sql = "SELECT COUNT(*) FROM brand WHERE brand_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, brandId));
    }

    public boolean existsBrandName(String brandName) {
        String sql = "SELECT COUNT(*) FROM brand WHERE brand_name = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setString(1, brandName));
    }

    public boolean hasPurchaseByBrandId(int brandId) {
        String sql = "SELECT COUNT(*) FROM purchase WHERE brand_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, brandId));
    }
}
