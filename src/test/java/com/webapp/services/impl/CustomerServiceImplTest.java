package com.webapp.services.impl;

import com.webapp.constant.SystemConstant;
import com.webapp.converter.CustomerConverter;
import com.webapp.converter.UserConverter;
import com.webapp.entities.CustomerEntity;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.AssignmentCustomerDTO;
import com.webapp.models.dtos.CustomerDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.dtos.StaffResponseDTO;
import com.webapp.models.request.CustomerSearchRequest;
import com.webapp.pagination.PaginationResult;
import com.webapp.repositories.CustomerRepository;
import com.webapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl Unit Tests")
class CustomerServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerConverter customerConverter;

  @Mock
  private UserConverter userConverter;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomerServiceImpl customerService;

  private CustomerEntity customerEntity;
  private CustomerDTO customerDTO;

  @BeforeEach
  void setUp() {
    customerEntity = new CustomerEntity();
    customerEntity.setId(1L);
    customerEntity.setFullName("Nguyen Van A");
    customerEntity.setPhone("0123456789");
    customerEntity.setEmail("test@example.com");
    customerEntity.setStatus("DANG_XU_LY");
    customerEntity.setIsActive(1);

    customerDTO = new CustomerDTO();
    customerDTO.setId(1L);
    customerDTO.setFullName("Nguyen Van A");
    customerDTO.setPhone("0123456789");
    customerDTO.setEmail("test@example.com");
    customerDTO.setStatus("DANG_XU_LY");
  }

  @Nested
  @DisplayName("getCustomers()")
  class GetCustomersTests {

    @Test
    @DisplayName("Should return paginated customer list")
    void getCustomers_withResults_returnsPaginatedResult() {
      CustomerSearchRequest searchRequest = new CustomerSearchRequest();
      searchRequest.setFullName("Nguyen");

      when(customerRepository.searchCustomers(searchRequest, 1, 3)).thenReturn(List.of(customerEntity));
      when(customerRepository.countAll(searchRequest)).thenReturn(1);
      when(customerConverter.toCustomerDTO(customerEntity)).thenReturn(customerDTO);

      PaginationResult<CustomerDTO> result = customerService.getCustomers(searchRequest, 1, 3, 3);

      assertThat(result).isNotNull();
      assertThat(result.getEntityList()).hasSize(1);
      assertThat(result.getEntityList().get(0).getFullName()).isEqualTo("Nguyen Van A");
      assertThat(result.getTotalRecords()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return empty list when no customers found")
    void getCustomers_noResults_returnsEmptyList() {
      CustomerSearchRequest searchRequest = new CustomerSearchRequest();
      when(customerRepository.searchCustomers(searchRequest, 1, 3)).thenReturn(Collections.emptyList());
      when(customerRepository.countAll(searchRequest)).thenReturn(0);

      PaginationResult<CustomerDTO> result = customerService.getCustomers(searchRequest, 1, 3, 3);

      assertThat(result.getEntityList()).isEmpty();
      assertThat(result.getTotalRecords()).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("Should return CustomerDTO when customer exists")
    void findById_existingId_returnsCustomerDTO() {
      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      when(customerConverter.toCustomerDTO(customerEntity)).thenReturn(customerDTO);

      CustomerDTO result = customerService.findById(1L);

      assertThat(result).isNotNull();
      assertThat(result.getFullName()).isEqualTo("Nguyen Van A");
      assertThat(result.getPhone()).isEqualTo("0123456789");
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void findById_nonExistingId_throwsRuntimeException() {
      when(customerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> customerService.findById(999L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Customer not found");
    }
  }

  @Nested
  @DisplayName("saveOrUpdate()")
  class SaveOrUpdateTests {

    @Test
    @DisplayName("Should create new customer when ID is null")
    void saveOrUpdate_newCustomer_createsEntity() {
      CustomerDTO newDTO = new CustomerDTO();
      newDTO.setFullName("New Customer");
      newDTO.setPhone("0111222333");

      CustomerEntity newEntity = new CustomerEntity();
      newEntity.setFullName("New Customer");

      when(customerConverter.toCustomerEntity(newDTO)).thenReturn(newEntity);
      when(customerRepository.save(newEntity)).thenReturn(newEntity);

      customerService.saveOrUpdate(newDTO);

      assertThat(newEntity.getIsActive()).isEqualTo(1);
      verify(customerConverter).toCustomerEntity(newDTO);
      verify(customerRepository).save(newEntity);
    }

    @Test
    @DisplayName("Should update existing customer when ID is not null")
    void saveOrUpdate_existingCustomer_updatesEntity() {
      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      doNothing().when(customerConverter).updateEntity(customerDTO, customerEntity);
      when(customerRepository.save(customerEntity)).thenReturn(customerEntity);

      customerService.saveOrUpdate(customerDTO);

      verify(customerConverter).updateEntity(customerDTO, customerEntity);
      verify(customerRepository).save(customerEntity);
    }

    @Test
    @DisplayName("Should create new entity when ID exists but entity not found in DB")
    void saveOrUpdate_idNotInDB_createsNewEntity() {
      customerDTO.setId(999L);
      CustomerEntity freshEntity = new CustomerEntity();

      when(customerRepository.findById(999L)).thenReturn(Optional.empty());
      doNothing().when(customerConverter).updateEntity(eq(customerDTO), any(CustomerEntity.class));
      when(customerRepository.save(any(CustomerEntity.class))).thenReturn(freshEntity);

      customerService.saveOrUpdate(customerDTO);

      verify(customerRepository).save(any(CustomerEntity.class));
    }
  }

  @Nested
  @DisplayName("deleteCustomers()")
  class DeleteCustomersTests {

    @Test
    @DisplayName("Should soft delete customers by setting isActive to 0")
    void deleteCustomers_validIds_setsInactive() {
      CustomerEntity customer2 = new CustomerEntity();
      customer2.setId(2L);
      customer2.setIsActive(1);

      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      when(customerRepository.findById(2L)).thenReturn(Optional.of(customer2));
      when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      customerService.deleteCustomers(List.of(1L, 2L));

      assertThat(customerEntity.getIsActive()).isEqualTo(0);
      assertThat(customer2.getIsActive()).isEqualTo(0);
      verify(customerRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when customer to delete not found")
    void deleteCustomers_nonExistingId_throwsException() {
      when(customerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> customerService.deleteCustomers(List.of(999L)))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Customer not found");
    }
  }

  @Nested
  @DisplayName("loadStaffsByCustomerId()")
  class LoadStaffsByCustomerIdTests {

    @Test
    @DisplayName("Should return staff list with correct checked status")
    void loadStaffs_withAssignedStaff_returnsCorrectCheckedStatus() {
      UserEntity staff1 = new UserEntity(1L, "staff1", true, "ROLE_STAFF", "Staff 1", "0111111111");
      UserEntity staff2 = new UserEntity(2L, "staff2", true, "ROLE_STAFF", "Staff 2", "0222222222");
      customerEntity.setUserEntities(List.of(staff1));

      StaffResponseDTO checkedDTO = new StaffResponseDTO(1L, "staff1", "checked");
      StaffResponseDTO uncheckedDTO = new StaffResponseDTO(2L, "staff2", "");

      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      when(userRepository.findByActiveAndUserRole(true, SystemConstant.STAFF_ROLE))
          .thenReturn(List.of(staff1, staff2));
      when(userConverter.toStaffResponseDTO(eq(staff1), anySet())).thenReturn(checkedDTO);
      when(userConverter.toStaffResponseDTO(eq(staff2), anySet())).thenReturn(uncheckedDTO);

      ResponseDTO result = customerService.loadStaffsByCustomerId(1L);

      assertThat(result.getMessage()).isEqualTo("Load Staffs successfully");
      @SuppressWarnings("unchecked")
      List<StaffResponseDTO> staffList = (List<StaffResponseDTO>) result.getData();
      assertThat(staffList).hasSize(2);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void loadStaffs_customerNotFound_throwsException() {
      when(customerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> customerService.loadStaffsByCustomerId(999L))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Customer not found");
    }
  }

  @Nested
  @DisplayName("updateAssignmentCustomer()")
  class UpdateAssignmentCustomerTests {

    @Test
    @DisplayName("Should assign staff to customer successfully")
    void updateAssignment_validData_assignsStaff() {
      AssignmentCustomerDTO dto = new AssignmentCustomerDTO();
      dto.setCustomerId(1L);
      dto.setStaffIds(List.of(1L, 2L));

      UserEntity staff1 = new UserEntity(1L, "staff1", true, "ROLE_STAFF", "Staff 1", "0111111111");
      UserEntity staff2 = new UserEntity(2L, "staff2", true, "ROLE_STAFF", "Staff 2", "0222222222");

      when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
      when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(staff1, staff2));
      when(customerRepository.save(customerEntity)).thenReturn(customerEntity);

      customerService.updateAssignmentCustomer(dto);

      assertThat(customerEntity.getUserEntities()).hasSize(2);
      verify(customerRepository).save(customerEntity);
    }

    @Test
    @DisplayName("Should throw exception when customer not found for assignment")
    void updateAssignment_customerNotFound_throwsException() {
      AssignmentCustomerDTO dto = new AssignmentCustomerDTO();
      dto.setCustomerId(999L);
      dto.setStaffIds(List.of(1L));

      when(customerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> customerService.updateAssignmentCustomer(dto))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Customer not found");
    }
  }
}
