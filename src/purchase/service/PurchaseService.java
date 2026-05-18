package purchase.service;

import membership.dto.MembershipDto;
import purchase.dao.PurchaseHistoryDao;
import purchase.dto.PurchaseHistoryDto;

import java.util.List;

public class PurchaseService {

    private final PurchaseHistoryDao purchaseHistoryDao = new PurchaseHistoryDao();

    public List<PurchaseHistoryDto> getPurchaseHistoryByUserId(int userId) {
        return purchaseHistoryDao.selectByUserId(userId);
    }

    public List<PurchaseHistoryDto> getPurchaseHistoryByMembershipId(int membershipId) {
        return purchaseHistoryDao.selectByMembershipId(membershipId);
    }

    public List<MembershipDto> getAllMemberships() {
        return purchaseHistoryDao.selectAllMemberships();
    }
}

