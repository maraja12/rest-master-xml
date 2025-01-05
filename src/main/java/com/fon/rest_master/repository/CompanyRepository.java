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
            "SUM(CAST(InvoiceDetails.value('(price_per_hour)[1]', 'DECIMAL(18,2)') AS DECIMAL(18,2))) AS total_price " +
            "FROM Company " +
            "CROSS APPLY invoices.nodes('/Invoices/Invoice[status=\"UNPAID\"]/InvoiceItems/InvoiceItem') AS InvoiceDetails(InvoiceDetails) " +
            "WHERE pib = :pib " +
            "GROUP BY name, pib, email",
            nativeQuery = true)
    Object sumUnpaidInvoicesByCompany(@Param("pib") int pib);
}
