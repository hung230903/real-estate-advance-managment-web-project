package com.webapp.api.admin;

import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.dtos.TransactionDTO;
import com.webapp.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/transactions")
@RequiredArgsConstructor
public class TransactionAPI {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody TransactionDTO transactionDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            transactionService.save(transactionDTO);
            responseDTO.setMessage("Transaction saved successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            TransactionDTO dto = transactionService.findById(id);
            responseDTO.setData(dto.getNote());
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            transactionService.delete(id);
            responseDTO.setMessage("Transaction deleted successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
}
