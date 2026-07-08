package com.webapp.api.web;

import com.webapp.models.dtos.CustomerDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactAPI {

  private final CustomerService customerService;

  @PostMapping
  public ResponseEntity<ResponseDTO> receiveContact(@RequestBody CustomerDTO customerDTO) {
    ResponseDTO responseDTO = new ResponseDTO();
    try {
      customerDTO.setStatus("DANG_XU_LY");
      customerService.saveOrUpdate(customerDTO);
      responseDTO.setMessage("Message sent successfully!");
      return ResponseEntity.ok(responseDTO);
    } catch (Exception e) {
      responseDTO.setMessage("Failed to send message: " + e.getMessage());
      return ResponseEntity.internalServerError().body(responseDTO);
    }
  }
}
