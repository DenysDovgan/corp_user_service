package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validation.premium.PremiumValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @Mock
    private PaymentServiceClient paymentService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    private PremiumValidator premiumValidator;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    void buyPremium_ValidArgs() {
        User user = getUser();
        PremiumPeriod period = PremiumPeriod.THREE_MONTH;
        PremiumDto expected = getPremiumDto();
        when(paymentService.sendPayment(any(PaymentRequest.class))).thenReturn(getResponse());
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(premiumRepository.save(any(Premium.class))).thenReturn(getPremium());

        PremiumDto actual = premiumService.buyPremium(user.getId(), period);

        assertEquals(expected, actual);
        verify(premiumValidator, times(1)).validateUserPremiumStatus(anyLong());
        verify(premiumValidator, times(1)).validatePaymentResponse(any(PaymentResponse.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(premiumRepository, times(1)).save(any(Premium.class));
        verify(premiumMapper, times(1)).toDto(any(Premium.class));
        verify(paymentService, times(1)).sendPayment(any(PaymentRequest.class));
    }

    @Test
    void deleteExpiredPremiums() {
        List<Premium> expected = List.of(getBeforePremium());
        when(userRepository.findPremiumUsers()).thenReturn(getPremiumUsers());

        premiumService.deleteExpiredPremiums();

        verify(userRepository, times(1)).findPremiumUsers();
        verify(premiumRepository, times(1)).deleteAll(expected);
    }

    private Stream<User> getPremiumUsers() {
        return Stream.of(User.builder()
                        .premium(getBeforePremium())
                        .build(),
                User.builder()
                        .premium(getAfterPremium())
                        .build());
    }

    private Premium getAfterPremium() {
        return Premium.builder()
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    private Premium getBeforePremium() {
        return Premium.builder()
                .endDate(LocalDateTime.of(1999, 1, 29, 0, 0))
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .build();
    }

    private PremiumDto getPremiumDto() {
        return PremiumDto.builder()
                .id(1L)
                .userId(getUser().getId())
                .build();
    }

    private Premium getPremium() {
        return Premium.builder()
                .id(1L)
                .user(getUser())
                .build();
    }

    private PaymentResponse getResponse() {
        return PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .build();
    }
}
