package com.example.carsharingservice.controller;

import com.example.carsharingservice.dto.request.RentalRequestDto;
import com.example.carsharingservice.dto.response.RentalResponseDto;
import com.example.carsharingservice.mapper.DtoMapper;
import com.example.carsharingservice.model.Rental;
import com.example.carsharingservice.service.RentalService;
import com.example.carsharingservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/rentals")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Rental", description = "The Rental API. "
        + "Contains all the operations that can be performed on a customer/manager.")
public class RentalController {
    private final RentalService rentalService;
    private final UserService userService;
    private final DtoMapper <RentalRequestDto, RentalResponseDto, Rental> rentalMapper;

    @Operation(summary = "Add rental", description = "Add rental")
    @PostMapping
    public RentalResponseDto add(@RequestBody RentalRequestDto rentalRequestDto) {
        Rental createdRental = rentalService.save(rentalMapper.mapToModel(rentalRequestDto));
        //TODO implement telegram notification for createRental
        //User userFromDb = userService.get(createdRental.getUser().getId());
        // telegramNotificationService.sendMessage(String
//                .format(
//                        "New rental was created.\n"
//                                + "Rental info: %s\n"
//                                + "User info: %s\n"
//                                + "Car info: %s", rentalMapper.mapToDto(createdRental),
//                        userMapper.mapToDto(userFromDb),
//                        carService.get(createdRental.getCar().getId())), userFromDb);
        return rentalMapper.mapToDto(createdRental);
    }

    @Operation(summary = "Get rental by user and status",
            description = "Get rental by user and status")
    @GetMapping
    public List<RentalResponseDto> getRentalsByUserIdAndStatus(
            @RequestParam(name = "user_id") Long id,
            @RequestParam(name = "is_active") boolean isActive,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageRequest = PageRequest.of(page, count);
        return rentalService.findByUSerId(id, isActive)
                .stream()
                .map(rentalMapper::mapToDto)
                .toList();
    }

    @Operation(summary = "Get rental by rental id", description = "Get rental by rental id")
    @GetMapping("/{rentalId}")
    public RentalResponseDto get(@PathVariable Long rentalId) {
        return rentalMapper.mapToDto(rentalService.find(rentalId));
    }

    @Operation(summary = "Set actual return date ", description = "Set actual return date ")
    @PostMapping("/{id}/return")
    public void returnCar(@PathVariable Long id,
                                       @RequestBody RentalRequestDto rentalRequestDto) {
        rentalService
                .returnCar(id);

    }
}
