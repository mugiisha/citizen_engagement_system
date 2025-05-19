package org.amir.ces.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.amir.ces.dto.AgencyResponseDto;
import org.amir.ces.dto.CreateAgencyDto;
import org.amir.ces.exception.BadRequestException;
import org.amir.ces.exception.ResourceNotFoundException;
import org.amir.ces.model.Agency;
import org.amir.ces.model.Tag;
import org.amir.ces.repository.AgencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final TagService tagService;

    public Agency getAgencyById(Long id) {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
    }

    public List<AgencyResponseDto> getAllAgencies() {

        List<Agency> agencies = agencyRepository.findAll();
        List<AgencyResponseDto> agencyResponseDtos = new ArrayList<>();
        for (Agency agency : agencies) {
            AgencyResponseDto agencyResponseDto = convertToResponseDto(agency);
            agencyResponseDtos.add(agencyResponseDto);
        }
        return agencyResponseDtos;
    }

    public Agency createAgency(CreateAgencyDto createAgencyDto) {
        List<Tag> tags = new ArrayList<>();
        if (createAgencyDto.getTags() != null) {
            for (String tagName : createAgencyDto.getTags()) {
                Tag tag = tagService.getTagByName(tagName.toLowerCase());
                if (tag != null) {
                    tags.add(tag);
                }
            }
        }

        if(tags.isEmpty()) {
            throw new BadRequestException("An agency must have at least one tag");
        }

        Agency agency = Agency.builder()
                .name(createAgencyDto.getName())
                .description(createAgencyDto.getDescription())
                .tags(tags)
                .build();

        return agencyRepository.save(agency);
    }

    public Agency updateAgency(Long id, CreateAgencyDto createAgencyDto) {
        Agency agency = getAgencyById(id);
        agency.setName(createAgencyDto.getName());
        agency.setDescription(createAgencyDto.getDescription());

        if (createAgencyDto.getTags() != null) {
            List<Tag> tags = new ArrayList<>();
            for (String tagName : createAgencyDto.getTags()) {
                Tag tag = tagService.getTagByName(tagName.toLowerCase());
                if (tag != null && !tags.contains(tag)) {
                    tags.add(tag);
                }
            }
            if (tags.isEmpty()) {
                throw new BadRequestException("An agency must have at least one tag");
            }
            agency.setTags(tags);
        }
        return agencyRepository.save(agency);
    }

    public void deleteAgency(Long id) {
        Agency agency = getAgencyById(id);
        agencyRepository.delete(agency);
    }

    public Agency getAgencyByName(String name) {
        Agency agency = agencyRepository.findByName(name);
        if (agency == null) {
            throw new ResourceNotFoundException("Agency not found");
        }
        return agency;
    }

    @Transactional
    public void ensureAdministrationDepartmentExists() {
        if (!agencyRepository.existsByName("Administration")) {
            Agency adminAgency = Agency.builder()
                    .name("Administration")
                    .description("Administration Department")
                    .build();
            agencyRepository.save(adminAgency);
        }
    }

    public AgencyResponseDto convertToResponseDto(Agency agency) {
        return AgencyResponseDto.builder()
                .id(agency.getId())
                .name(agency.getName())
                .description(agency.getDescription())
                .createdAt(agency.getCreatedAt().toString())
                .build();
    }

}
