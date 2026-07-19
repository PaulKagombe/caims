package com.countyassembly.caims.department;

import com.countyassembly.caims.common.entity.DuplicateResourceException;
import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public Department create(Department department) {

        String name = department.getName().trim();

        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A department named '" + name + "' already exists.");
        }

        department.setName(name);

        if (department.getActive() == null) {
            department.setActive(true);
        }

        return departmentRepository.save(department);
    }

    public Department update(Long id, Department incoming) {

        Department existing = findById(id);

        String name = incoming.getName().trim();

        if (!existing.getName().equalsIgnoreCase(name)
                && departmentRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A department named '" + name + "' already exists.");
        }

        existing.setName(name);
        existing.setDescription(incoming.getDescription());
        existing.setActive(incoming.getActive());

        return departmentRepository.save(existing);
    }

    public void delete(Long id) {

        Department department = findById(id);
        department.setActive(false);
        departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public Department findById(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findByActiveTrueOrderByNameAsc();
    }
}