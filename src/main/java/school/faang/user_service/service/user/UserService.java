package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.pojo.user.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final CountryService countryService;

    @Transactional(readOnly = true)
    public UserDto getUser(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND, userId)));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Long> getNotExistingUserIds(List<Long> userIds) {
        return userIds.isEmpty() ? Collections.emptyList() : userRepository.findNotExistingUserIds(userIds);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getNotPremiumUsers(UserFilterDto filterDto) {
        Stream<User> usersToFilter = userRepository.findAll().stream();
        Stream<User> notPremiumUsers = filterPremiumUsers(usersToFilter);

        List<UserDto> filteredUsers = filter(notPremiumUsers, filterDto);
        log.info("Got {} filtered users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> users = userRepository.findPremiumUsers();

        List<UserDto> filteredUsers = filter(users, filterDto);
        log.info("Got {} filtered premium users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    @Transactional
    public List<UserDto> parsePersonDataIntoUserDto(MultipartFile csvFile) {
        log.info("Starting to parse CSV file: {}", csvFile.getOriginalFilename());
        try {
            InputStream inputStream = csvFile.getInputStream();
            CsvMapper csvMapper = new CsvMapper();
            csvMapper.findAndRegisterModules();

            CsvSchema schema = csvMapper.schemaFor(Person.class).withHeader();
            MappingIterator<Person> iterator = csvMapper.readerFor(Person.class).with(schema).readValues(inputStream);
            List<Person> persons = iterator.readAll();
            log.info("CSV file processed. Number of records: {}", persons.size());

            userValidator.validateAllPersons(persons);

            return saveUsers(persons);
        } catch (IOException e) {
            log.error("Error processing CSV file", e);
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    private List<UserDto> saveUsers(List<Person> persons) {
        log.info("Starting to save {} users", persons.size());

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<User>> futures = persons.stream()
                .map(person -> CompletableFuture.supplyAsync(() -> convertToUser(person), executor))
                .toList();

        List<User> users = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException("Error processing user", e);
                    }
                })
                .collect(Collectors.toList());

        executor.shutdown();

        List<User> savedUsers = userRepository.saveAll(users);
        log.info("Successfully saved {} users to the database", savedUsers.size());

        return savedUsers.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private User convertToUser(Person person) {
        log.debug("Processing person: {}", person);
        User user = userMapper.toUser(person);
        user.setPassword(generatePassword());

        if (countryService.isCountryExistsByTitle(person.getCountry())) {
            user.setCountry(countryService.getCountryByTitle(person.getCountry()));
        } else {
            Country country = countryService.addNewCountry(person.getCountry());
            log.info("Added new country to DB: {}", person.getCountry());
            user.setCountry(country);
        }

        return user;
    }

    private String generatePassword() {
        String password = UUID.randomUUID().toString().substring(0, 8);
        log.debug("Generated password: {}", password); // Log generated password
        return password;
    }

    private List<UserDto> filter(Stream<User> usersStream, UserFilterDto filterDto) {
        return userMapper.entityStreamToDtoList(userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(usersStream,
                        (users, userFilter) -> userFilter.apply(users, filterDto),
                        (a, b) -> b));
    }

    private Stream<User> filterPremiumUsers(Stream<User> users) {
        return users.filter(user -> user.getPremium() == null
                || user.getPremium().getEndDate() == null
                || user.getPremium().getEndDate().isBefore(LocalDateTime.now()));
    }

}