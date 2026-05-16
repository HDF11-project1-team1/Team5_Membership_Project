package master.dao;

import common.jdbc.JdbcTemplate;
import master.dto.BranchDto;

import java.util.List;

public class BranchDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int insertBranch(BranchDto branchDto) {
        return insertBranchAndReturnId(branchDto) > 0 ? 1 : 0;
    }

    public int insertBranchAndReturnId(BranchDto branchDto) {
        Integer branchId = jdbcTemplate.queryForObject("SELECT seq_branch.NEXTVAL FROM dual", rs -> rs.getInt(1));
        if (branchId == null) {
            return 0;
        }

        String sql = "INSERT INTO branch (branch_id, branch_name, branch_address) VALUES (?, ?, ?)";
        int result = jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, branchId);
            pstmt.setString(2, branchDto.getBranchName());
            pstmt.setString(3, branchDto.getBranchAddress());
        });

        return result > 0 ? branchId : 0;
    }

    public int insertBranchWithId(BranchDto branchDto) {
        String sql = "INSERT INTO branch (branch_id, branch_name, branch_address) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, branchDto.getBranchId());
            pstmt.setString(2, branchDto.getBranchName());
            pstmt.setString(3, branchDto.getBranchAddress());
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

