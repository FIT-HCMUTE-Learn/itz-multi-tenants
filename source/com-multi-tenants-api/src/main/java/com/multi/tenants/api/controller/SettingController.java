package com.multi.tenants.api.controller;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.dto.ApiMessageDto;
import com.multi.tenants.api.dto.ErrorCode;
import com.multi.tenants.api.dto.ResponseListDto;
import com.multi.tenants.api.dto.setting.SettingDto;
import com.multi.tenants.api.exception.BadRequestException;
import com.multi.tenants.api.exception.NotFoundException;
import com.multi.tenants.api.form.setting.CreateSettingForm;
import com.multi.tenants.api.form.setting.FindByGroupNameForm;
import com.multi.tenants.api.form.setting.FindByKeyNameForm;
import com.multi.tenants.api.form.setting.UpdateSettingForm;
import com.multi.tenants.api.mapper.SettingMapper;
import com.multi.tenants.api.model.Setting;
import com.multi.tenants.api.model.criteria.SettingCriteria;
import com.multi.tenants.api.repository.SettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/setting")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SettingController extends ABasicController {
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private SettingMapper settingMapper;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SET_V')")
    public ApiMessageDto<SettingDto> get(@PathVariable("id") Long id) {
        Setting setting = settingRepository.findById(id).orElse(null);
        if (setting == null) {
            throw new NotFoundException(ErrorCode.SETTING_ERROR_NOT_FOUND, "Not found setting");
        }

        ApiMessageDto<SettingDto> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(settingMapper.fromEntityToSettingAdminDto(setting));
        apiMessageDto.setMessage(("Get setting successfully"));
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SET_L')")
    public ApiMessageDto<ResponseListDto<List<SettingDto>>> list(SettingCriteria settingCriteria, Pageable pageable) {
        Page<Setting> settings = settingRepository.findAll(settingCriteria.getCriteria(), pageable);
        ResponseListDto<List<SettingDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(settingMapper.fromEntityListToSettingAdminDtoList(settings.getContent()));
        responseListObj.setTotalPages(settings.getTotalPages());
        responseListObj.setTotalElements(settings.getTotalElements());

        ApiMessageDto<ResponseListDto<List<SettingDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListObj);
        apiMessageDto.setMessage("List setting successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<SettingDto>>> autoComplete(SettingCriteria settingCriteria) {
        Pageable pageable = PageRequest.of(0, 10);
        settingCriteria.setStatus(ITzBaseConstant.STATUS_ACTIVE);
        Page<Setting> settings = settingRepository.findAll(settingCriteria.getCriteria(), pageable);
        ResponseListDto<List<SettingDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(settingMapper.fromEntityListToSettingDtoAutoCompleteList(settings.getContent()));
        responseListObj.setTotalPages(settings.getTotalPages());
        responseListObj.setTotalElements(settings.getTotalElements());

        ApiMessageDto<ResponseListDto<List<SettingDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListObj);
        apiMessageDto.setMessage("List setting successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SET_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateSettingForm createSettingForm, BindingResult bindingResult) {
        if (settingRepository.findFirstByGroupNameAndKeyName(createSettingForm.getGroupName(), createSettingForm.getKeyName()).isPresent()) {
            throw new BadRequestException(ErrorCode.SETTING_ERROR_EXISTED_GROUP_NAME_AND_KEY_NAME, "Group name and key name existed");
        }
        settingRepository.save(settingMapper.fromCreateSettingFormToEntity(createSettingForm));

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setMessage("Create setting successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SET_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateSettingForm updateSettingForm, BindingResult bindingResult) {
        Setting setting = settingRepository.findById(updateSettingForm.getId()).orElse(null);
        if (setting == null) {
            throw new BadRequestException(ErrorCode.SETTING_ERROR_NOT_FOUND, "Not found setting");
        }
        if ((!updateSettingForm.getGroupName().equals(setting.getGroupName()) || !updateSettingForm.getKeyName().equals(setting.getKeyName())) &&
                settingRepository.findFirstByGroupNameAndKeyName(updateSettingForm.getGroupName(), updateSettingForm.getKeyName()).isPresent()) {
            throw new BadRequestException(ErrorCode.SETTING_ERROR_EXISTED_GROUP_NAME_AND_KEY_NAME, "Group name and key name existed");
        }
        settingMapper.fromUpdateSettingFormToEntity(updateSettingForm, setting);
        settingRepository.save(setting);

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setMessage("Update setting successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/find-by-key", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<List<SettingDto>> findByKey(FindByKeyNameForm findByKeyNameForm) {
        List<Setting> settings = settingRepository.findByKeyNames(findByKeyNameForm.getKeyNames(), false);

        ApiMessageDto<List<SettingDto>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(settingMapper.fromEntityListToSettingDtoList(settings));
        apiMessageDto.setMessage("Find key by name successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/find-by-group", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<List<SettingDto>> findByGroup(FindByGroupNameForm findByGroupNameForm) {
        List<Setting> settings = settingRepository.findByGroupNames(findByGroupNameForm.getGroupNames(), false);

        ApiMessageDto<List<SettingDto>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(settingMapper.fromEntityListToSettingDtoList(settings));
        apiMessageDto.setMessage("Find key by group successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<List<SettingDto>> listSetting() {
        List<Setting> settings = settingRepository.findAllByIsSystem(false);
        ApiMessageDto<List<SettingDto>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(settingMapper.fromEntityToSettingDtoPublicList(settings));
        apiMessageDto.setMessage("List setting successfully");
        return apiMessageDto;
    }
}
