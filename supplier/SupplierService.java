package com.countyassembly.caims.supplier;

import com.countyassembly.caims.common.exception.DuplicateResourceException;
import com.countyassembly.caims.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Supplier create(Supplier supplier) {

        String name = supplier.getName().trim();

        if (supplierRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A supplier named '" + name + "' already exists.");
        }

        supplier.setName(name);

        if (supplier.getActive() == null) {
            supplier.setActive(true);
        }

        return supplierRepository.save(supplier);
    }

    public Supplier update(Long id, Supplier incoming) {

        Supplier existing = findById(id);

        String name = incoming.getName().trim();

        if (!existing.getName().equalsIgnoreCase(name)
                && supplierRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A supplier named '" + name + "' already exists.");
        }

        existing.setName(name);
        existing.setContactPerson(incoming.getContactPerson());
        existing.setPhoneNumber(incoming.getPhoneNumber());
        existing.setEmail(incoming.getEmail());
        existing.setAddress(incoming.getAddress());
        existing.setActive(incoming.getActive());

        return supplierRepository.save(existing);
    }

    public void delete(Long id) {

        Supplier supplier = findById(id);
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public Supplier findById(Long id) {

        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Supplier> findAll() {
        return supplierRepository.findByActiveTrueOrderByNameAsc();
    }
}
