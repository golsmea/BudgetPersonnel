package bgpersonnel.budget.budget;


import bgpersonnel.budget.authentification.common.services.UserService;
import bgpersonnel.budget.objectif.Objectif;
import bgpersonnel.budget.objectif.ObjectifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private JavaMailSender mailSender;


    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    // CRUD operations

    /**
     * Create a new budget
     * @param budget to create
     * @return the created budget
     */
    public Budget create(Budget budget) {
        return budgetRepository.save(budget);
    }

    /**
     * Find a budget by its id
     * @param id of the budget
     * @return the budget corresponding to the id
     */
    public Budget findById(Long id) {
        return budgetRepository.findById(id).get();
    }

    /**
     * Find all budgets
     * @return all budgets
     */
    public Iterable<Budget> findAll() {
        return budgetRepository.findAll();
    }

    /**
     * Update a budget
     * @param budget to update
     * @return the updated budget
     */
    public Budget update(Budget budget) {
        return budgetRepository.save(budget);
    }

    /**
     * Delete a budget by its id
     * @param id of the budget to delete
     */
    public void deleteById(Long id) {
        budgetRepository.deleteById(id);
    }


    /**
     * calculate the real annual global budget for a year
     * @param year of the budget
     * @return the real annual global budget that corresponds to the year
     */
    public double calculateAnnualGlobalBudgetForYear( Integer year) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateAnnualGlobalBudgetForYear(year,userId );
    }

    /**
     * calculate the real global budget
     * @return the real global budget
     */
    public double calculateGlobalBudget() {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateGlobalBudget(userId );
    }


    /**
     * calculate the real global budget for each year
     * @return the real global budget for each year
     */
    public List<Map<String, Object>> getTotalAnnualBudgetsByYearAndUser() {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.getTotalAnnualBudgetsByYearAndUser(userId );
    }

    /**
     * calculate the real monthly global budget for a year and a month
     * @param month of the budget
     * @param year of the budget
     * @return the real monthly global budget that corresponds to the year and the month
     */
    public Double calculateMonthlyGlobalBudgetForYearAndMonth( Integer month, Integer year) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateMonthlyGlobalBudgetForYearAndMonth(userId, month, year);
    }


    /**
     * calculate the real monthly global budget for each month of this year
     * @param year of the budget
     * @return the real monthly global budget for each month of this year
     */
    public List<Map<String, Object>> calculateMonthlyGlobalBudgetForYear( Integer year) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateMonthlyGlobalBudgetForYear(year,userId );
    }

    /**
     * calculate the real monthly global budget for each month of this year
     * @param year of the budget
     * @param budgetId of the budget
     * @return the real monthly global budget for each month of this year
     */
    public List<Map<String, Object>> calculateMonthlyBudgetForYearAndCategory(Integer year, Long budgetId) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateMonthlyBudgetForYearAndCategory(year, userId, budgetId);
    }

    /**
     * calculate the real monthly global budget for each month of this year
     * @param month of the budget
     * @param year of the budget
     * @param budgetId of the budget
     * @return the real monthly global budget for each month of this year
     */
    public Double calculateMonthlyBudgetForYearAndMonthAndCategory( Integer month, Integer year, Long budgetId) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateMonthlyBudgetForYearAndMonthAndCategory(userId, month, year, budgetId);
    }

    /**
     * calculate the real monthly global budget for each month of this year
     * @param budgetId of the budget
     * @return the real monthly global budget for each month of this year
     */
    public List<Map<String, Object>> getTotalAnnualBudgetsByYearAndUserAndCategory( Long budgetId) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.getTotalAnnualBudgetsByYearAndUserAndCategory(userId, budgetId);
    }


    /**
     * calculate the real monthly global budget for each month of this year
     * @param year of the budget
     * @param budgetId of the budget
     * @return the real monthly global budget for each month of this year
     */
    public Double calculateAnnualBudgetForYearAndCategory(Integer year, Long budgetId) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateAnnualBudgetForYearAndCategory(year, userId, budgetId);
    }


    /**
     * calculate the budget for a category
     * @param budgetId of the budget
     * @return the budget for a category
     */
    public Double calculateBudgetForCategory( Long budgetId) {
        Long userId = UserService.getIdConnectedUser();
        return budgetRepository.calculateBudgetForCategory( userId, budgetId);
    }


        /**
     * calculate if the budget is over the limit fixed
     * @param budgetId of the budget
     * @return true if the budget is over the limit fixed
     */ public boolean isBudgetDepasse(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId).orElse(null);
        double progressPercentage = 0;
        Calendar calendar = Calendar.getInstance();
        if(budget.isGlobal() == true) {
            switch (budget.getType()) {
                case ANNUEL:
                    progressPercentage = calculateAnnualGlobalBudgetForYear(calendar.get(Calendar.YEAR));
                    break;
                case MENSUEL:
                    progressPercentage = calculateMonthlyGlobalBudgetForYearAndMonth(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                    break;
                case UNIQUE:
                    progressPercentage = calculateGlobalBudget();
                    break;
            }
        } else {
            switch (budget.getType()) {
                case ANNUEL:
                    progressPercentage = calculateAnnualBudgetForYearAndCategory(calendar.get(Calendar.YEAR), budgetId);
                    break;
                case MENSUEL:
                    progressPercentage = calculateMonthlyBudgetForYearAndMonthAndCategory(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), budgetId);
                    break;
                case UNIQUE:
                    progressPercentage = calculateBudgetForCategory(budgetId);
                    break;
            }
        }

        if(progressPercentage >= budget.getMaxAmount()) {
            String subject = "Budget dépassé : " + budget.getName();
            String text = "Vous avez atteint votre bodget " + budget.getName() + " !";

            // envoyer un email à l'utilisateur
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(budget.getUser().getEmail());
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            return true;
        }
        return false;
    }



    /**
     * calculate the Adjustments for each category
     * @return the category with the adjustments
     */
    public List<Map<String, Object>> calculateAdjustments() {

        Calendar calendar = Calendar.getInstance();
        // Récupération du budget mensuel global
        Double monthlyGlobalBudget = calculateMonthlyGlobalBudgetForYearAndMonth( calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

        // Récupération des dépenses mensuelles par catégorie
        List<Map<String, Object>> monthlyExpensesByCategory = calculateMonthlyGlobalBudgetForYear(calendar.get(Calendar.YEAR));

        // Initialisation de la liste des ajustements
        List<Map<String, Object>> adjustments = new ArrayList<>();

        // Calcul des ajustements pour chaque catégorie
        for (Map<String, Object> categoryExpense : monthlyExpensesByCategory) {
            String categoryName = (String) categoryExpense.get("categoryName");
            Double categoryExpenseAmount = (Double) categoryExpense.get("expenseAmount");
            Double categoryBudgetAmount = (Double) categoryExpense.get("budgetAmount");

            // Calcul de l'ajustement pour la catégorie
            Double categoryAdjustment = (categoryExpenseAmount / monthlyGlobalBudget) * categoryBudgetAmount - categoryExpenseAmount;

            // Ajout de l'ajustement à la liste
            Map<String, Object> suggestion = new HashMap<>();
            suggestion.put("from_category", categoryName);
            suggestion.put("amount_adjustemnt", categoryAdjustment);
            suggestion.put("suggestion", "Réaffecter des fonds depuis cette catégorie.");
            adjustments.add(suggestion);

        }

        return adjustments;
    }


}
