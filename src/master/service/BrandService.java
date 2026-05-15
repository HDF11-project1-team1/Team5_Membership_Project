package master.service;

import master.dao.BrandDao;
import master.dao.CategoryDao;
import master.dto.BrandDto;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class BrandService {

    private final BrandDao brandDao = new BrandDao();
    private final CategoryDao categoryDao = new CategoryDao();

    // ===== 브랜드 등록 =====
    public boolean registerBrand(String brandName, int categoryId) {
        if (!hasText(brandName) || !isValidId(categoryId)) {
            return false;
        }
        if (!categoryDao.existsCategoryId(categoryId)) {
            return false;
        }
        if (brandDao.existsBrandName(brandName)) {
            return false;
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
            return null;
        }
        return brandDao.selectBrandById(brandId);
    }

    // ===== 카테고리별 브랜드 조회 =====
    public List<BrandDto> getBrandListByCategory(int categoryId) {
        if (!isValidId(categoryId)) {
            return null;
        }
        return brandDao.selectBrandsByCategoryId(categoryId);
    }

    // ===== 브랜드 수정 =====
    public boolean updateBrand(int brandId, String brandName, int categoryId) {
        if (!isValidId(brandId) || !hasText(brandName) || !isValidId(categoryId)) {
            return false;
        }
        if (!brandDao.existsBrandId(brandId) || !categoryDao.existsCategoryId(categoryId)) {
            return false;
        }

        BrandDto brandDto = new BrandDto(brandId, categoryId, brandName);
        return brandDao.updateBrand(brandDto) > 0;
    }
}
