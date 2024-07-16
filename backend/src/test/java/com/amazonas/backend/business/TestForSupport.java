import com.amazonas.common.dtos.PaymentInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TestForSupport {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RealPayment realPayment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandshakeSuccess() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        boolean result = realPayment.handshake();

        assertTrue(result);
    }

    @Test
    void testHandshakeNetworkError() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        boolean result = realPayment.handshake();

        assertFalse(result);
    }

    @Test
    void testHandshakeHttpClientError() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        boolean result = realPayment.handshake();

        assertFalse(result);
    }

    @Test
    void testHandshakeHttpServerError() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        boolean result = realPayment.handshake();

        assertFalse(result);
    }

    @Test
    void testPaymentSuccess() {
        PaymentInfoDto paymentInfo = new PaymentInfoDto();
        // Initialize paymentInfo with valid data

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("12345", HttpStatus.OK));

        int result = realPayment.payment(paymentInfo, 100.0);

        assertEquals(12345, result);
    }

    @Test
    void testPaymentNetworkError() {
        PaymentInfoDto paymentInfo = new PaymentInfoDto();
        // Initialize paymentInfo with valid data

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        int result = realPayment.payment(paymentInfo, 100.0);

        assertEquals(-1, result);
    }

    // Add more tests for different error scenarios in payment and cancel_pay methods
}
