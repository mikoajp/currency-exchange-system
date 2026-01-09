package pl.aeh.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.aeh.currencyexchange.dto.TransactionDto;
import pl.aeh.currencyexchange.model.Transaction;
import pl.aeh.currencyexchange.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public Page<TransactionDto> getUserTransactionHistory(Long userId, Pageable pageable) {
        return transactionRepository.findAllByUserId(userId, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getAllUserTransactions(Long userId) {
        return transactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public pl.aeh.currencyexchange.dto.TransactionSummaryDto getUserSummary(Long userId) {
        long totalTransactions = transactionRepository.countByUserId(userId);
        long completedTransactions = transactionRepository.findAllByUserIdAndStatus(userId, pl.aeh.currencyexchange.model.TransactionStatus.COMPLETED).size();
        String mostTraded = transactionRepository.findMostTradedCurrency(userId);
        
        // Calculate volumes (simplified: just summing up 'fromAmount' per currency for completed transactions)
        // Note: For a real production system, this should be an optimized JPQL query.
        List<Transaction> allTransactions = transactionRepository.findAllByUserIdAndStatus(userId, pl.aeh.currencyexchange.model.TransactionStatus.COMPLETED);
        
        java.util.Map<String, java.math.BigDecimal> volumes = new java.util.HashMap<>();
        
        for (Transaction t : allTransactions) {
            String currency = t.getFromCurrency();
            if (currency != null) {
                volumes.merge(currency, t.getFromAmount(), java.math.BigDecimal::add);
            }
        }

        return pl.aeh.currencyexchange.dto.TransactionSummaryDto.builder()
                .totalTransactions(totalTransactions)
                .completedTransactions(completedTransactions)
                .mostTradedCurrency(mostTraded)
                .volumeByCurrency(volumes)
                .build();
    }

    private TransactionDto mapToDto(Transaction t) {
        return TransactionDto.builder()
                .id(t.getId())
                .type(t.getType())
                .status(t.getStatus())
                .fromCurrency(t.getFromCurrency())
                .toCurrency(t.getToCurrency())
                .fromAmount(t.getFromAmount())
                .toAmount(t.getToAmount())
                .rate(t.getExchangeRate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
