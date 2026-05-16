package master.service;

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

    public boolean registerBrand(BrandRegisterRequestDto requestDto) {
        if (requestDto == null) {
            return false;
        }
        if (!hasText(requestDto.getBrandName()) || !isValidId(requestDto.getCategoryId())) {
            return false;
        }
        if (!isValidMileageRate(requestDto.getDefaultMileageRate())) {
            return false;
        }
        if (!categoryDao.existsCategoryId(requestDto.getCategoryId())) {
            return false;
        }
        if (brandDao.existsBrandName(requestDto.getBrandName())) {
            return false;
        }

        BrandDto brandDto = new BrandDto(0, requestDto.getCategoryId(), requestDto.getBrandName());
        int brandId = brandDao.insertBrandAndReturnId(brandDto);
        if (!isValidId(brandId)) {
            return false;
        }

        return defaultPolicyService.createDefaultPoliciesForNewBrand(brandId, requestDto);
    }

    public List<BrandDto> findBrandList() {
        return brandDao.selectAllBrands();
    }

    public BrandDto findBrandDetail(int brandId) {
        if (!isValidId(brandId)) {
            return null;
        }
        return brandDao.selectBrandById(brandId);
    }

    public List<BrandDto> findBrandListByCategory(int categoryId) {
        if (!isValidId(categoryId)) {
            return null;
        }
        return brandDao.selectBrandsByCategoryId(categoryId);
    }

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
