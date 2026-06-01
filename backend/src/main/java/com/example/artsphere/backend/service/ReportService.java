package com.example.artsphere.backend.service;

import com.example.artsphere.backend.model.*;
import com.example.artsphere.backend.repository.*;
import com.example.artsphere.pdf.ArtSpherePdfReportGenerator;
import com.example.artsphere.pdf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    private final ArtSpherePdfReportGenerator pdfGenerator = new ArtSpherePdfReportGenerator();

    // ==================== RAPORTY DLA KLIENTA (BUYER) ====================

    /**
     * Generuje raport zakupów klienta w zadanym zakresie dat.
     *
     * @param userId identyfikator klienta, którego zakupy mają być uwzględnione.
     * @param dateFrom data początkowa zakresu (włącznie).
     * @param dateTo data końcowa zakresu (włącznie).
     * @return zawartość PDF raportu jako tablica bajtów.
     */
    public byte[] generateClientPurchaseReport(Long userId, LocalDate dateFrom, LocalDate dateTo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        List<Order> orders = orderRepository.findByUserId(userId).stream()
                .filter(o -> o.getCreatedAt() != null)
                .filter(o -> !o.getCreatedAt().toLocalDate().isBefore(dateFrom))
                .filter(o -> !o.getCreatedAt().toLocalDate().isAfter(dateTo))
                .collect(Collectors.toList());

        List<UserPurchasesReportData.PurchaseRow> rows = orders.stream()
                .flatMap(order -> orderItemRepository.findByOrderId(order.getId()).stream())
                .map(item -> {
                    Artwork artwork = item.getArtwork();
                    return new UserPurchasesReportData.PurchaseRow(
                            item.getOrder().getCreatedAt(),
                            artwork != null ? artwork.getTitle() : "Nieznane",
                            artwork != null && artwork.getUser() != null ? artwork.getUser().getUsername() : "Nieznany",
                            artwork != null && artwork.getCategory() != null ? artwork.getCategory().getName() : null,
                            item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO
                    );
                })
                .collect(Collectors.toList());

        int totalPurchases = rows.size();
        BigDecimal totalAmount = rows.stream()
                .map(UserPurchasesReportData.PurchaseRow::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UserPurchasesReportData data = new UserPurchasesReportData(
                user.getUsername(),
                null,
                new DateRange(dateFrom, dateTo),
                null,
                rows,
                totalPurchases,
                totalAmount
        );

        return pdfGenerator.generateUserPurchasesReport(data);
    }

    /**
     * Generuje raport transakcji portfela klienta w zadanym zakresie dat.
     *
     * @param userId identyfikator klienta, którego transakcje mają być uwzględnione.
     * @param dateFrom data początkowa zakresu (włącznie).
     * @param dateTo data końcowa zakresu (włącznie).
     * @return zawartość PDF raportu jako tablica bajtów.
     */
    public byte[] generateClientTransactionsReport(Long userId, LocalDate dateFrom, LocalDate dateTo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        List<WalletTransaction> transactions = walletTransactionRepository
                .findByUserIdOrderByTransactionDateDesc(userId).stream()
                .filter(t -> t.getTransactionDate() != null)
                .filter(t -> !t.getTransactionDate().toLocalDate().isBefore(dateFrom))
                .filter(t -> !t.getTransactionDate().toLocalDate().isAfter(dateTo))
                .collect(Collectors.toList());

        List<ClientTransactionsReportData.TransactionRow> rows = transactions.stream()
                .map(tx -> new ClientTransactionsReportData.TransactionRow(
                        tx.getTransactionDate(),
                        tx.getTitle(),
                        tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO,
                        tx.isIncome()
                ))
                .collect(Collectors.toList());

        BigDecimal totalIncome = rows.stream()
                .filter(ClientTransactionsReportData.TransactionRow::income)
                .map(ClientTransactionsReportData.TransactionRow::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = rows.stream()
                .filter(r -> !r.income())
                .map(ClientTransactionsReportData.TransactionRow::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        ClientTransactionsReportData data = new ClientTransactionsReportData(
                user.getUsername(),
                new DateRange(dateFrom, dateTo),
                rows,
                totalIncome,
                totalExpenses,
                balance
        );

        return pdfGenerator.generateClientTransactionsReport(data);
    }

    // ==================== RAPORTY DLA SPRZEDAWCY (ARTIST) ====================

    /**
     * Generuje raport sprzedaży sprzedawcy, opcjonalnie filtrowany kategorią.
     *
     * @param sellerId identyfikator sprzedawcy.
     * @param dateFrom data początkowa zakresu (włącznie).
     * @param dateTo data końcowa zakresu (włącznie).
     * @param category opcjonalny filtr kategorii (nazwa kategorii).
     * @return zawartość PDF raportu jako tablica bajtów.
     */
    public byte[] generateSellerSalesReport(Long sellerId, LocalDate dateFrom, LocalDate dateTo, String category) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono sprzedawcy"));

        List<Artwork> sellerArtworks = artworkRepository.findByUserId(sellerId);

        List<Sale> filteredSales = saleRepository.findAll().stream()
                .filter(s -> s.getArtwork() != null && sellerArtworks.contains(s.getArtwork()))
                .filter(s -> s.getSoldAt() != null)
                .filter(s -> !s.getSoldAt().toLocalDate().isBefore(dateFrom))
                .filter(s -> !s.getSoldAt().toLocalDate().isAfter(dateTo))
                .filter(s -> category == null || category.isBlank()
                        || (s.getArtwork().getCategory() != null
                        && category.equalsIgnoreCase(s.getArtwork().getCategory().getName())))
                .collect(Collectors.toList());

        List<SellerSalesReportData.SellerSalesRow> rows = filteredSales.stream()
                .map(sale -> new SellerSalesReportData.SellerSalesRow(
                        sale.getSoldAt(),
                        sale.getArtwork().getTitle(),
                        sale.getArtwork().getCategory() != null ? sale.getArtwork().getCategory().getName() : null,
                        sale.getBuyer() != null ? sale.getBuyer().getUsername() : null,
                        sale.getPrice() != null ? sale.getPrice() : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());

        int totalSales = rows.size();
        BigDecimal totalRevenue = rows.stream()
                .map(SellerSalesReportData.SellerSalesRow::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averagePrice = totalSales > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalSales), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        SellerSalesReportData data = new SellerSalesReportData(
                seller.getUsername(),
                new DateRange(dateFrom, dateTo),
                category,
                rows,
                totalSales,
                totalRevenue,
                averagePrice
        );

        return pdfGenerator.generateSellerSalesReport(data);
    }

    // ==================== RAPORTY DLA ADMINA ====================

    /**
     * Generuje raport aktywności użytkowników dla administratora.
     *
     * @param dateFrom data początkowa zakresu (włącznie).
     * @param dateTo data końcowa zakresu (włącznie).
     * @param roleFilter opcjonalny filtr roli użytkownika.
     * @return zawartość PDF raportu jako tablica bajtów.
     */
    public byte[] generateAdminUserActivityReport(LocalDate dateFrom, LocalDate dateTo, String roleFilter) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null)
                .filter(u -> !u.getCreatedAt().toLocalDate().isBefore(dateFrom))
                .filter(u -> !u.getCreatedAt().toLocalDate().isAfter(dateTo))
                .filter(u -> roleFilter == null || roleFilter.isBlank() || roleFilter.equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());

        List<AdminUserActivityReportData.UserActivityRow> rows = users.stream()
                .map(u -> {
                    int orderCount = orderRepository.findByUserId(u.getId()).size();
                    return new AdminUserActivityReportData.UserActivityRow(
                            u.getUsername(),
                            u.getRole() != null ? u.getRole() : "BUYER",
                            u.getCreatedAt(),
                            u.getBalance() != null ? u.getBalance() : BigDecimal.ZERO,
                            orderCount,
                            u.getIsActive() != null && u.getIsActive()
                    );
                })
                .collect(Collectors.toList());

        int totalUsers = rows.size();
        int activeUsers = (int) rows.stream().filter(AdminUserActivityReportData.UserActivityRow::active).count();
        BigDecimal totalBalance = rows.stream()
                .map(AdminUserActivityReportData.UserActivityRow::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AdminUserActivityReportData data = new AdminUserActivityReportData(
                new DateRange(dateFrom, dateTo),
                roleFilter,
                rows,
                totalUsers,
                activeUsers,
                totalBalance
        );

        return pdfGenerator.generateAdminUserActivityReport(data);
    }

    /**
     * Generuje raport prowizji systemowych dla administratora.
     *
     * @param dateFrom data początkowa zakresu (włącznie).
     * @param dateTo data końcowa zakresu (włącznie).
     * @param category opcjonalny filtr kategorii (nazwa kategorii).
     * @return zawartość PDF raportu jako tablica bajtów.
     */
    public byte[] generateSystemCommissionReport(LocalDate dateFrom, LocalDate dateTo, String category) {
        List<Sale> allSales = saleRepository.findAll().stream()
                .filter(s -> s.getSoldAt() != null)
                .filter(s -> !s.getSoldAt().toLocalDate().isBefore(dateFrom))
                .filter(s -> !s.getSoldAt().toLocalDate().isAfter(dateTo))
                .filter(s -> category == null || category.isBlank()
                        || (s.getArtwork().getCategory() != null
                        && category.equalsIgnoreCase(s.getArtwork().getCategory().getName())))
                .collect(Collectors.toList());

        BigDecimal commissionRate = new BigDecimal("10.00");
        BigDecimal rateFactor = commissionRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        List<SystemCommissionReportData.CommissionRow> rows = allSales.stream()
                .map(sale -> {
                    BigDecimal gross = sale.getPrice() != null ? sale.getPrice() : BigDecimal.ZERO;
                    BigDecimal commission = gross.multiply(rateFactor).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal artistAmount = gross.subtract(commission);
                    return new SystemCommissionReportData.CommissionRow(
                            sale.getSoldAt(),
                            sale.getArtwork().getTitle(),
                            sale.getArtwork().getCategory() != null ? sale.getArtwork().getCategory().getName() : null,
                            gross,
                            commission,
                            artistAmount
                    );
                })
                .collect(Collectors.toList());

        BigDecimal totalSales = rows.stream()
                .map(SystemCommissionReportData.CommissionRow::grossAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCommission = rows.stream()
                .map(SystemCommissionReportData.CommissionRow::commissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SystemCommissionReportData data = new SystemCommissionReportData(
                new DateRange(dateFrom, dateTo),
                category,
                commissionRate,
                rows,
                totalSales,
                totalCommission
        );

        return pdfGenerator.generateSystemCommissionReport(data);
    }
}
