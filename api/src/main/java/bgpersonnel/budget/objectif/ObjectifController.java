package bgpersonnel.budget.objectif;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/objectifs")
public class ObjectifController {

    @Autowired
    private final ObjectifService objectifService;

    public ObjectifController(ObjectifService objectifService) {
        this.objectifService = objectifService;
    }

    @GetMapping("{id}")
    public Optional<Objectif> findById(Long id) {
        return Optional.ofNullable(objectifService.findById(id));
    }

    @GetMapping("/")
    public Iterable<Objectif> findAll() {
        return objectifService.findAll();
    }

    @PostMapping("/")
    public Objectif create(Objectif objectif) {
        return objectifService.createObjectif(objectif);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        objectifService.deleteById(id);
    }

    @PutMapping
    public Objectif update(@RequestBody Objectif objectif) {
        return objectifService.update(objectif);
    }

    @GetMapping("/user/{id}")
    public Iterable<Objectif> findByUser(@PathVariable Long id) {
        return objectifService.findByUser(id);
    }

    /**
     * Calculate the progress percentage of an objectif
     * @param id of the objectif
     * @return the progress percentage
     */
    @GetMapping("/{id}/progression")
    public ResponseEntity<Double> getProgression(@PathVariable Long id) {
        Double progression = objectifService.calculateProgressPercentage(id);
        return ResponseEntity.ok(progression);
    }

    /**
     * Check if an objectif is reached
     * @param id of the objectif
     * @return true if the objectif is reached, false otherwise
     */
    @GetMapping("/verifier/{id}")
    public ResponseEntity<Boolean> isObjectifAtteint(@PathVariable Long id) {
        return ResponseEntity.ok(objectifService.isObjectifAtteint(id));
    }

}
