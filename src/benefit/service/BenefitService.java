package benefit.service;

import benefit.dao.BenefitDao;
import benefit.dto.CoffeeHistoryDto;
import benefit.dto.LoungeHistoryDto;
import benefit.dto.ParkingHistoryDto;
import benefit.dto.VehicleDto;

public class BenefitService {

    private final BenefitDao benefitDao = new BenefitDao();

    // // 차량 등록 서비스
    // public void registerVehicle(VehicleDto vehicle) {
    // benefitDao.insertVehicle(vehicle);
    // }

    // 주차 이용 기록 조회 서비스
    public void findParkingHistory(ParkingHistoryDto history) {
        // 내부에 필요한 로직 구현 (예: DAO 호출)
    }

    // // 라운지 이용 서비스
    // public void useLounge(LoungeHistoryDto history) {
    // benefitDao.insertLoungeHistory(history);
    // }

    // // 커피 쿠폰 사용 서비스
    // public void useCoffeeCoupon(CoffeeHistoryDto history) {
    // benefitDao.insertCoffeeHistory(history);
    // }
}
