package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto mapToCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category mapToCategory(NewCategoryDto request);

    default Page<CategoryDto> toDtoPage(Page<Category> categoryPage) {
        return categoryPage.map(this::mapToCategoryDto);
    }
}
