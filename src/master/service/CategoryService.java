package master.service;

import master.dao.CategoryDao;
import master.dto.CategoryDto;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class CategoryService {

    private final CategoryDao categoryDao = new CategoryDao();

    // ===== 카테고리 등록 =====
    public boolean registerCategory(String categoryName) {
        if (!hasText(categoryName)) {
            return false;
        }
        if (categoryDao.existsCategoryName(categoryName)) {
            return false;
        }

        CategoryDto categoryDto = new CategoryDto(0, categoryName);
        return categoryDao.insertCategory(categoryDto) > 0;
    }

    // ===== 카테고리 목록 조회 =====
    public List<CategoryDto> getCategoryList() {
        return categoryDao.selectAllCategories();
    }

    public CategoryDto getCategoryDetail(int categoryId) {
        if (!isValidId(categoryId)) {
            return null;
        }
        return categoryDao.selectCategoryById(categoryId);
    }

    // ===== 카테고리 수정 =====
    public boolean updateCategory(int categoryId, String categoryName) {
        if (!isValidId(categoryId) || !hasText(categoryName)) {
            return false;
        }
        if (!categoryDao.existsCategoryId(categoryId)) {
            return false;
        }

        CategoryDto categoryDto = new CategoryDto(categoryId, categoryName);
        return categoryDao.updateCategory(categoryDto) > 0;
    }

}

