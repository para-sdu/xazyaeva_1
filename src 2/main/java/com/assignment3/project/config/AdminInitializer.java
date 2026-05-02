package com.assignment3.project.config;

import com.assignment3.project.entities.Category;
import com.assignment3.project.entities.Donation;
import com.assignment3.project.entities.Image;
import com.assignment3.project.entities.Project;
import com.assignment3.project.entities.User;
import com.assignment3.project.entities.VolunteerEvent;
import com.assignment3.project.enums.UserRole;
import com.assignment3.project.repositories.CategoryRepository;
import com.assignment3.project.repositories.DonationRepository;
import com.assignment3.project.repositories.ProjectRepository;
import com.assignment3.project.repositories.UserRepository;
import com.assignment3.project.repositories.VolunteerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;
    private final DonationRepository donationRepository;
    private final VolunteerEventRepository volunteerEventRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User admin = ensureUser(
                "admin@azharfund.kz",
                "Администратор AZHAR",
                "admin123",
                UserRole.ADMIN,
                true
        );

        User donor = ensureUser(
                "donor@azharfund.kz",
                "Демо донор",
                "donor12345",
                UserRole.DONOR,
                true
        );

        seedCategoriesProjectsAndEvents(admin, donor);
    }

    private User ensureUser(String email, String fullName, String password, UserRole role, boolean verified) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setVerified(verified);

            User saved = userRepository.save(user);
            log.info("Demo user created with email: {}", email);
            return saved;
        });
    }

    private void seedCategoriesProjectsAndEvents(User admin, User donor) {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    category("Лечение", "Сборы на операции, лекарства и реабилитацию."),
                    category("Образование", "Помощь детям и студентам с учебой, формой и материалами."),
                    category("Семьи", "Поддержка семей в сложной жизненной ситуации."),
                    category("Срочная помощь", "Быстрые сборы на еду, жилье и базовые потребности.")
            ));
            log.info("Demo categories created");
        }

        List<Category> categories = categoryRepository.findAll();

        if (projectRepository.count() == 0) {
            Project medicine = project(
                    "Реабилитация после операции для Айлин",
                    "Айлин проходит восстановление после сложной операции. Средства нужны на курс реабилитации, лекарства и регулярные занятия со специалистами.",
                    admin,
                    categories.get(0),
                    1_800_000,
                    640_000,
                    "https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=1200&q=80"
            );

            Project education = project(
                    "Учебные наборы для школьников Алматы",
                    "Собираем рюкзаки, канцтовары и учебные материалы для детей из семей, которым сейчас особенно нужна поддержка.",
                    admin,
                    categories.get(1),
                    950_000,
                    380_000,
                    "https://images.unsplash.com/photo-1509062522246-3755977927d7?auto=format&fit=crop&w=1200&q=80"
            );

            Project family = project(
                    "Тёплая зима для многодетных семей",
                    "Помогаем семьям купить зимнюю одежду, продукты и оплатить самые важные бытовые расходы в холодный сезон.",
                    admin,
                    categories.get(2),
                    1_250_000,
                    710_000,
                    "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&w=1200&q=80"
            );

            projectRepository.saveAll(List.of(medicine, education, family));
            donationRepository.saveAll(List.of(
                    donation(120_000, donor, medicine),
                    donation(85_000, donor, medicine),
                    donation(60_000, donor, education),
                    donation(150_000, donor, family),
                    donation(45_000, donor, family)
            ));
            log.info("Demo projects and donations created");
        }

        if (volunteerEventRepository.count() == 0) {
            volunteerEventRepository.saveAll(List.of(
                    event(
                            "Сбор продуктовых корзин",
                            "Команда волонтёров собирает и сортирует продуктовые наборы для семей. Нужна помощь с упаковкой и доставкой.",
                            LocalDateTime.now().plusDays(7).withHour(11).withMinute(0),
                            "Алматы, ул. Абая 150",
                            admin
                    ),
                    event(
                            "Благотворительная ярмарка",
                            "Открытая встреча фонда, где можно познакомиться с проектами, передать вещи и поддержать текущие сборы.",
                            LocalDateTime.now().plusDays(15).withHour(13).withMinute(30),
                            "Алматы, Парк Первого Президента",
                            admin
                    )
            ));
            log.info("Demo volunteer events created");
        }
    }

    private Category category(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private Project project(
            String title,
            String description,
            User author,
            Category category,
            long targetAmount,
            long collectedAmount,
            String imageUrl
    ) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setAuthor(author);
        project.setCategory(category);
        project.setTargetAmount(targetAmount);
        project.setCollectedAmount(collectedAmount);
        project.setVerified(true);

        Image image = Image.builder()
                .relativePath(imageUrl)
                .project(project)
                .build();
        project.getImages().add(image);

        return project;
    }

    private Donation donation(long amount, User donor, Project project) {
        Donation donation = new Donation();
        donation.setAmount(amount);
        donation.setDonor(donor);
        donation.setProject(project);
        return donation;
    }

    private VolunteerEvent event(String title, String description, LocalDateTime date, String location, User organizer) {
        VolunteerEvent event = new VolunteerEvent();
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDate(date);
        event.setLocation(location);
        event.setOrganizer(organizer);
        return event;
    }
}
