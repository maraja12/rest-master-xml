package com.fon.rest_master.service.impl;

import com.fon.rest_master.converter.impl.CompanyConverter;
import com.fon.rest_master.domain.Company;
import com.fon.rest_master.dto.CompanyDto;
import com.fon.rest_master.repository.CompanyRepository;
import com.fon.rest_master.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;
    private CompanyConverter companyConverter;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyConverter companyConverter) {
        this.companyRepository = companyRepository;
        this.companyConverter = companyConverter;
    }

    @Override
    public List<CompanyDto> getAll() {
        return companyRepository.findAll().stream()
                .map(entity -> companyConverter.toDto(entity))
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDto getById(int id) throws EntityNotFoundException {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            return companyConverter.toDto(company);
        }
        else{
            throw new EntityNotFoundException("Company with id = " + id + " is not found");
        }
    }

    @Override
    public CompanyDto save(CompanyDto companyDto){
        Company company = companyConverter.toEntity(companyDto);
        company = companyRepository.save(company);
        return companyConverter.toDto(company);
    }

    @Override
    public CompanyDto update(CompanyDto companyDto) throws EntityNotFoundException {
        Optional<Company> companyOptional = companyRepository.findById(companyDto.getPib());
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            company.setPib(companyDto.getPib());
            company.setName(companyDto.getName());
            company.setAddress(companyDto.getAddress());
            company.setEmail(companyDto.getEmail());
            company.setInvoices(companyDto.getInvoices());
            company = companyRepository.save(company);
            return companyConverter.toDto(company);
        }
        else{
            throw new EntityNotFoundException("Company with pib = " + companyDto.getPib() + " is not found");
        }
    }

    @Override
    public void delete(int id) throws EntityNotFoundException {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            companyRepository.delete(company);
        }
        else{
            throw new EntityNotFoundException("Company with id = " + id + " is not found");
        }
    }

    public void doesCompanyExist(int pib){
        Optional<Company> companyOpt = companyRepository.findById(pib);
        if (companyOpt.isEmpty()) {
            throw new EntityNotFoundException("Company with pib = " + pib + " is not found");
        }
    }

    @Override
    public Object getUnpaidInvoicesByCompany(int pib) throws EntityNotFoundException {
        doesCompanyExist(pib);
        return companyRepository.findUnpaidInvoicesByCompany(pib);
    }

    @Override
    public Object findInvoicesByCompanyPib(int pib) throws EntityNotFoundException {
        doesCompanyExist(pib);
        return companyRepository.findInvoicesByCompanyPib(pib);
    }

    @Override
    public Object sumUnpaidInvoicesByCompanyPib(int pib) throws EntityNotFoundException {
        doesCompanyExist(pib);
        return companyRepository.sumUnpaidInvoicesByCompany(pib);
    }

    @Override
    public Object findProjectsForCertainCompanyInvoice(int pib, Long invoiceId) throws EntityNotFoundException {
        doesCompanyExist(pib);
        Object list =  companyRepository.findProjectsForCertainCompanyInvoice(pib, invoiceId);
        if(list == null){
            throw new EntityNotFoundException("Invoice with id = " + invoiceId +
                    " for certain company = " + pib + " is not found");
        }
        return list;
    }

    @Override
    public Object findEmployeesForCertainCompanyInvoice(int pib, Long invoiceId) throws EntityNotFoundException {
        doesCompanyExist(pib);
        Object list =  companyRepository.findEmployeesForCertainCompanyInvoice(pib, invoiceId);
        if(list == null){
            throw new EntityNotFoundException("Invoice with id = " + invoiceId +
                    " for certain company = " + pib + " is not found");
        }
        return list;
    }

    @Override
    public Object findEmployeeRoleOnProjectForCompanyInvoiceItem(int pib, Long invoiceId, int seqNum) throws EntityNotFoundException {
        doesCompanyExist(pib);
        Object list =  companyRepository.findEmployeeRoleOnProjectForCompanyInvoiceItem(pib, invoiceId, seqNum);
        if(list == null){
            throw new EntityNotFoundException("Invoice with id = " + invoiceId +
                    " or seqNum = "+ seqNum + " for certain company = " + pib + " is not found");
        }
        return list;
    }
}
