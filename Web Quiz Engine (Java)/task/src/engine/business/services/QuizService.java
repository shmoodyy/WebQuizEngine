package engine.business.services;

import engine.business.models.*;
import engine.persistence.CompletedQuizRepository;
import engine.persistence.QuizRepository;
import engine.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final QuizRepository quizRepository;

    @Autowired
    private final CompletedQuizRepository completedQuizRepository;

    private final int MAX_QUIZZES_PER_PAGE = 10;

    public Quiz createQuiz(String email, Quiz quiz) {
        var user = userRepository.findUserByEmailIgnoreCase(email);
        quiz.setUser(user);
        return quizRepository.save(quiz);
    }

    public Page<Quiz> getQuizzes(int page) {
        return quizRepository.findAll(PageRequest.of(page, MAX_QUIZZES_PER_PAGE));
    }

    public Quiz getQuiz(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    public AnswerResult postAnswer(String email, Quiz quiz, AnswerObj answer) {
        if (answer.getAnswer().equals(quiz.getAnswer())) {
            var completedQuiz = new CompletedQuiz();
            var user = userRepository.findUserByEmailIgnoreCase(email);
            completedQuiz.setUser(user);
            completedQuiz.setId(quiz.getId());
            completedQuizRepository.save(completedQuiz);
            return new AnswerResult(true, "Congratulations, you're right!");
        } else {
            return new AnswerResult(false, "Wrong answer! Please, try again.");
        }
    }

    public Page<CompletedQuiz> getCompletedQuizzes(String email, int page) {
        var user = userRepository.findUserByEmailIgnoreCase(email);
        return completedQuizRepository
                .findAllByUserOrderByCompletedAtDesc(user, PageRequest.of(page, MAX_QUIZZES_PER_PAGE));
    }

    @Transactional
    public Optional<Quiz> deleteQuiz(String email, Long id) {
        var potentialQuiz = quizRepository.findById(id);
        if (potentialQuiz.isPresent()) {
            Quiz quiz = potentialQuiz.get();
            if (email.equals(quiz.getUser().getEmail())) {
                quizRepository.deleteById(id);
                return Optional.of(quiz);
            }
        }
        return Optional.empty();
    }

    public boolean doesNotExistById(Long id) {
        return !quizRepository.existsById(id);
    }
}