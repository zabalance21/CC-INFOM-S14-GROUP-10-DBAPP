package Model.Entities;

import java.time.LocalDate;

public class ContractRenewalResult {
    public LocalDate oldStart;
    public LocalDate oldEnd;
    public ContractStatus oldStatus;

    public LocalDate newStart;
    public LocalDate newEnd;
    public ContractStatus newStatus;

    public ContractRenewalResult(LocalDate oldStart, LocalDate oldEnd, ContractStatus oldStatus,
                                 LocalDate newStart, LocalDate newEnd, ContractStatus newStatus) {
        this.oldStart = oldStart;
        this.oldEnd = oldEnd;
        this.oldStatus = oldStatus;
        this.newStart = newStart;
        this.newEnd = newEnd;
        this.newStatus = newStatus;
    }
}
