package bgpersonnel.budget.category;

import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    /**
     * Retourne la liste des catégories.
     *
     * @return la liste de toutes les catégories.
     */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Sauvegarde une nouvelle catégorie dans la base de données
     *
     * @param category à sauvegarder
     * @return catégorie sauvegarder avec son id.
     */
    public Category create(Category category) {
        User user = this.userService.getConnectedUser();
        category.setUser(user);
        category.setCreatedAt(LocalDateTime.now());
        category.setCreatedBy(user.getName());
        return categoryRepository.save(category);
    }

    /**
     * Remplace les données d'une catégorie dans la base de données en fonction de son id.
     *
     * @param category nouvelles données à sauvegarder
     * @return la catégorie avec les nouvelles données.
     */
    public Category update(Category category) {
        User user = this.userService.getConnectedUser();
        category.setUser(user);
        category.setUpdatedAt(LocalDateTime.now());
        category.setUpdatedBy(user.getName());
        return this.categoryRepository.save(category);
    }


    /**
     * Recherche une catégorie en fonction de son id.
     *
     * @param id de la catégorie à rechercher
     * @return la catégorie portant l'id passé en paramètre
     * @throws ResponseStatusException si aucune catégorie ne porte cet id
     */
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public int countByUser(User user) {
        return categoryRepository.countByUser(user);
    }

    /**
     * supprime une catégorie en fonction de son id.
     *
     * @param id de la catégorie à supprimer.
     */
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }


    public List<Category> findByUser(User user) {
        return categoryRepository.findByUser(user);
    }

}
