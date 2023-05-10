package bgpersonnel.budget.category;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public Category create(@RequestBody Category category) {
        return service.create(category);
    }

    @PutMapping("{id}")
    public Category update(@RequestBody Category category) {
        return service.update(category);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    @GetMapping
    public List<Category> findAll() {
        return service.findAll();
    }

    @GetMapping("{id}")
    public Category findById(@PathVariable Long id) {
        return service.findById(id);
    }

}
