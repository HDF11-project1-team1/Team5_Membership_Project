package master.service;

import common.exception.DuplicateException;
import common.exception.NotFoundException;
import common.exception.ValidationException;
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
            throw new ValidationException("카테고리명은 필수입니다.");
        }
        if (categoryDao.existsCategoryName(categoryName)) {
            throw new DuplicateException("이미 등록된 카테고리명입니다.");
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
            throw new ValidationException("카테고리 ID는 1 이상이어야 합니다.");
        }
        CategoryDto category = categoryDao.selectCategoryById(categoryId);
        if (category == null) {
            throw new NotFoundException("카테고리를 찾을 수 없습니다.");
        }
        return category;
    }

    // ===== 카테고리 수정 =====
    public boolean updateCategory(int categoryId, String categoryName) {
        if (!isValidId(categoryId) || !hasText(categoryName)) {
            throw new ValidationException("카테고리 ID와 카테고리명은 필수입니다.");
        }
        if (!categoryDao.existsCategoryId(categoryId)) {
            throw new NotFoundException("수정할 카테고리를 찾을 수 없습니다.");
        }

        CategoryDto categoryDto = new CategoryDto(categoryId, categoryName);
        return categoryDao.updateCategory(categoryDto) > 0;
    }
}
