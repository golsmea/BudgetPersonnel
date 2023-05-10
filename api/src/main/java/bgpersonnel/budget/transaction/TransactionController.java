package bgpersonnel.budget.transaction;

import bgpersonnel.budget.transaction.dto.SuggestionEconomieDto;
import bgpersonnel.budget.transaction.dto.SumTransactionDto;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public Transaction create(@RequestBody Transaction transaction) {
        return service.create(transaction);
    }

    @PutMapping
    public Transaction update(@RequestBody Transaction transaction) {
        return service.update(transaction);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }


    @GetMapping
    public List<Transaction> findAll() {
        return service.findAll();
    }

    @GetMapping("{id}")
    public Transaction findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/category/{id}")
    public List<Transaction> findByCategory(@PathVariable Long id) {
        return service.findByCategory(id);
    }

    @GetMapping("/date/{id}")
    public List<Transaction> findByDate(@PathVariable String date) {
        return service.findByDate(date);
    }

    @PutMapping("{transactionId}/category/{categoryId}")
    public Transaction addCategory(@PathVariable long transactionId, @PathVariable long categoryId) {
        return service.addCategory(transactionId, categoryId);
    }

    @GetMapping("categories/year/{year}")
    public List<SumTransactionDto> getSumTransactionByCategories(@PathVariable int year) {
        return service.getSumTransactionByCategoriesAndYear(Year.of(year));
    }

    @GetMapping("categories/month/{month}/year/{year}")
    public List<SumTransactionDto> getSumTransactionByCategories(@PathVariable int month, @PathVariable int year) {
        return service.getSumTransactionByCategoriesAndMonth(YearMonth.of(year, month));
    }

    @GetMapping("suggestions")
    public List<SuggestionEconomieDto> getSumTransactionByCategories() {
        return service.getSuggestionEconomie();
    }
}
