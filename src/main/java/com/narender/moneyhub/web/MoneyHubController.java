package com.narender.moneyhub.web;

import com.narender.moneyhub.dto.*;
import com.narender.moneyhub.service.MoneyHubService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MoneyHubController {

    private final MoneyHubService service;

    public MoneyHubController(MoneyHubService service) {
        this.service = service;
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest req) {
        return AccountResponse.from(service.createAccount(req));
    }

    @GetMapping("/accounts")
    public List<AccountResponse> listAccounts() {
        return service.listAccounts().stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/accounts/{id}")
    public AccountResponse getAccount(@PathVariable Long id) {
        return AccountResponse.from(service.getAccount(id));
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transfer(@Valid @RequestBody CreateTransferRequest req) {
        return TransferResponse.from(service.transfer(req));
    }

    @GetMapping("/transfers")
    public List<TransferResponse> listTransfers() {
        return service.listTransfers().stream().map(TransferResponse::from).toList();
    }

    /** Unified everyday view: GET /api/overview?owner=Jane */
    @GetMapping("/overview")
    public OverviewResponse overview(@RequestParam String owner) {
        return service.overview(owner);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
}
