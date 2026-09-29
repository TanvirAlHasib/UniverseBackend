package com.amr.uniVerse.auth.config;

import com.amr.uniVerse.auth.entity.Department;
import com.amr.uniVerse.auth.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            departmentRepository.save(Department.builder().name("Computer Science").code("CSE").build());
            departmentRepository.save(Department.builder().name("Business Administration").code("BBA").build());
            departmentRepository.save(Department.builder().name("Electrical Engineering").code("EEE").build());
        }
    }
}