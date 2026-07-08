package com.webapp.api.admin;

import com.webapp.models.dtos.AssignmentCustomerDTO;
import com.webapp.models.dtos.CustomerDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/customers")
@Slf4j
@RequiredArgsConstructor
public class CustomerAPI {

  private final CustomerService customerService;

  @PostMapping
  public ResponseEntity<ResponseDTO> saveOrUpdate(@RequestBody CustomerDTO customerDTO) {
    ResponseDTO responseDTO = new ResponseDTO();
    try {
      customerService.saveOrUpdate(customerDTO);
      responseDTO.setMessage("Successfully saved/updated customer");
      return ResponseEntity.ok(responseDTO);
    } catch (Exception e) {
      responseDTO.setMessage(e.getMessage());
      return ResponseEntity.internalServerError().body(responseDTO);
    }
  }

  @DeleteMapping("/{ids}")
  public ResponseEntity<ResponseDTO> deleteCustomers(@PathVariable List<Long> ids) {
    ResponseDTO responseDTO = new ResponseDTO();
    try {
      customerService.deleteCustomers(ids);
      responseDTO.setMessage("Successfully deleted customers");
      return ResponseEntity.ok(responseDTO);
    } catch (Exception e) {
      responseDTO.setMessage(e.getMessage());
      return ResponseEntity.internalServerError().body(responseDTO);
    }
  }

  @GetMapping("/{id}/staff")
  public ResponseEntity<ResponseDTO> getStaff(@PathVariable("id") Long customerId) {
    return ResponseEntity.ok(customerService.loadStaffsByCustomerId(customerId));
  }

  @PutMapping("/assign")
  public ResponseEntity<ResponseDTO> assignStaff(@RequestBody AssignmentCustomerDTO assignmentCustomerDTO) {
    customerService.updateAssignmentCustomer(assignmentCustomerDTO);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setMessage("Assigned staff successfully");
    return ResponseEntity.ok(responseDTO);
  }
}
