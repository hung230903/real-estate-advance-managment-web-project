package com.webapp.controller.admin.customer;

import com.webapp.enums.CustomerStatus;
import com.webapp.enums.UserRole;
import com.webapp.models.dtos.CustomerDTO;
import com.webapp.models.request.CustomerSearchRequest;
import com.webapp.services.CustomerService;
import com.webapp.services.UserService;
import com.webapp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.webapp.constant.SystemConstant.MAX_RESULT;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;
    private final com.webapp.services.TransactionService transactionService;

    @GetMapping("/list")
    public String customerList(@ModelAttribute CustomerSearchRequest searchRequest,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               Model model) {

        List<String> authorities = SecurityUtils.getAuthorities();
        if (authorities.contains(UserRole.ROLE_EMPLOYEE.name())) {
            Long staffId = Objects.requireNonNull(SecurityUtils.getPrincipal()).getId();
            searchRequest.setStaffId(staffId);
        }

        model.addAttribute("modelSearch", searchRequest);
        model.addAttribute("statuses", CustomerStatus.getCustomerStatus());

        if (authorities.contains(UserRole.ROLE_MANAGER.name())) {
            model.addAttribute("staffs", userService.getAllStaff());
        }

        model.addAttribute("model", customerService.getCustomers(searchRequest, page, MAX_RESULT, com.webapp.constant.SystemConstant.MAX_NAVIGATION_PAGE));

        return "admin/customer/customerList";
    }

    @GetMapping("/edit")
    public String createCustomer(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        model.addAttribute("statuses", CustomerStatus.getCustomerStatus());
        model.addAttribute("transactionTypes", com.webapp.enums.TransactionType.getTransactionTypes());
        model.addAttribute("transactions", new java.util.HashMap<>());
        return "admin/customer/customerEdit";
    }

    @GetMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, Model model) {
        CustomerDTO customerDTO = customerService.findById(id);
        model.addAttribute("customer", customerDTO);
        model.addAttribute("statuses", CustomerStatus.getCustomerStatus());
        model.addAttribute("transactionTypes", com.webapp.enums.TransactionType.getTransactionTypes());
        
        java.util.Map<String, List<com.webapp.models.dtos.TransactionDTO>> transactions = new java.util.HashMap<>();
        for (com.webapp.enums.TransactionType type : com.webapp.enums.TransactionType.values()) {
            transactions.put(type.name(), transactionService.findByCustomerIdAndCode(id, type.name()));
        }
        model.addAttribute("transactions", transactions);
        
        return "admin/customer/customerEdit";
    }
}
