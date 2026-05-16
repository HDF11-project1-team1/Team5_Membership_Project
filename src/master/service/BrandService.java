package master.service;

import common.exception.DuplicateException;
import common.exception.NotFoundException;
import common.exception.ValidationException;
import master.dao.BrandDao;
import master.dao.CategoryDao;
import master.dto.BrandDto;
import master.dto.request.BrandRegisterRequestDto;
import policy.service.DefaultPolicyService;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;
import static common.validation.InputValidator.isValidMileageRate;

public class BrandService {

    private final BrandDao brandDao = new BrandDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final DefaultPolicyService defaultPolicyService = new DefaultPolicyService();

    // ===== 브랜드 등록 =====
    public boolean registerBrand(String brandName, int categoryId) {
        if (!hasText(brandName) || !isValidId(categoryId)) {
            throw new ValidationException("브랜드명과 카테고리 ID는 필수입니다.");
        }
        if (!categoryDao.existsCategoryId(categoryId)) {
            throw new NotFoundException("브랜드를 등록할 카테고리를 찾을 수 없습니다.");
        }
        if (brandDao.existsBrandName(brandName)) {
            throw new DuplicateException("이미 등록된 브랜드명입니다.");
        }

        BrandDto brandDto = new BrandDto(0, categoryId, brandName);
        return brandDao.insertBrand(brandDto) > 0;
    }

    // ===== 브랜드 목록 조회 =====
    public List<BrandDto> getBrandList() {
        return brandDao.selectAllBrands();
    }

    // ===== 브랜드 상세 조회 =====
    public BrandDto getBrandDetail(int brandId) {
        if (!isValidId(brandId)) {
            throw new ValidationException("브랜드 ID는 1 이상이어야 합니다.");
        }
        BrandDto brand = brandDao.selectBrandById(brandId);
        if (brand == null) {
            throw new NotFoundException("브랜드를 찾을 수 없습니다.");
        }
        return brand;
    }

    // ===== 카테고리별 브랜드 조회 =====
    public List<BrandDto> getBrandListByCategory(int categoryId) {
        if (!isValidId(categoryId)) {
            throw new ValidationException("카테고리 ID는 1 이상이어야 합니다.");
        }
        if (!categoryDao.existsCategoryId(categoryId)) {
            throw new NotFoundException("카테고리를 찾을 수 없습니다.");
        }
        return brandDao.selectBrandsByCategoryId(categoryId);
    }

    // ===== 브랜드 수정 =====
    public boolean updateBrand(int brandId, String brandName, int categoryId) {
        if (!isValidId(brandId) || !hasText(brandName) || !isValidId(categoryId)) {
            throw new ValidationException("브랜드 ID, 브랜드명, 카테고리 ID는 필수입니다.");
        }
        if (!brandDao.existsBrandId(brandId)) {
            throw new NotFoundException("수정할 브랜드를 찾을 수 없습니다.");
        }
        if (!categoryDao.existsCategoryId(categoryId)) {
            throw new NotFoundException("브랜드에 연결할 카테고리를 찾을 수 없습니다.");
        }

        BrandDto brandDto = new BrandDto(brandId, categoryId, brandName);
        return brandDao.updateBrand(brandDto) > 0;
    }
}
