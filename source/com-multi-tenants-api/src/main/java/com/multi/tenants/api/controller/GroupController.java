package com.multi.tenants.api.controller;

import com.multi.tenants.api.dto.ApiMessageDto;
import com.multi.tenants.api.dto.ErrorCode;
import com.multi.tenants.api.exception.BadRequestException;
import com.multi.tenants.api.exception.NotFoundException;
import com.multi.tenants.api.exception.UnauthorizationException;
import com.multi.tenants.api.form.group.CreateGroupForm;
import com.multi.tenants.api.form.group.UpdateGroupForm;
import com.multi.tenants.api.model.Group;
import com.multi.tenants.api.model.Permission;
import com.multi.tenants.api.repository.AccountRepository;
import com.multi.tenants.api.repository.GroupRepository;
import com.multi.tenants.api.repository.PermissionRepository;
import com.multi.tenants.api.dto.ResponseListDto;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/group")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class GroupController extends ABasicController{
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping(value = "/create", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateGroupForm createGroupForm, BindingResult bindingResult) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed create.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findFirstByName(createGroupForm.getName());
        if(group != null){
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("Group name is exist");
            return apiMessageDto;
        }
        group = new Group();
        group.setName(createGroupForm.getName());
        group.setDescription(createGroupForm.getDescription());
        group.setKind(createGroupForm.getKind());
        List<Permission> permissions = new ArrayList<>();
        for(int i=0;i< createGroupForm.getPermissions().length;i++){
            Permission permission = permissionRepository.findById(createGroupForm.getPermissions()[i]).orElse(null);
            if(permission != null){
                permissions.add(permission);
            }
        }
        group.setStatus(1);
        group.setPermissions(permissions);
        groupRepository.save(group);
        apiMessageDto.setMessage("Create group success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateGroupForm updateGroupForm, BindingResult bindingResult) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed update.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findById(updateGroupForm.getId()).orElseThrow(()
        -> new NotFoundException("Group name doesnt exist"));
        //check su ton tai cua group name khac khi dat ten.
        if (!updateGroupForm.getName().equals(group.getName())) {
            Group otherGroup = groupRepository.findFirstByName(updateGroupForm.getName());
            if(otherGroup != null) {
                throw new BadRequestException("Group name already exists");
            }
        }
        group.setName(updateGroupForm.getName());
        group.setDescription(updateGroupForm.getDescription());
        List<Permission> permissions = new ArrayList<>();
        if (updateGroupForm.getPermissions().length > 0) {
            List<Long> permissionIds = Arrays.asList(updateGroupForm.getPermissions());
            permissions = permissionRepository.findAllById(permissionIds);
        }
        group.setPermissions(permissions);
        groupRepository.save(group);
        apiMessageDto.setMessage("Update group success");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_V')")
    public ApiMessageDto<Group> get(@PathVariable("id")  Long id) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed to get.");
        }
        ApiMessageDto<Group> apiMessageDto = new ApiMessageDto<>();
        Group group =groupRepository.findById(id).orElse(null);
        apiMessageDto.setData(group);
        apiMessageDto.setMessage("Get group success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_L')")
    public ApiMessageDto<ResponseListDto<Group>> list(@RequestParam(required = true)  int kind, Pageable pageable) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed list group.");
        }

        ApiMessageDto<ResponseListDto<Group>> apiMessageDto = new ApiMessageDto<>();
        Page<Group> groups = groupRepository
                .findAllByKind(kind, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.DESC, "createdDate"))));
        ResponseListDto<Group> responseListDto = new ResponseListDto(groups.getContent() , groups.getTotalElements(), groups.getTotalPages());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("list group success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_D')")
    public ApiMessageDto<String> delete(@PathVariable Long id) {
        if (!isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed delete");
        }

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findById(id).orElse(null);
        if (group == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.GROUP_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Group not found");
            return apiMessageDto;
        }
        if (group.getIsSystemRole() || accountRepository.countByGroupId(id) > 0) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.GROUP_ERROR_CANT_DELETE);
            apiMessageDto.setMessage("Group can not delete");
            return apiMessageDto;
        }

        group.getPermissions().clear();
        groupRepository.delete(group);
        apiMessageDto.setMessage("Delete group successfully");
        return apiMessageDto;
    }
}
