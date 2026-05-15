package master.dao;

import common.jdbc.JdbcTemplate;
import master.dto.BranchDto;

import java.util.List;

public class BranchDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int insertBranch(BranchDto branchDto) {
        String sql = "INSERT INTO branch (branch_id, branch_name, branch_address) VALUES (seq_branch.NEXTVAL, ?, ?)";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setString(1, branchDto.getBranchName());
            pstmt.setString(2, branchDto.getBranchAddress());
        });
    }

    public List<BranchDto> selectAllBranches() {
        String sql = "SELECT branch_id, branch_name, branch_address FROM branch ORDER BY branch_id";
        return jdbcTemplate.query(sql, rs -> new BranchDto(
                rs.getInt("branch_id"),
                rs.getString("branch_name"),
                rs.getString("branch_address")
        ));
    }

    public BranchDto selectBranchById(int branchId) {
        String sql = "SELECT branch_id, branch_name, branch_address FROM branch WHERE branch_id = ?";
        return jdbcTemplate.queryForObject(sql,
                pstmt -> pstmt.setInt(1, branchId),
                rs -> new BranchDto(
                        rs.getInt("branch_id"),
                        rs.getString("branch_name"),
                        rs.getString("branch_address")
                ));
    }

    public int updateBranch(BranchDto branchDto) {
        String sql = "UPDATE branch SET branch_name = ?, branch_address = ? WHERE branch_id = ?";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setString(1, branchDto.getBranchName());
            pstmt.setString(2, branchDto.getBranchAddress());
            pstmt.setInt(3, branchDto.getBranchId());
        });
    }

    public int deleteBranch(int branchId) {
        String sql = "DELETE FROM branch WHERE branch_id = ?";
        return jdbcTemplate.update(sql, pstmt -> pstmt.setInt(1, branchId));
    }

    public boolean existsBranchId(int branchId) {
        String sql = "SELECT COUNT(*) FROM branch WHERE branch_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, branchId));
    }

    public boolean existsBranchName(String branchName) {
        String sql = "SELECT COUNT(*) FROM branch WHERE branch_name = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setString(1, branchName));
    }

    public boolean existsPurchaseByBranchId(int branchId) {
        String sql = "SELECT COUNT(*) FROM purchase WHERE branch_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, branchId));
    }
}

