package master.dao;

import common.jdbc.JdbcTemplate;
import master.dto.LoungeDto;

import java.util.List;

public class LoungeDao {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public int insertLounge(LoungeDto loungeDto) {
        return insertLoungeAndReturnId(loungeDto) > 0 ? 1 : 0;
    }

    public int insertLoungeAndReturnId(LoungeDto loungeDto) {
        Integer loungeId = jdbcTemplate.queryForObject("SELECT seq_lounge.NEXTVAL FROM dual", rs -> rs.getInt(1));
        if (loungeId == null) {
            return 0;
        }

        String sql = "INSERT INTO lounge (lounge_id, lounge_name) VALUES (?, ?)";
        int result = jdbcTemplate.update(sql, pstmt -> {
            pstmt.setInt(1, loungeId);
            pstmt.setString(2, loungeDto.getLoungeName());
        });

        return result > 0 ? loungeId : 0;
    }

    public List<LoungeDto> selectAllLounges() {
        String sql = "SELECT lounge_id, lounge_name FROM lounge ORDER BY lounge_id";
        return jdbcTemplate.query(sql, rs -> new LoungeDto(
                rs.getInt("lounge_id"),
                rs.getString("lounge_name")
        ));
    }

    public LoungeDto selectLoungeById(int loungeId) {
        String sql = "SELECT lounge_id, lounge_name FROM lounge WHERE lounge_id = ?";
        return jdbcTemplate.queryForObject(sql,
                pstmt -> pstmt.setInt(1, loungeId),
                rs -> new LoungeDto(
                        rs.getInt("lounge_id"),
                        rs.getString("lounge_name")
                ));
    }

    public int updateLounge(LoungeDto loungeDto) {
        String sql = "UPDATE lounge SET lounge_name = ? WHERE lounge_id = ?";
        return jdbcTemplate.update(sql, pstmt -> {
            pstmt.setString(1, loungeDto.getLoungeName());
            pstmt.setInt(2, loungeDto.getLoungeId());
        });
    }

    public int deleteLounge(int loungeId) {
        String sql = "DELETE FROM lounge WHERE lounge_id = ?";
        return jdbcTemplate.update(sql, pstmt -> pstmt.setInt(1, loungeId));
    }

    public boolean existsLoungeId(int loungeId) {
        String sql = "SELECT COUNT(*) FROM lounge WHERE lounge_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, loungeId));
    }

    public boolean existsLoungeName(String loungeName) {
        String sql = "SELECT COUNT(*) FROM lounge WHERE lounge_name = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setString(1, loungeName));
    }

    public boolean existsLoungePolicyByLoungeId(int loungeId) {
        String sql = "SELECT COUNT(*) FROM lounge_policy WHERE lounge_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, loungeId));
    }

    public boolean existsLoungeHistoryByLoungeId(int loungeId) {
        String sql = "SELECT COUNT(*) FROM lounge_history WHERE lounge_id = ?";
        return jdbcTemplate.exists(sql, pstmt -> pstmt.setInt(1, loungeId));
    }
}

