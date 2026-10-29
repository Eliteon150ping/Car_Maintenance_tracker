package sia.sever.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.dto.car.CreateCarDTO;
import sia.sever.dto.car.MileageValidationDTO;
import sia.sever.dto.car.UpdateCarDTO;
import sia.sever.dto.user.LoginDTO;
import sia.sever.exception.InvalidClassException;
import sia.sever.exception.InvalidMileageException;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.exception.ValidationException;
import sia.sever.service.CarService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @Test
    void createCar_shouldReturnCreated() throws Exception {

        CarResponseDTO carResponseDTO = new CarResponseDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                "carImage"
        );

        when(carService.createCar(any(CreateCarDTO.class), isNull()))
                .thenReturn(carResponseDTO);

        mockMvc.perform(multipart("/api/my-cars")
                        .param("brand", "Toyota")
                        .param("model", "Corolla RXI")
                        .param("year", "2001")
                        .param("colour", "White")
                        .param("currentMileage", "150000"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.year").value(2001))
                .andExpect(jsonPath("$.colour").value("White"))
                .andExpect(jsonPath("$.currentMileage").value(150000))
                .andExpect(jsonPath("$.carImageUrl").value("carImage"));
    }

    @Test
    void createCar_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        mockMvc.perform(multipart("/api/my-cars")
                        .param("brand", "")
                        .param("model", "Corolla RXI")
                        .param("year", "2001")
                        .param("colour", "White")
                        .param("currentMileage", "150000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.brand").value("Brand is required"));

        verify(carService, never()).createCar(any(CreateCarDTO.class), isNull());
    }

    @Test
    void createCar_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        when(carService.createCar(any(CreateCarDTO.class), isNull()))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(post("/api/my-cars"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllCars_shouldReturnOk() throws Exception {

        CarResponseDTO carResponseDTO = new CarResponseDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                "carImage"
        );

        List<CarResponseDTO> carResponseDTOList = new ArrayList<>();
        carResponseDTOList.add(carResponseDTO);

        when(carService.getAllCars())
                .thenReturn(carResponseDTOList);

        mockMvc.perform(get("/api/my-cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].year").value(2001))
                .andExpect(jsonPath("$[0].colour").value("White"))
                .andExpect(jsonPath("$[0].currentMileage").value(150000))
                .andExpect(jsonPath("$[0].carImageUrl").value("carImage"));
    }

    @Test
    void getAllCars_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        doThrow(new RuntimeException("Something went wrong"))
                .when(carService).getAllCars();

        mockMvc.perform(get("/api/my-cars"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void searchCars_shouldReturnOk() throws Exception {

        CarResponseDTO carResponseDTO = new CarResponseDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                "carImage"
        );

        List<CarResponseDTO> carResponseDTOList = new ArrayList<>();
        carResponseDTOList.add(carResponseDTO);

        when(carService.searchCars("Toyota"))
                .thenReturn(carResponseDTOList);

        mockMvc.perform(get("/api/my-cars/search")
                        .param("search", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].year").value(2001))
                .andExpect(jsonPath("$[0].colour").value("White"))
                .andExpect(jsonPath("$[0].currentMileage").value(150000))
                .andExpect(jsonPath("$[0].carImageUrl").value("carImage"));
    }

    @Test
    void searchCars_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        when(carService.searchCars("Toyota"))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/my-cars/search")
                        .param("search", "Toyota"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCarById_shouldReturnOk() throws Exception {

        CarResponseDTO carResponseDTO = new CarResponseDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                "carImage"
        );

        when(carService.getCarById(1L))
                .thenReturn(carResponseDTO);

        mockMvc.perform(get("/api/my-cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.year").value(2001))
                .andExpect(jsonPath("$.colour").value("White"))
                .andExpect(jsonPath("$.currentMileage").value(150000))
                .andExpect(jsonPath("$.carImageUrl").value("carImage"));
    }

    @Test
    void getCarById_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {

        when(carService.getCarById(1L))
                .thenThrow(new ResourceNotFoundException("Car not found with ID: " + 1L));

        mockMvc.perform(get("/api/my-cars/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found with ID: " + 1L));

        verify(carService).getCarById(1L);
    }

    @Test
    void getCarById_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        when(carService.getCarById(1L))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/my-cars/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateCarById_shouldReturnOk() throws Exception {

        CarResponseDTO carResponseDTO = new CarResponseDTO(
                1L,
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                160000,
                "carImage"
        );

        when(carService.updateCar(eq(1L), any(UpdateCarDTO.class), isNull()))
                .thenReturn(carResponseDTO);

        mockMvc.perform(multipart("/api/my-cars/1")
                        .param("colour", "White")
                        .param("currentMileage", "160000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.year").value(2001))
                .andExpect(jsonPath("$.colour").value("White"))
                .andExpect(jsonPath("$.currentMileage").value(160000))
                .andExpect(jsonPath("$.carImageUrl").value("carImage"));
    }

    @Test
    void updateCarById_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        Map<String, String> errors = new HashMap<>();
        errors.put("currentMileage", "Updated mileage cannot be less than current mileage");

        when(carService.updateCar(eq(1L), any(UpdateCarDTO.class), isNull()))
                .thenThrow(new InvalidMileageException("Updated failed", errors));

        mockMvc.perform(multipart("/api/my-cars/1")
                        .param("colour", "White")
                        .param("currentMileage", "140000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Updated failed"))
                .andExpect(jsonPath("$.errors.currentMileage")
                        .value("Updated mileage cannot be less than current mileage"));

        verify(carService).updateCar(eq(1L), any(UpdateCarDTO.class), isNull());
    }

    @Test
    void updateCarById_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {

        when(carService.updateCar(eq(1L), any(UpdateCarDTO.class), isNull()))
                .thenThrow(new ResourceNotFoundException("Car not found with ID: " + 1L));

        mockMvc.perform(multipart("/api/my-cars/1")
                        .param("colour", "White")
                        .param("currentMileage", "140000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found with ID: " + 1L));

        verify(carService).updateCar(eq(1L), any(UpdateCarDTO.class), isNull());
    }

    @Test
    void updateCarById_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        when(carService.updateCar(eq(1L), any(UpdateCarDTO.class), isNull()))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(multipart("/api/my-cars/1")
                        .param("colour", "White")
                        .param("currentMileage", "160000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void validateMileage_shouldReturnNoContent() throws Exception {

        MileageValidationDTO mileageValidationDTO = new MileageValidationDTO(
                160000
        );

        mockMvc.perform(put("/api/my-cars/1/mileage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mileageValidationDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void validateMileage_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception{

        MileageValidationDTO mileageValidationDTO = new MileageValidationDTO(
                140000
        );

        Map<String, String> errors = new HashMap<>();
        errors.put("currentMileage", "Updated mileage cannot be less than current mileage");

        doThrow(new InvalidMileageException("Updated failed", errors))
                .when(carService).validateUpdateMileage(eq(1L), anyInt());

        mockMvc.perform(put("/api/my-cars/1/mileage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mileageValidationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Updated failed"))
                .andExpect(jsonPath("$.errors.currentMileage")
                        .value("Updated mileage cannot be less than current mileage"));

        verify(carService).validateUpdateMileage(eq(1L), anyInt());
    }

    @Test
    void validateMileage_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception{

        MileageValidationDTO mileageValidationDTO = new MileageValidationDTO(
                140000
        );

        doThrow(new RuntimeException("Something went wrong"))
                .when(carService).validateUpdateMileage(eq(1L), anyInt());

        mockMvc.perform(put("/api/my-cars/1/mileage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mileageValidationDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteCarById_shouldReturnNoContent() throws Exception{

        mockMvc.perform(delete("/api/my-cars/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCarById_shouldReturnNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception{

        doThrow(new ResourceNotFoundException("Car not found with ID: " + 1L))
                .when(carService).deleteCar(eq(1L));

        mockMvc.perform(delete("/api/my-cars/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found with ID: " + 1L));

        verify(carService).deleteCar(eq(1L));
    }

    @Test
    void deleteCarById_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception{

        doThrow(new RuntimeException("Something went wrong"))
                .when(carService).deleteCar(eq(1L));

        mockMvc.perform(delete("/api/my-cars/1"))
                .andExpect(status().isInternalServerError());
    }
}