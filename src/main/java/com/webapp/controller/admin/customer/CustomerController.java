package com.webapp.controller.admin.customer;

import com.webapp.constant.SystemConstant;
import com.webapp.entities.UserEntity;
import com.webapp.enums.CustomerStatus;
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

    @GetMapping("/list")
    public String customerList(@ModelAttribute CustomerSearchRequest searchRequest,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               Model model) {

        List<String> authorities = SecurityUtils.getAuthorities();
        if (authorities.contains(SystemConstant.STAFF_ROLE)) {
            Long staffId = Objects.requireNonNull(SecurityUtils.getPrincipal()).getId();
            searchRequest.setStaffId(staffId);
        }

        model.addAttribute("modelSearch", searchRequest);
        model.addAttribute("statuses", CustomerStatus.getCustomerStatus());

        if (authorities.contains(SystemConstant.MANAGER_ROLE)) {
            model.addAttribute("staffs", userService.getAllStaff());
        }

        model.addAttribute("model", customerService.getCustomers(searchRequest, page, MAX_RESULT, SystemConstant.MAX_NAVIGATION_PAGE));
        model.addAttribute("transactionTypes", com.webapp.enums.TransactionType.getTransactionTypes());

        return "admin/customer/customerList";
    }

    @GetMapping("/edit")
    public String createCustomer(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        model.addAttribute("statuses", CustomerStatus.getCustomerStatus());
        return "admin/customer/customerEdit";
    }

    @GetMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, Model model) {
        if (SecurityUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
            UserEntity userEntity = userService.getUserByUserName(Objects.requireNonNull(SecurityUtils.getPrincipal()).getUsername());
            if (userEntity.getCustomerEntities().stream().noneMatch(c -> c.getId().equals(id))) {
                return "redirect:/403";
            }
        }
        CustomerDTO customerDTO = customerService.findById(id);
        model.addAttribute("customer", customerDTO);
        model.addAttribute("statuses", CustomerStatus.getCustomerStatus());
        return "admin/customer/customerEdit";
    }
}
