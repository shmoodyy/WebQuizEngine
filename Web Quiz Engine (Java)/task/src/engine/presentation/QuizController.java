package engine.presentation;

import engine.business.models.AnswerObj;
import engine.business.models.Quiz;
import engine.business.services.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<Object> createQuiz(@Valid @RequestBody Quiz quiz,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizService.createQuiz(userDetails.getUsername(), quiz));
    }

    @GetMapping
    public ResponseEntity<Object> getQuizPage(@RequestParam int page) {
        return ResponseEntity.ok(quizService.getQuizzes(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getQuiz(@PathVariable Long id) {
        if (quizService.doesNotExistById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quizService.getQuiz(id));
    }

    @PostMapping("/{id}/solve")
    public ResponseEntity<Object> postAnswer(@PathVariable Long id, @Valid @RequestBody AnswerObj answer,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (quizService.doesNotExistById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quizService.postAnswer(userDetails.getUsername(), quizService.getQuiz(id), answer));
    }

    @GetMapping("/completed")
    public ResponseEntity<Object> getCompletedQuizPage(@RequestParam int page,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizService.getCompletedQuizzes(userDetails.getUsername(), page));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteQuiz(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (quizService.doesNotExistById(id)) {
            return ResponseEntity.notFound().build();
        } if (quizService.deleteQuiz(userDetails.getUsername(), id).isPresent()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}