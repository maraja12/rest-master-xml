package com.fon.rest_master.repository;

import com.fon.rest_master.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

//    find all invoices for particular company and show company details like name, pib and email
    @Query(value = "SELECT name, pib, email, invoices " +
            "FROM Company " +
            "WHERE pib = :pib ",
            nativeQuery = true)
    Object findInvoicesByCompanyPib(@Param("pib") int companyPib);

//    find all unpaid invoices for particular company and show company details like name, pib and email
    @Query(value = "SELECT name, pib, email," +
            "invoices.query('/Invoices/Invoice[status=\"UNPAID\"]') " +
            "FROM Company " +
            "WHERE pib = :pib " +
            "AND invoices.exist('/Invoices/Invoice[status=\"UNPAID\"]') = 1",
            nativeQuery = true)
    Object findUnpaidInvoicesByCompany(@Param("pib") int pib);

//  sum all unpaid invoices for particular company and show company name, pib and email
    @Query(value = "SELECT name, pib, email, " +
            "SUM(CAST(InvoiceDetails.value('price_per_hour', 'DECIMAL(18,2)') AS DECIMAL(18,2))) AS total_price " +
            "FROM Company " +
            "CROSS APPLY invoices.nodes('/Invoices/Invoice[status=\"UNPAID\"]/InvoiceItems/InvoiceItem') AS InvoiceDetails(InvoiceDetails) " +
            "WHERE pib = :pib " +
            "GROUP BY name, pib, email",
            nativeQuery = true)
    Object sumUnpaidInvoicesByCompany(@Param("pib") int pib);

    //find name of projects for certain company invoice
    @Query(value =
            "select c.name as company_name, " +
            "string_agg(p.name, ', ') as project_names " +
            "from company c " +
            "cross apply invoices.nodes('/Invoices/Invoice') as T1(InvoicesList) " +
            "cross apply InvoicesList.nodes('InvoiceItems/InvoiceItem') as T2(InvoiceItems) " +
            "inner join project p on InvoiceItems.value('project_id', 'INT') = p.id " +
            "where pib = :pib and InvoicesList.value('@id', 'INT') = :invoice_id  " +
            "group by c.name;",
            nativeQuery = true)
    Object findProjectsForCertainCompanyInvoice(@Param("pib") int pib, @Param("invoice_id") Long invoiceId);

    //find employees id, name and surname for certain company invoice
    @Query(value =
            "select c.name as company_name, " +
            "string_agg(concat('id: ', e.id, ' ', e.name, ' ', e.surname), ', ') AS employees " +
            "from company c " +
            "cross apply invoices.nodes('/Invoices/Invoice') as T1(InvoicesList) " +
            "cross apply InvoicesList.nodes('InvoiceItems/InvoiceItem') as T2(InvoiceItems) " +
            "inner join employee e on InvoiceItems.value('employee_id', 'INT') = e.id " +
            "where pib = :pib and InvoicesList.value('@id', 'INT') = :invoice_id  " +
            "group by c.name;",
            nativeQuery = true)
    Object findEmployeesForCertainCompanyInvoice(@Param("pib") int pib, @Param("invoice_id") Long invoiceId);

    //find role of employee on project for certain company invoice (invoice item)
    @Query(value =
            "select c.name as company_name, " +
            "e.project_id as project_id, " +
            "e.employee_id as employee_id, " +
            "string_agg(e.role, ', ') AS employees " +
            "from company c " +
            "cross apply invoices.nodes('/Invoices/Invoice') as T1(InvoicesList) " +
            "cross apply InvoicesList.nodes('InvoiceItems/InvoiceItem') as T2(InvoiceItems) " +
            "inner join engagement e " +
            "on InvoiceItems.value('project_id', 'INT') = e.project_id " +
            "and InvoiceItems.value('employee_id', 'INT') = e.employee_id " +
            "where pib = :pib " +
            "and InvoicesList.value('@id', 'INT') = :invoice_id " +
            "and InvoiceItems.value('@seq_num', 'INT') = :seq_num " +
            "group by c.name, e.project_id, e.employee_id;",
            nativeQuery = true)
    Object findEmployeeRoleOnProjectForCompanyInvoiceItem(
            @Param("pib") int pib, @Param("invoice_id") Long invoiceId, @Param("seq_num") int seqNum);
}
