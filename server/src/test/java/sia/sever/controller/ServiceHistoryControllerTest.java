package sia.sever.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sia.sever.dto.car.CarSummaryDTO;
import sia.sever.dto.car.UpdateCarDTO;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.LatestServiceInfoDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.dto.serviceRecord.UpdateServiceRecordDTO;
import sia.sever.exception.InvalidClassException;
import sia.sever.exception.InvalidMileageException;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.service.ServiceHistoryService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sia.sever.enums.ServiceType.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(ServiceHistoryController.class)
public class ServiceHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceHistoryService serviceHistoryService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @Test
    void createServiceRecord_shouldReturnCreated() throws Exception {

        CarSummaryDTO carSummaryDTO = new CarSummaryDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                150000
        );

        CreateServiceRecordDTO createServiceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter"
        );

        ServiceRecordResponseDTO serviceRecordResponseDTO = new ServiceRecordResponseDTO(
                1L,
                LocalDate.of(2026, 1, 2),
                150000,
                160000,
                LocalDate.of(2026, 6, 2),
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter",
                10000,
                182,
                carSummaryDTO,
                true
        );

        when(serviceHistoryService.createServiceHistory(any(CreateServiceRecordDTO.class), eq(1L)))
                .thenReturn(serviceRecordResponseDTO);

        mockMvc.perform(post("/api/service-records/car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceRecordDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$.mileageAtService").value(150000))
                .andExpect(jsonPath("$.nextDueMileage").value(160000))
                .andExpect(jsonPath("$.nextDueDate").value("2026-06-02"))
                .andExpect(jsonPath("$.serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.cost").value(400))
                .andExpect(jsonPath("$.description").value("Changed oil and filter"))
                .andExpect(jsonPath("$.remainingKm").value(10000))
                .andExpect(jsonPath("$.remainingDays").value(182))
                .andExpect(jsonPath("$.car.id").value(1))
                .andExpect(jsonPath("$.car.brand").value("Toyota"))
                .andExpect(jsonPath("$.car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.car.year").value(2001))
                .andExpect(jsonPath("$.car.currentMileage").value(150000))
                .andExpect(jsonPath("$.latestRecord").value(true));
    }

    @Test
    void createServiceRecord_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        CreateServiceRecordDTO createServiceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter"
        );

        Map<String, String> errors = new HashMap<>();
        errors.put("mileageAtService", "New service mileage cannot be lower than the last latest service mileage");

        when(serviceHistoryService.createServiceHistory(any(CreateServiceRecordDTO.class), eq(1L)))
                .thenThrow(new InvalidMileageException("Error", errors));

        mockMvc.perform(post("/api/service-records/car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceRecordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.errors.mileageAtService").value("New service mileage cannot be lower than the last latest service mileage"));
    }

    @Test
    void createServiceRecord_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        CreateServiceRecordDTO createServiceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2028, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter"
        );

        mockMvc.perform(post("/api/service-records/car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceRecordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.serviceDate").value("Service date cannot be in the future."));

        verify(serviceHistoryService, never()).createServiceHistory(any(CreateServiceRecordDTO.class), eq(1L));
    }

    @Test
    void createServiceRecord_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {

        CreateServiceRecordDTO createServiceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter"
        );

        when(serviceHistoryService.createServiceHistory(any(CreateServiceRecordDTO.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException("Car not found"));

        mockMvc.perform(post("/api/service-records/car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceRecordDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found"));

        verify(serviceHistoryService, times(1))
                .createServiceHistory(any(CreateServiceRecordDTO.class), eq(1L));
    }

    @Test
    void createServiceRecord_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        CreateServiceRecordDTO createServiceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter"
        );

        when(serviceHistoryService.createServiceHistory(any(CreateServiceRecordDTO.class), eq(1L)))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(post("/api/service-records/car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createServiceRecordDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void validateDuplicateRecord_shouldReturnOk() throws Exception {

        mockMvc.perform(post("/api/service-records/car/1/validate-duplicate")
                        .param("serviceType", "ENGINE_OIL_AND_FILTER")
                        .param("serviceDate", "2026-01-02")
                        .param("mileageAtService", "150000"))
                .andExpect(status().isOk());
    }

    @Test
    void validateDuplicateRecord_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        doThrow(new InvalidClassException(
                "Cannot add duplicate service record for the same service type with same mileage and date"
        )).when(serviceHistoryService).validateDuplicateRecord(
                eq(1L),
                eq(ENGINE_OIL_AND_FILTER),
                eq(LocalDate.of(2026, 1, 2)),
                eq(150000)
        );

        mockMvc.perform(post("/api/service-records/car/1/validate-duplicate")
                        .param("serviceType", "ENGINE_OIL_AND_FILTER")
                        .param("serviceDate", "2026-01-02")
                        .param("mileageAtService", "150000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot add duplicate service record for the same service type with same mileage and date"));

        verify(serviceHistoryService, times(1)).validateDuplicateRecord(
                eq(1L),
                eq(ENGINE_OIL_AND_FILTER),
                eq(LocalDate.of(2026, 1, 2)),
                eq(150000)
        );
    }

    @Test
    void validateDuplicateRecord_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {

        doThrow(new ResourceNotFoundException("Car not found"))
                .when(serviceHistoryService).validateDuplicateRecord(
                        eq(1L),
                        eq(ENGINE_OIL_AND_FILTER),
                        eq(LocalDate.of(2026, 1, 2)),
                        eq(150000)
                );

        mockMvc.perform(post("/api/service-records/car/1/validate-duplicate")
                        .param("serviceType", "ENGINE_OIL_AND_FILTER")
                        .param("serviceDate", "2026-01-02")
                        .param("mileageAtService", "150000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found"));

        verify(serviceHistoryService, times(1)).validateDuplicateRecord(
                eq(1L),
                eq(ENGINE_OIL_AND_FILTER),
                eq(LocalDate.of(2026, 1, 2)),
                eq(150000)
        );
    }

    @Test
    void validateDuplicateRecord_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        doThrow(new RuntimeException("Something went wrong"))
                .when(serviceHistoryService).validateDuplicateRecord(
                        eq(1L),
                        eq(ENGINE_OIL_AND_FILTER),
                        eq(LocalDate.of(2026, 1, 2)),
                        eq(150000)
                );

        mockMvc.perform(post("/api/service-records/car/1/validate-duplicate")
                        .param("serviceType", "ENGINE_OIL_AND_FILTER")
                        .param("serviceDate", "2026-01-02")
                        .param("mileageAtService", "150000"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(serviceHistoryService, times(1)).validateDuplicateRecord(
                eq(1L),
                eq(ENGINE_OIL_AND_FILTER),
                eq(LocalDate.of(2026, 1, 2)),
                eq(150000)
        );
    }

    @Test
    void getAllServiceRecords_shouldReturnOk() throws Exception {

        CarSummaryDTO carSummaryDTO = new CarSummaryDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                150000
        );

        ServiceRecordResponseDTO serviceRecordResponseDTO = new ServiceRecordResponseDTO(
                1L,
                LocalDate.of(2026, 1, 2),
                150000,
                160000,
                LocalDate.of(2026, 6, 2),
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter",
                10000,
                182,
                carSummaryDTO,
                true
        );

        List<ServiceRecordResponseDTO> serviceRecordResponseDTOList = new ArrayList<>();
        serviceRecordResponseDTOList.add(serviceRecordResponseDTO);

        when(serviceHistoryService.getAllServiceRecords())
                .thenReturn(serviceRecordResponseDTOList);

        mockMvc.perform(get("/api/service-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$[0].nextDueDate").value("2026-06-02"))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(400))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$[0].remainingKm").value(10000))
                .andExpect(jsonPath("$[0].remainingDays").value(182))
                .andExpect(jsonPath("$[0].car.id").value(1))
                .andExpect(jsonPath("$[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].car.year").value(2001))
                .andExpect(jsonPath("$[0].car.currentMileage").value(150000))
                .andExpect(jsonPath("$[0].latestRecord").value(true));

        verify(serviceHistoryService, times(1)).getAllServiceRecords();
    }

    @Test
    void getAllServiceRecords_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        doThrow(new RuntimeException("Something went wrong"))
                .when(serviceHistoryService).getAllServiceRecords();

        mockMvc.perform(get("/api/service-records"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(serviceHistoryService, times(1)).getAllServiceRecords();
    }

    @Test
    void updateServiceRecord_shouldReturnOk() throws Exception {

        CarSummaryDTO carSummaryDTO = new CarSummaryDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                150000
        );

        UpdateServiceRecordDTO updateServiceRecordDTO = new UpdateServiceRecordDTO(
                "Changed oil and filter updated",
                450.0
        );

        ServiceRecordResponseDTO serviceRecordResponseDTO = new ServiceRecordResponseDTO(
                1L,
                LocalDate.of(2026, 1, 2),
                150000,
                160000,
                LocalDate.of(2026, 6, 2),
                ENGINE_OIL_AND_FILTER,
                450.0,
                "Changed oil and filter updated",
                10000,
                182,
                carSummaryDTO,
                true
        );

        when(serviceHistoryService
                .updateServiceHistory(
                        eq(1L),
                        any(UpdateServiceRecordDTO.class),
                        eq(1L))

        ).thenReturn(serviceRecordResponseDTO);

        mockMvc.perform(put("/api/service-records/car/1/service/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$.mileageAtService").value(150000))
                .andExpect(jsonPath("$.nextDueMileage").value(160000))
                .andExpect(jsonPath("$.nextDueDate").value("2026-06-02"))
                .andExpect(jsonPath("$.serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.cost").value(450.0))
                .andExpect(jsonPath("$.description").value("Changed oil and filter updated"))
                .andExpect(jsonPath("$.remainingKm").value(10000))
                .andExpect(jsonPath("$.remainingDays").value(182))
                .andExpect(jsonPath("$.car.id").value(1))
                .andExpect(jsonPath("$.car.brand").value("Toyota"))
                .andExpect(jsonPath("$.car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.car.year").value(2001))
                .andExpect(jsonPath("$.car.currentMileage").value(150000))
                .andExpect(jsonPath("$.latestRecord").value(true));

        verify(serviceHistoryService, times(1)).updateServiceHistory(
                eq(1L),
                any(UpdateServiceRecordDTO.class),
                eq(1L)
        );
    }

    @Test
    void updateServiceRecord_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        UpdateServiceRecordDTO updateServiceRecordDTO = new UpdateServiceRecordDTO(
                "Changed oil and filter",
                450.0
        );

        when(serviceHistoryService.updateServiceHistory(eq(1L), any(UpdateServiceRecordDTO.class), eq(1L)))
                .thenThrow(new InvalidClassException("Description cannot be empty"));

        mockMvc.perform(put("/api/service-records/car/1/service/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRecordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Description cannot be empty"));

        verify(serviceHistoryService, times(1)).updateServiceHistory(
                eq(1L),
                any(UpdateServiceRecordDTO.class),
                eq(1L)
        );
    }

    @Test
    void updateServiceRecord_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        UpdateServiceRecordDTO updateServiceRecordDTO = new UpdateServiceRecordDTO(
                "",
                0.0
        );

        mockMvc.perform(put("/api/service-records/car/1/service/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRecordDTO)))
                .andExpect(status().isBadRequest());

        verify(serviceHistoryService, never()).updateServiceHistory(
                anyLong(),
                any(UpdateServiceRecordDTO.class),
                anyLong()
        );
    }

    @Test
    void updateServiceRecord_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {

        UpdateServiceRecordDTO updateServiceRecordDTO = new UpdateServiceRecordDTO(
                "Updated",
                100.0
        );

        when(serviceHistoryService.updateServiceHistory(eq(1L), any(UpdateServiceRecordDTO.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException("Service not found for this car"));

        mockMvc.perform(put("/api/service-records/car/1/service/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRecordDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Service not found for this car"));

        verify(serviceHistoryService, times(1)).updateServiceHistory(
                eq(1L),
                any(UpdateServiceRecordDTO.class),
                eq(1L)
        );
    }

    @Test
    void updateServiceRecord_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        UpdateServiceRecordDTO updateServiceRecordDTO = new UpdateServiceRecordDTO(
                "Updated",
                100.0
        );

        when(serviceHistoryService.updateServiceHistory(eq(1L), any(UpdateServiceRecordDTO.class), eq(1L)))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(put("/api/service-records/car/1/service/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRecordDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(serviceHistoryService, times(1)).updateServiceHistory(
                eq(1L),
                any(UpdateServiceRecordDTO.class),
                eq(1L)
        );
    }

    @Test
    void getServiceHistoryByCar_shouldReturnOk() throws Exception{

        CarSummaryDTO carSummaryDTO = new CarSummaryDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                150000
        );

        ServiceRecordResponseDTO serviceRecordResponseDTO = new ServiceRecordResponseDTO(
                1L,
                LocalDate.of(2026, 1, 2),
                150000,
                160000,
                LocalDate.of(2026, 6, 2),
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter",
                10000,
                182,
                carSummaryDTO,
                true
        );

        ServiceRecordResponseDTO serviceRecordResponseDTO2 = new ServiceRecordResponseDTO(
                2L,
                LocalDate.of(2026, 1, 2),
                150000,
                160000,
                LocalDate.of(2026, 6, 2),
                AIR_FILTER,
                400.0,
                "Changed Air filter",
                15000,
                364,
                carSummaryDTO,
                true
        );

        List<ServiceRecordResponseDTO> serviceRecordResponseDTOList =new ArrayList<>();
        serviceRecordResponseDTOList.add(serviceRecordResponseDTO);
        serviceRecordResponseDTOList.add(serviceRecordResponseDTO2);

        when(serviceHistoryService.getServiceHistoryByCar(eq(1L)))
                .thenReturn(serviceRecordResponseDTOList);

        mockMvc.perform(get("/api/service-records/car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$[0].nextDueDate").value("2026-06-02"))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(400))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$[0].remainingKm").value(10000))
                .andExpect(jsonPath("$[0].remainingDays").value(182))
                .andExpect(jsonPath("$[0].car.id").value(1))
                .andExpect(jsonPath("$[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].car.year").value(2001))
                .andExpect(jsonPath("$[0].car.currentMileage").value(150000))
                .andExpect(jsonPath("$[0].latestRecord").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$[1].mileageAtService").value(150000))
                .andExpect(jsonPath("$[1].nextDueMileage").value(160000))
                .andExpect(jsonPath("$[1].nextDueDate").value("2026-06-02"))
                .andExpect(jsonPath("$[1].serviceType").value("AIR_FILTER"))
                .andExpect(jsonPath("$[1].cost").value(400))
                .andExpect(jsonPath("$[1].description").value("Changed Air filter"))
                .andExpect(jsonPath("$[1].remainingKm").value(15000))
                .andExpect(jsonPath("$[1].remainingDays").value(364))
                .andExpect(jsonPath("$[1].car.id").value(1))
                .andExpect(jsonPath("$[1].car.brand").value("Toyota"))
                .andExpect(jsonPath("$[1].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$[1].car.year").value(2001))
                .andExpect(jsonPath("$[1].car.currentMileage").value(150000))
                .andExpect(jsonPath("$[1].latestRecord").value(true));

        verify(serviceHistoryService, times(1)).getServiceHistoryByCar(eq(1L));
    }

    @Test
    void getServiceHistoryByCar_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception{

        when(serviceHistoryService.getServiceHistoryByCar(eq(1L)))
                .thenThrow(new ResourceNotFoundException("Car not found"));

        mockMvc.perform(get("/api/service-records/car/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found"));

        verify(serviceHistoryService, times(1)).getServiceHistoryByCar(
                eq(1L)
        );
    }

    @Test
    void getServiceHistoryByCar_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception{

        when(serviceHistoryService.getServiceHistoryByCar(eq(1L)))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/service-records/car/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(serviceHistoryService, times(1)).getServiceHistoryByCar(
                eq(1L)
        );
    }

    @Test
    void getLatestServiceInfo_shouldReturnOk() throws Exception{

        LatestServiceInfoDTO latestServiceInfoDTO = new LatestServiceInfoDTO(
                150000,
                LocalDate.of(2026, 1, 2)
        );

        when(serviceHistoryService.getLatestServiceInfo(eq(1L)))
                .thenReturn(latestServiceInfoDTO);

        mockMvc.perform(get("/api/service-records/car/1/latest-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestServiceMileage").value(150000))
                .andExpect(jsonPath("$.latestServiceDate").value("2026-01-02"));

        verify(serviceHistoryService, times(1)).getLatestServiceInfo(
                eq(1L)
        );
    }

    @Test
    void getLatestServiceInfo_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception{

        when(serviceHistoryService.getLatestServiceInfo(eq(1L)))
                .thenThrow(new ResourceNotFoundException("Car not found"));

        mockMvc.perform(get("/api/service-records/car/1/latest-info"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found"));

        verify(serviceHistoryService, times(1)).getLatestServiceInfo(
                eq(1L)
        );
    }

    @Test
    void getLatestServiceInfo_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception{

        when(serviceHistoryService.getLatestServiceInfo(eq(1L)))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/service-records/car/1/latest-info"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(serviceHistoryService, times(1)).getLatestServiceInfo(
                eq(1L)
        );
    }

    @Test
    void getUpcomingServiceRecords_shouldReturnOk() throws Exception{

        CarSummaryDTO carSummaryDTO = new CarSummaryDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                150000
        );

        ServiceRecordResponseDTO serviceRecordResponseDTO = new ServiceRecordResponseDTO(
                1L,
                LocalDate.of(2026, 1, 2),
                150000,
                160000,
                LocalDate.of(2026, 6, 2),
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter",
                900,
                182,
                carSummaryDTO,
                true
        );

        List<ServiceRecordResponseDTO> serviceRecordResponseDTOList =new ArrayList<>();
        serviceRecordResponseDTOList.add(serviceRecordResponseDTO);

        when(serviceHistoryService.getUpcomingServiceRecords())
                .thenReturn(serviceRecordResponseDTOList);

        mockMvc.perform(get("/api/service-records/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$[0].nextDueDate").value("2026-06-02"))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(400))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$[0].remainingKm").value(900))
                .andExpect(jsonPath("$[0].remainingDays").value(182))
                .andExpect(jsonPath("$[0].car.id").value(1))
                .andExpect(jsonPath("$[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].car.year").value(2001))
                .andExpect(jsonPath("$[0].car.currentMileage").value(150000))
                .andExpect(jsonPath("$[0].latestRecord").value(true));

        verify(serviceHistoryService, times(1)).getUpcomingServiceRecords();
    }

    @Test
    void getUpcomingServiceRecords_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception{

        when(serviceHistoryService.getUpcomingServiceRecords())
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/service-records/upcoming"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(serviceHistoryService, times(1)).getUpcomingServiceRecords();
    }

    @Test
    void getOverdueServiceRecords_shouldReturnOk() throws Exception{

        CarSummaryDTO carSummaryDTO = new CarSummaryDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                150000
        );

        ServiceRecordResponseDTO serviceRecordResponseDTO = new ServiceRecordResponseDTO(
                1L,
                LocalDate.of(2026, 1, 2),
                150000,
                160000,
                LocalDate.of(2026, 6, 2),
                ENGINE_OIL_AND_FILTER,
                400.0,
                "Changed oil and filter",
                -120,
                182,
                carSummaryDTO,
                true
        );

        List<ServiceRecordResponseDTO> serviceRecordResponseDTOList =new ArrayList<>();
        serviceRecordResponseDTOList.add(serviceRecordResponseDTO);

        when(serviceHistoryService.getOverdueServiceRecords())
                .thenReturn(serviceRecordResponseDTOList);

        mockMvc.perform(get("/api/service-records/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$[0].nextDueDate").value("2026-06-02"))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(400))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$[0].remainingKm").value(-120))
                .andExpect(jsonPath("$[0].remainingDays").value(182))
                .andExpect(jsonPath("$[0].car.id").value(1))
                .andExpect(jsonPath("$[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].car.year").value(2001))
                .andExpect(jsonPath("$[0].car.currentMileage").value(150000))
                .andExpect(jsonPath("$[0].latestRecord").value(true));

        verify(serviceHistoryService, times(1)).getOverdueServiceRecords();
    }

    @Test
    void getOverdueServiceRecords_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception{

        when(serviceHistoryService.getOverdueServiceRecords())
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/service-records/overdue"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));

        verify(serviceHistoryService, times(1)).getOverdueServiceRecords();
    }
}

//ServiceHistoryControllerTest checklist

//Paginated endpoints
//Get service history by car — paginated
//- [ ] getServiceHistoryByCarPaginated_shouldReturnOk
//- [ ] getServiceHistoryByCarPaginated_shouldReturnBadRequestWhenServiceThrowsValidationException
//- [ ] getServiceHistoryByCarPaginated_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException
//- [ ] getServiceHistoryByCarPaginated_shouldReturnInternalServerErrorWhenServiceThrowsException

//Get all service records — paginated
//- [ ] getAllServiceRecordsPaginated_shouldReturnOk
//- [ ] getAllServiceRecordsPaginated_shouldReturnInternalServerErrorWhenServiceThrowsException

//Get upcoming service records — paginated
//- [ ] getUpcomingServiceRecordsPaginated_shouldReturnOk
//- [ ] getUpcomingServiceRecordsPaginated_shouldReturnInternalServerErrorWhenServiceThrowsException

//Get overdue service records — paginated
//- [ ] getOverdueServiceRecordsPaginated_shouldReturnOk
//- [ ] getOverdueServiceRecordsPaginated_shouldReturnInternalServerErrorWhenServiceThrowsException