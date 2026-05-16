package master.service;

import master.dao.CategoryDao;
import master.dto.CategoryDto;
import master.dto.request.CategoryRegisterRequestDto;

import java.util.List;

import static common.validation.InputValidator.hasText;
import static common.validation.InputValidator.isValidId;

public class CategoryService {

    private final CategoryDao categoryDao = new CategoryDao();

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

    public boolean registerCategory(CategoryRegisterRequestDto requestDto) {
        if (requestDto == null || !hasText(requestDto.getCategoryName())) {
            return false;
        }
        if (categoryDao.existsCategoryName(requestDto.getCategoryName())) {
            return false;
        }

        CategoryDto categoryDto = new CategoryDto(0, requestDto.getCategoryName());
        return categoryDao.insertCategoryAndReturnId(categoryDto) > 0;
    }

    public List<CategoryDto> findCategoryList() {
        return categoryDao.selectAllCategories();
    }

    public CategoryDto findCategoryDetail(int categoryId) {
        if (!isValidId(categoryId)) {
            return null;
        }
        return categoryDao.selectCategoryById(categoryId);
    }

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
